package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public class CancelGCDInFraction implements SimplifyNode {
	private final Kernel kernel;
	private ExpressionNode numerator;
	private ExpressionNode denominator;

	public CancelGCDInFraction(Kernel kernel) {
		this.kernel = kernel;
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
			return doCancel(numerator, denominator);
		}
		if (isCancelable(denominator, numerator)) {
			return doCancel(denominator, numerator);
		}
		return node;
	}

	private boolean isCancelable(ExpressionNode node1, ExpressionNode node2) {
		return !node1.isLeaf() && node2.isLeaf();
	}

	private ExpressionNode doCancel(ExpressionNode node1, ExpressionNode node2) {
		ExpressionValue canceled = node2.getLeft();
		double evalCanceled = canceled.evaluateDouble();
		if (node1.isOperation(Operation.MULTIPLY))  {
			double evalLeft = node1.getLeft().evaluateDouble();
			double evalRight = node1.getRight().evaluateDouble();
			long gcdLeft = Kernel.gcd((long) evalCanceled, (long) evalLeft);
			long gcdRight = Kernel.gcd((long) evalCanceled, (long) evalRight);
			if (DoubleUtil.isEqual(gcdLeft, evalCanceled)) {
				if (node1 == numerator) {
					return node1.getRightTree();
				} else {
					return new ExpressionNode(kernel,
							new MyDouble(kernel, 1), Operation.DIVIDE, node1.getRight());
				}
			} else if (gcdLeft != 1) {
				double v = evalLeft / gcdLeft;
				double canceledDominator = node2.divide(gcdLeft).evaluateDouble();
				return new ExpressionNode(kernel, new MyDouble(kernel, v).wrap()
						.multiplyR(node1.getRightTree()),
						Operation.DIVIDE, new MyDouble(kernel, canceledDominator)
				);
			} else if (DoubleUtil.isEqual(gcdRight, evalCanceled)) {
				if (DoubleUtil.isEqual(evalCanceled, evalRight)) {
					return node1.getLeftTree();
				} else {
					double v = evalRight / evalCanceled;
					return new ExpressionNode(kernel, new MyDouble(kernel, v),
							node1.getOperation(), node1.getLeftTree()
					);
				}
			}
		} else if (node1.isOperation(Operation.DIVIDE)) {
			ExpressionNode expressionNode = new ExpressionNode(kernel,
					node1.getLeftTree(),
					Operation.DIVIDE,
					new MyDouble(kernel, node1.getRight().evaluateDouble() / evalCanceled
					));
			return expressionNode;
		}
		return node1.divide(canceled);
	}
}
