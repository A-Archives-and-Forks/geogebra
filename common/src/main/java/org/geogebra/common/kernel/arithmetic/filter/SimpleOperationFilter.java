package org.geogebra.common.kernel.arithmetic.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

public class SimpleOperationFilter implements ExpressionFilter {
	private final List<Operation> filteredOperations = new ArrayList<>();
	public SimpleOperationFilter(Operation... operations) {
		filteredOperations.addAll(Arrays.asList(operations));
	}

	@Override
	public boolean isAllowed(ExpressionNode node) {
		return !node.inspect(v -> hasFilteredOperations(v));
	}

	@Override
	public boolean isAllowed(Operation operation) {
		return !filteredOperations.contains(operation);
	}

	private boolean hasFilteredOperations(ExpressionValue v) {
		for (Operation operation : filteredOperations) {
			if (v.isOperation(operation)) {
				return true;
			}
		}
		return false;
	}
}
