package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.Surds.getResolution;
import static org.geogebra.common.util.DoubleUtil.isInteger;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;

public class SimplifyMultiplication implements SimplifyNode {

	private final Kernel kernel;

	public SimplifyMultiplication(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (!isAccepted(node)) {
			return node;
		}
		ExpressionNode tagA = node.getLeftTree();
		ExpressionValue a1 = tagA.getLeft();
		ExpressionValue a2 = tagA.getRight();
		ExpressionNode tagB = node.getRightTree();
		ExpressionValue b1 = tagB.getLeft();
		ExpressionValue b2 = tagB.getRight();
		int signA2 = tagA.isOperation(Operation.MINUS) ? -1 : 1;
		int signB2 = tagB.isOperation(Operation.MINUS) ? -1 : 1;
		ExpressionNode a1b1 = multiplyValues(a1, b1, 1).wrap();
		ExpressionNode a1b2 = multiplyValues(a1, b2, signB2).wrap();
		ExpressionNode a2b1 = multiplyValues(a2, b1, signA2).wrap();
		ExpressionNode a2b2 = multiplyValues(a2, b2, signA2 * signB2).wrap();
		return a1b1.plus(a1b2).plus(a2b2).plus(a2b1);
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node.isOperation(Operation.MULTIPLY) && isTag(node.getLeftTree())
				&& isTag(node.getRightTree());
	}

	private boolean isTag(ExpressionNode node) {
		return node.getLeftTree() != null && node.getRightTree() != null
			&& (node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS));
	}

	private ExpressionValue multiplyValues(ExpressionValue a, ExpressionValue b, int sign) {
		boolean aIsNumber = a instanceof NumberValue;
		boolean bIsNumber = b instanceof NumberValue;
		ExpressionValue product = null;
		if (aIsNumber && bIsNumber) {
			double v = a.evaluateDouble() * b.evaluateDouble() * sign;
			product = new MyDouble(kernel, v);
		} else if (aIsNumber) {
			product = b.wrap().multiplyR(a.evaluateDouble() * sign);
		} else if (bIsNumber) {
			product = a.wrap().multiplyR(b.evaluateDouble() * sign);
		} else if (a.isOperation(Operation.SQRT) && b.isOperation(Operation.SQRT)) {
			double v = a.wrap().getLeftTree().evaluateDouble()
					* b.wrap().getLeftTree().evaluateDouble();
			ExpressionNode sqrtNode = new ExpressionNode(kernel, new MyDouble(kernel, v),
					Operation.SQRT, null);
			ExpressionNode expr = sqrtNode;
			ExpressionValue resolution = getResolution(sqrtNode, kernel);
			product =
					(resolution != null ? resolution : expr).wrap().multiplyR(sign);
		} else {
			product = a.wrap().multiply(b).multiplyR(sign);
		}

		double v1 = product.evaluateDouble();
		if (isInteger(v1)) {
			return new MyDouble(kernel, v1);
		}
		return product;
	}
}
