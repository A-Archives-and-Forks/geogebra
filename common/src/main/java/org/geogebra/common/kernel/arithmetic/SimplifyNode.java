package org.geogebra.common.kernel.arithmetic;

public interface SimplifyNode {
	boolean isAccepted(ExpressionNode node);
	ExpressionNode apply(ExpressionNode node);
}
