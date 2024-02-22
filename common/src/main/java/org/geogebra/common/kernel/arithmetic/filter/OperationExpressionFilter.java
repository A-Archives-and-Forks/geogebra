package org.geogebra.common.kernel.arithmetic.filter;

import java.util.Set;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.plugin.Operation;

public class OperationExpressionFilter implements ExpressionFilter {

	private final Set<Operation> filteredOperations;

	public OperationExpressionFilter(Operation... filteredOperations) {
		this.filteredOperations = Set.of(filteredOperations);
	}

	private boolean isFilteredOperation(ExpressionValue expressionValue) {
		return filteredOperations.stream().anyMatch(expressionValue::isOperation);
	}

	@Override
	public boolean isAllowed(ValidExpression expression) {
		boolean containsFilteredOperations = expression.inspect(expressionValue -> isFilteredOperation(expressionValue));
		return !containsFilteredOperations;
	}
}
