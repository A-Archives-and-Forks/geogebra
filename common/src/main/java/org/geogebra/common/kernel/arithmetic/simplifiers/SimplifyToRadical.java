package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Surds;
import org.geogebra.common.plugin.Operation;

public class SimplifyToRadical implements SimplifyNode {
	private final Kernel kernel;

	public SimplifyToRadical(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node.isOperation(Operation.DIVIDE);
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		ExpressionNode numerator = node.getLeftTree();
		if (numerator.isOperation(Operation.SQRT)) {
			ExpressionValue reducedSqrt = Surds.getResolution(numerator, kernel);
			if (reducedSqrt != null) {
				return new ExpressionNode(kernel, reducedSqrt, Operation.DIVIDE,
						node.getRightTree());
			}
		} else if (numerator.isOperation(Operation.MULTIPLY)) {
			ExpressionNode rightTree = numerator.getRightTree();
			ExpressionValue reducedSqrt = Surds.getResolution(rightTree, kernel);
			if (reducedSqrt != null) {
				ExpressionNode constantProduct =
						numerator.getLeftTree().multiplyR(reducedSqrt.wrap().getLeftTree());
				return new ExpressionNode(kernel,
						reducedSqrt.wrap().getRightTree().multiplyR(
								constantProduct.unwrap().evaluateDouble()),
						Operation.DIVIDE,
						node.getRightTree());
			}
		}

		return node;
	}
}
