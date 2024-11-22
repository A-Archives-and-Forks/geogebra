package org.geogebra.common.kernel.arithmetic.simplifiers;


import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;

public class SumNumbers implements SimplifyNode{
	private final Kernel kernel;
	private int sum;

	public SumNumbers(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		sum = 0;
		List<ExpressionValue> expressionValues = new ArrayList<>();
		node.traverse(new Traversing() {
			ExpressionValue prevEv = null;

			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev.isOperation(Operation.SQRT)) {
					expressionValues.add(ev);
					return new MyDouble(kernel, 0);
				}
				return ev;
			}

		});
		return new ExpressionNode(kernel, new MyDouble(kernel, node.evaluateDouble()), Operation.NO_OPERATION, null);
	}
}
