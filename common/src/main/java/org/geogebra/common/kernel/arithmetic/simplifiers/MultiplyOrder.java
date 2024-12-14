package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.SimplifyUtils;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;

public class MultiplyOrder implements SimplifyNode {
	private final SimplifyUtils utils;

	public MultiplyOrder(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		return node.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev.isOperation(Operation.MULTIPLY)) {
					if (!SimplifyUtils.isPlusMinusNode(ev.wrap().getRightTree())) {
						return utils.flipTrees(ev.wrap());
					}
				}
				return ev;
			}
		}).wrap();
	}
}
