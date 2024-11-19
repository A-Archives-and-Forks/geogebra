package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public class CancelGCDInFraction implements SimplifyNode {
	private final ExpressionNode node;
	private final Kernel kernel;
	private final ExpressionNode numerator;
	private final ExpressionNode denominator;

	public CancelGCDInFraction(ExpressionNode node, Kernel kernel) {
		this.node = node;
		this.kernel = kernel;
		numerator = node.getLeftTree();
		denominator = node.getRightTree();
	}

	@Override
	public ExpressionNode simplify() {
		if (isNumeratorCancelableByDenominator()) {
			ExpressionValue canceled = denominator.getLeft();
			if (numerator.isOperation(Operation.MULTIPLY))  {
				double evalCanceled = canceled.evaluateDouble();
				double evalLeft = numerator.getLeft().evaluateDouble();
				double evalRight = numerator.getRight().evaluateDouble();
				long gcdLeft = Kernel.gcd((long) evalCanceled, (long) evalLeft);
				long gcdRight = Kernel.gcd((long) evalCanceled, (long) evalRight);
				if (DoubleUtil.isEqual(gcdLeft, evalCanceled)) {
					return numerator.getRightTree();
				} else if (gcdLeft != 1) {
					double v = evalLeft / gcdLeft;
					double canceledDominator = denominator.divide(gcdLeft).evaluateDouble();
					return new ExpressionNode(kernel, new MyDouble(kernel, v).wrap()
							.multiplyR(numerator.getRightTree()),
							Operation.DIVIDE, new MyDouble(kernel, canceledDominator)
					);
				} else if (DoubleUtil.isEqual(gcdRight, evalCanceled)) {
					if (DoubleUtil.isEqual(evalCanceled, evalRight)) {
						return numerator.getLeftTree();
					} else {
						double v = evalRight / evalCanceled;
						return new ExpressionNode(kernel, new MyDouble(kernel, v),
								numerator.getOperation(), numerator.getLeftTree()
						);
					}
				}
			}
		}
		return node;
	}

	private boolean isNumeratorCancelableByDenominator() {
		return !numerator.isLeaf() && denominator.isLeaf();
	}
}
