package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.plugin.Operation;

/**
 * Filters entire operation.
 * For specific arguments only, see {@link OperationArgumentFilter}
 */
public interface ExpressionFilter {

	/**
	 *
	 * @param node to check
	 * @return if node is allowed
	 */
	boolean isAllowed(ExpressionNode node);

	/**
	 * @param operation to check
	 * @return if allowed.
	 */
	boolean isAllowed(Operation operation);
}
