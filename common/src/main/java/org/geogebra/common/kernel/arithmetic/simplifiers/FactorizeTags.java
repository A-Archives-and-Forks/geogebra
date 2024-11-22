package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.plugin.Operation;

public class FactorizeTags implements SimplifyNode {
	private final Kernel kernel;

	public FactorizeTags(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return !node.inspect(new Inspecting() {
			@Override
			public boolean check(ExpressionValue v) {
				return !v.isLeaf() && v.wrap().isOperation(Operation.DIVIDE);
			}
		});
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		return node;
	}
}
