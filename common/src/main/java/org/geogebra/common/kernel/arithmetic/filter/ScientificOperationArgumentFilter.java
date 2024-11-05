package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

public enum ScientificOperationArgumentFilter implements ExpressionFilter {

	INSTANCE;

	@Override
	public boolean isAllowed(ValidExpression expression) {
		return !expression.isExpressionNode() || hasNoLists((ExpressionNode) expression);
	}

	private boolean hasNoLists(ExpressionNode expression) {
		return !expression.getLeft().evaluatesToList() && !expression.getLeft().evaluatesToList();
	}
}
