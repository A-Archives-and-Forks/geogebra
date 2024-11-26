package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

public interface SimplifyNode {
	boolean isAccepted(ExpressionNode node);
	ExpressionNode apply(ExpressionNode node);

	default String name() {

		String className = getClass().toString();
		String[] tags = className.split("\\.");
		return tags.length > 0 ? tags[tags.length - 1] : "-";
	}
}
