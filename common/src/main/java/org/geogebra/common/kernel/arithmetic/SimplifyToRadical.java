package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;

public class SimplifyToRadical implements SimplifyNode {
	private final Kernel kernel;

	public SimplifyToRadical(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public ExpressionNode simplify(ExpressionNode node) {
		if (!node.isOperation(Operation.DIVIDE)) {
			return node;
		}
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
