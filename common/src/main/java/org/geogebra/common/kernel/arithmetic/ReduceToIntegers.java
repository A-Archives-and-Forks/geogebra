package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyNode;
import org.geogebra.common.plugin.Operation;

public class ReduceToIntegers implements SimplifyNode {

	private final SimplifyUtils utils;

	public ReduceToIntegers(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		return node.traverse(new Traversing() {
			ExpressionNode parent = null;
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				ExpressionNode node = ev.wrap();
				if (ev.isOperation(Operation.PLUS)) {
					if (node.getLeft().evaluateDouble() == 0) {
						return node.getRight();
					}
					if (node.getRight().evaluateDouble() == 0) {
						return node.getLeft();
					}
				} else if (ev.isOperation(Operation.MINUS)) {
					if (node.getLeft().evaluateDouble() == 0) {
						return utils.makeNegative(node.getRightTree());
					}
					if (node.getRight().evaluateDouble() == 0) {
						return node.getLeft();
					}
				} else if (utils.isDivNode(node)) {
					double numeratorVal = node.getLeft().evaluateDouble();
					double denominatorVal = node.getRight().evaluateDouble();
					if (numeratorVal == 0 && denominatorVal != 0)  {
						return utils.newDouble(0);
					}
					if (numeratorVal != 0 && denominatorVal == 0)  {
						return utils.infinity();
					}if (numeratorVal == 0 && denominatorVal == 0)  {
						return utils.negativeInfinity();
					}
				}
				double v = ev.evaluateDouble();
				if (Math.round(v) == v && v != -1) {
					return utils.newDouble(v);
				}

				return ev;
			}

			private boolean minusOneMultiply(ExpressionNode node) {
				if (node == null) {
					return false;
				}
				return node.isOperation(Operation.MULTIPLY) && node.getLeft().evaluateDouble() == -1;
			}
		}).wrap();
	}
}
