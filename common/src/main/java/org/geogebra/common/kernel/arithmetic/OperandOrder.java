package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyNode;
import org.geogebra.common.plugin.Operation;

public class OperandOrder implements SimplifyNode {
	private final SimplifyUtils utils;

	public OperandOrder(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (node.isOperation(Operation.DIVIDE)) {
			return utils.div(apply(node.getLeftTree()), apply(node.getRightTree()));
		}
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		double v = left.evaluateDouble();
		if (left.isLeaf() && v < 0
		&& node.isOperation(Operation.PLUS) && right.evaluateDouble() >=0) {
			return utils.newNode(right, Operation.MINUS, utils.newDouble(-v));
		}
		return node;
	}
}
