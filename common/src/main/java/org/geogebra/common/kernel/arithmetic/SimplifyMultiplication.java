package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;

public class SimplifyMultiplication implements SimplifyNode {

	private final Kernel kernel;

	public SimplifyMultiplication(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public ExpressionNode simplify(ExpressionNode node) {
		return null;
	}
}
