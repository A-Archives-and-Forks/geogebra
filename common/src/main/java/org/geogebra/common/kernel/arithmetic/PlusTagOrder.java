package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.SimplifyUtils.isIntegerValue;

import org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyNode;
import org.geogebra.common.plugin.Operation;

public class PlusTagOrder implements SimplifyNode {
	private final SimplifyUtils utils;

	public PlusTagOrder(SimplifyUtils utils) {
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
				if (ev.isOperation(Operation.PLUS)) {
					ExpressionNode wrap = ev.wrap();
					double valLeft = wrap.getLeft().evaluateDouble();
					double valRight = wrap.getRight().evaluateDouble();
					if (valLeft < 0 && valRight > 0) {
						return utils.flipTrees(wrap);
					}
				} else if (ev.isOperation(Operation.MINUS)) {
					ExpressionNode wrap = ev.wrap();
					double valLeft = wrap.getLeft().evaluateDouble();
					double valRight = wrap.getRight().evaluateDouble();
					if (valLeft < 0 && valRight < 0 && isIntegerValue(valLeft)) {
						return utils.newNode(utils.mulByMinusOne(wrap.getRightTree()),
								Operation.PLUS, utils.newDouble(valLeft));
					}

				} else if (ev.isOperation(Operation.MULTIPLY)) {
					ExpressionNode wrap = ev.wrap();
					double valLeft = wrap.getLeft().evaluateDouble();
					double valRight = wrap.getRight().evaluateDouble();
					if (isIntegerValue(valRight) && !isIntegerValue(valLeft)) {
						ExpressionValue lft = process(wrap.getLeftTree());

						return utils.newNode(wrap.getRight(), wrap.getOperation(), lft);
					}
				}
				return ev;
			}
		}).wrap();
	}
}
