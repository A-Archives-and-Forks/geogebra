package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.util.DoubleUtil.isInteger;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.SimplifyUtils;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public class CancelGCDInFraction implements SimplifyNode {

	private int multiplier;

	private enum NodeType {
		INVALID,
		SIMPLE_FRACTION,
		MULTIPLIED_FRACTION,
		MULTIPLIED_NUMERATOR, NEGATIVE_MULTIPLIED_NUMERATOR,
	}

	private final SimplifyUtils utils;
	private final Kernel kernel;
	private NodeType nodeType = NodeType.INVALID;
	private ExpressionNode numerator;
	private ExpressionNode denominator;

	public CancelGCDInFraction(SimplifyUtils utils) {
		this.utils = utils;
		kernel = utils.kernel;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		if (node.isOperation(Operation.DIVIDE)) {
			ExpressionNode leftTree = node.getLeftTree();
			multiplier = utils.getLeftMultiplier(leftTree);
			if (multiplier == -1 && leftTree.getRightTree().isOperation(Operation.MULTIPLY)) {
				multiplier *= utils.getLeftMultiplier(leftTree.getRightTree());
				nodeType = NodeType.NEGATIVE_MULTIPLIED_NUMERATOR;
			} else {
				nodeType = isTrivialMultiplier() ? NodeType.SIMPLE_FRACTION : NodeType.MULTIPLIED_NUMERATOR;
			}
		}

		if (node.isOperation(Operation.MULTIPLY)
				&& node.getRightTree().isOperation(Operation.DIVIDE)) {
			multiplier = utils.getLeftMultiplier(node);
			nodeType = isTrivialMultiplier() ? NodeType.SIMPLE_FRACTION
					:NodeType.MULTIPLIED_FRACTION;
		}
		return nodeType != NodeType.INVALID;
	}

	private boolean isTrivialMultiplier() {
		return Math.abs(multiplier) == 1;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		switch (nodeType) {
		case MULTIPLIED_FRACTION:
			return applyForMultipliedFraction(node);
		case MULTIPLIED_NUMERATOR:
			return applyForMultipliedNumerator(node);
		case NEGATIVE_MULTIPLIED_NUMERATOR:
			return applyForNegativeMultipliedNumerator(node);
		case SIMPLE_FRACTION:
			return applyForSimpleFraction(node);
		case INVALID:
		default:
			return node;
		}
	}

	private ExpressionNode applyForNegativeMultipliedNumerator(ExpressionNode node) {
		ExpressionNode numerator = node.getLeftTree();
		ExpressionNode node1 = utils.newNode(
				utils.newNode(
						utils.newDouble(multiplier),
						Operation.MULTIPLY,
						numerator.getRightTree().getRightTree()
				)
				, Operation.DIVIDE, node.getRightTree());
		return applyForMultipliedNumerator(node1);
	}

	private ExpressionNode applyForMultipliedNumerator(ExpressionNode node) {
		int n = multiplier;
		int m = (int) node.getRightTree().evaluateDouble();
		long gcd = Kernel.gcd(n, m);
		long newMul = n / gcd;
		long newDenom = m / gcd;
		return utils.div(utils.multiplyR(node.getLeftTree().getRightTree(), newMul),
				utils.newDouble(newDenom).wrap());
	}

	private ExpressionNode applyForMultipliedFraction(ExpressionNode node) {
		int n = utils.getLeftMultiplier(node);
		int m = (int) node.getRightTree().getRightTree().evaluateDouble();
		long gcd = Kernel.gcd(n, m);
		long newMul = n / gcd;
		long newDenom = m / gcd;
		return utils.div(utils.multiplyR(node.getRightTree().getLeftTree(), newMul),
				utils.newDouble(newDenom).wrap());
	}

	private ExpressionNode applyForSimpleFraction(ExpressionNode node) {
		numerator = node.getLeftTree();
		denominator = node.getRightTree();

		return getCanceledFraction(node, numerator, denominator);
	}

	private ExpressionNode getCanceledFraction(ExpressionNode node, ExpressionNode node1,
			ExpressionNode node2) {
		ExpressionNode canceledFraction = null;
		if (isCancelable(node1, node2)) {
			canceledFraction = doCancel(node1, node2);
		} else if (isCancelable(node2, node1)) {
			canceledFraction = doCancel(node2, node1);
		}
		return canceledFraction != null ? canceledFraction : node;
	}

	private boolean isCancelable(ExpressionNode node1, ExpressionNode node2) {
		return !node1.isLeaf() && node2.isLeaf();
	}

	private ExpressionNode doCancel(ExpressionNode node1, ExpressionNode node2) {
		ExpressionValue canceled = node2.getLeft();
		double evalCanceled = canceled.evaluateDouble();
		if (node1.isOperation(Operation.MULTIPLY)) {
			double evalLeft = node1.getLeft().evaluateDouble();
			double evalRight = node1.getRight().evaluateDouble();
			if (isInteger(evalLeft)) {
				return cancel(node1, node2, evalCanceled, evalLeft);
			}

			if (isInteger(evalRight)) {
				return cancel(node1, node2, evalCanceled, evalRight);
			}
		} else if (node1.isOperation(Operation.DIVIDE)) {
			ExpressionNode expressionNode = utils.div(node1.getLeftTree(),
					node1.getRight().evaluateDouble() / evalCanceled);
			return expressionNode;
		}
		return node1.divide(canceled);
	}

	private ExpressionNode cancel(ExpressionNode node1, ExpressionNode node2,
			double evalCanceled, double eval) {
		long gcd = Kernel.gcd((long) evalCanceled, (long) eval);
		if (DoubleUtil.isEqual(gcd, -1)) {
			return null;
		}
		if (DoubleUtil.isEqual(gcd, evalCanceled)) {
			if (node1 == numerator) {
				if (node1.getLeft().evaluateDouble() == evalCanceled) {
					return node1.getRightTree();
				}

				double rightValue = node1.getRightTree().evaluateDouble();
				if ((int) rightValue % (int) evalCanceled == 0) {
					double div = rightValue / evalCanceled;
					return DoubleUtil.isEqual(div, 1, Kernel.MAX_PRECISION)
							? node1.getLeftTree()
							: new ExpressionNode(kernel,
							new MyDouble(kernel, div), Operation.MULTIPLY, node1.getLeftTree());
				}
			} else {
				return new ExpressionNode(kernel,
						new MyDouble(kernel, 1), Operation.DIVIDE, node1.getRight());
			}
		} else if (gcd != 1) {
			double v = eval / gcd;
			double canceledDominator = node2.divide(gcd).evaluateDouble();
			ExpressionValue multRArg = node1.getLeft().isLeaf() ? node1.getRight()
					: node1.getLeft();
			return new ExpressionNode(kernel, new MyDouble(kernel, v).wrap()
					.multiplyR(multRArg),
					Operation.DIVIDE, new MyDouble(kernel, canceledDominator)
			);
		} else {
			double v = eval / evalCanceled;
			return new ExpressionNode(kernel, new MyDouble(kernel, v),
					node1.getOperation(), node1.getLeftTree());
		}
		return null;
	}
}
