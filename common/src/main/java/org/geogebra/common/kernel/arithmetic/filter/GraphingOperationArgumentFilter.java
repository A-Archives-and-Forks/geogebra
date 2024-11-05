package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * OperationArgumentFilter for the Graphing app.
 */
public enum GraphingOperationArgumentFilter implements ExpressionFilter {

	INSTANCE;

	@Override
	public boolean isAllowed(ValidExpression expression) {
		if (!expression.isExpressionNode()) {
			return true;
		}
		ExpressionNode node = (ExpressionNode) expression;
		switch (node.getOperation()) {
		case ABS:
			return allowAbs(node.getLeft());
		case MULTIPLY:
			return !isInnerProduct(node.getLeft(), node.getRight());
		case VECTORPRODUCT:
			return false;
		default:
			return true;
		}
	}

	private boolean isInnerProduct(ExpressionValue left,
			ExpressionValue right) {
		return left.evaluatesToNDVector() || right.evaluatesToNDVector();
	}

	private boolean allowAbs(ExpressionValue left) {
		return left.evaluatesToNumber(true);
	}
}
