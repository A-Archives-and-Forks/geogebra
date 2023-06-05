package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

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
}
