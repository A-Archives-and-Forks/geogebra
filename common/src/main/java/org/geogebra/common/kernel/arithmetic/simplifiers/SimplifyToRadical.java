package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.SimplifyUtils;
import org.geogebra.common.plugin.Operation;

public class SimplifyToRadical implements SimplifyNode {

	private final SimplifyUtils utils;

	public SimplifyToRadical(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node.isOperation(Operation.DIVIDE);
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		ExpressionNode numerator = node.getLeftTree();
		if (utils.isSqrt(numerator)) {
			ExpressionValue reducedSqrt = utils.getSurds(numerator);
			if (reducedSqrt != null) {
				return utils.div(reducedSqrt, node.getRightTree());
			}
		} else if (numerator.isOperation(Operation.MULTIPLY)) {
			ExpressionNode rightTree = numerator.getRightTree();
			ExpressionValue reducedSqrt = utils.getSurdsOrSame(rightTree);
			if (reducedSqrt != rightTree) {
				ExpressionNode constantProduct =
						numerator.getLeftTree().multiplyR(reducedSqrt.wrap().getLeftTree());
				return utils.newNode(
						reducedSqrt.wrap().getRightTree().multiplyR(
								constantProduct.unwrap().evaluateDouble()),
						Operation.DIVIDE,
						node.getRightTree());
			}
		}

		return node;
	}
}
