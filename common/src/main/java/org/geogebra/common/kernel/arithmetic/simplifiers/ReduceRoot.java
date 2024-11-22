package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Surds;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;

public class ReduceRoot implements SimplifyNode {
	private final Kernel kernel;

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	public ReduceRoot(Kernel kernel) {
		this.kernel = kernel;
	}
	@Override
	public ExpressionNode apply(ExpressionNode node) {
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
		double v = reduceUnderSqrt.evaluateDouble();
		if (v == Math.round(v)) {
			return new MyDouble(kernel, v).wrap();
		}

		ExpressionValue surd = Surds.getResolution(reduceUnderSqrt.wrap(), kernel);
		return surd != null ? surd.wrap() : reduceUnderSqrt.wrap();

	}
}
