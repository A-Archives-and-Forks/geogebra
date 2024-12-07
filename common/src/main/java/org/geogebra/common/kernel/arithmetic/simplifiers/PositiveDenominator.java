package org.geogebra.common.kernel.arithmetic.simplifiers;


import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.SimplifyUtils;
import org.geogebra.common.plugin.Operation;

public class PositiveDenominator implements SimplifyNode{

	private final SimplifyUtils utils;

	public PositiveDenominator(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node.isOperation(Operation.DIVIDE);
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		ExpressionNode numerator = node.getLeftTree();
		ExpressionNode denominator = node.getRightTree();
		double v = denominator.evaluateDouble();
		if (denominator.isLeaf() && v < 0) {
			return utils.newNode(utils.makeNegative(numerator),
					Operation.DIVIDE,
					utils.newDouble(-v));
		}
		return node;
	}



}
