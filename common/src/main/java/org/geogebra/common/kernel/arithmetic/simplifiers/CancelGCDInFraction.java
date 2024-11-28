package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.util.DoubleUtil.isInteger;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.SimplifyUtils;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public class CancelGCDInFraction implements SimplifyNode {
	private final SimplifyUtils utils;
	private final Kernel kernel;
	private ExpressionNode numerator;
	private ExpressionNode denominator;

	public CancelGCDInFraction(SimplifyUtils utils) {
		this.utils = utils;
		kernel = utils.kernel;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node.inspect(new Inspecting() {
			@Override
			public boolean check(ExpressionValue v) {
				return v.isOperation(Operation.DIVIDE);
			}
		});
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		numerator = node.getLeftTree();
		denominator = node.getRightTree();

		if (isCancelable(numerator, denominator)) {
			ExpressionNode canceled = doCancel(numerator, denominator);
			return canceled != null ? canceled: node;
		}
		if (isCancelable(denominator, numerator)) {
			ExpressionNode canceled = doCancel(denominator, numerator);
			return canceled != null ? canceled : node;
		}
		return node;
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
