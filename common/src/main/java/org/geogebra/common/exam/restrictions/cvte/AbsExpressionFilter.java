package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.plugin.Operation;

public class AbsExpressionFilter implements ExpressionFilter {

	@Override
	public boolean isAllowed(ValidExpression expression) {
		if (expression.isOperation(Operation.ABS) && expression instanceof ExpressionNode) {
			ExpressionNode node = (ExpressionNode) expression;
			return node.getLeft().evaluatesToNumber(false);
		}
		return false;
	}
}
