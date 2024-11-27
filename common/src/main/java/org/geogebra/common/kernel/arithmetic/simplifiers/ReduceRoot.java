package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Surds;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

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
					ExpressionValue surd = Surds.getResolution(ev.wrap(), kernel);
					if (surd != null) {
						return surd;
					}
					double valUnderSqrt = ev.wrap().getLeftTree().evaluateDouble();
					double sqrt = Math.sqrt(valUnderSqrt);
					if (DoubleUtil.isInteger(sqrt)) {
						return new MyDouble(kernel, sqrt);
					}
					MyDouble evalUnderSqrt = new MyDouble(kernel, valUnderSqrt);
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
