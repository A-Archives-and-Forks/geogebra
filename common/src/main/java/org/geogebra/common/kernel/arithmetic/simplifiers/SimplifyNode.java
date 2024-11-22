package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

public interface SimplifyNode {
	boolean isAccepted(ExpressionNode node);
	ExpressionNode apply(ExpressionNode node);
}
