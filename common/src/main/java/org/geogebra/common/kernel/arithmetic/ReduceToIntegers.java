package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyNode;
import org.geogebra.common.plugin.Operation;

public class ReduceToIntegers implements SimplifyNode {
	private final Kernel kernel;

	public ReduceToIntegers(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		return node.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				double v = ev.evaluateDouble();
				if (Math.round(v) == v && v != -1) {
					return new MyDouble(kernel, v);
				}

				return ev;
			}

			private boolean minusOneMultiply(ExpressionNode node) {
				if (node == null) {
					return false;
				}
				return node.isOperation(Operation.MULTIPLY) && node.getLeft().evaluateDouble() == -1;
			}
		}).wrap();
	}
}
