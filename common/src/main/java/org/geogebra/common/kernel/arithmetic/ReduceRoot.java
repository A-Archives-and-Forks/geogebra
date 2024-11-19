package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;

public class ReduceRoot implements SimplifyNode {

	private final ExpressionNode node;
	private final Kernel kernel;

	public ReduceRoot(ExpressionNode node, Kernel kernel) {
		this.node = node;
		this.kernel = kernel;
	}

	@Override
	public ExpressionNode simplify() {
		ExpressionValue reduceUnderSqrt = node.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev.isOperation(Operation.SQRT)) {
					MyDouble evalUnderSqrt = new MyDouble(kernel, ev.wrap().getLeftTree()
							.evaluateDouble());
					ev.wrap().setLeft(evalUnderSqrt);
				}
				return ev;
			}
		});

		ExpressionValue surd = Surds.getResolution(reduceUnderSqrt.wrap(), kernel);
		return surd != null ? surd.wrap() : reduceUnderSqrt.wrap();

	}
}
