package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;

public final class BernsteinBasisPolynomial {
	private final int index;
	private final int degree;
	private final FunctionVariable fv;
	private final Kernel kernel;
	private final ExpressionNode value;

	public BernsteinBasisPolynomial(int index, int degree, FunctionVariable fv) {
		this.index = index;
		this.degree = degree;
		this.fv = fv;
		kernel = fv.kernel;
		value = compute();
	}

	private ExpressionNode compute() {
		ExpressionNode powerOfX = powerOf(fv.wrap(), index);
		ExpressionNode powerOfOneMinusX = powerOf(getOneMinusX(fv), degree);
		MyDouble binomial = new MyDouble(kernel, MyMath.binomial(degree, index));
		return binomial.wrap().multiply(powerOfX).multiply(powerOfOneMinusX);

	}

	private ExpressionNode powerOf(ExpressionNode node, int power) {
		if (power == 0) {
			return new MyDouble(kernel, 1).wrap();
		}

		if (power == 1) {
			return node;
		}

		return new ExpressionNode(kernel,
				node,
				Operation.POWER,
				new MyDouble(kernel, power));
	}

	private ExpressionNode getOneMinusX(FunctionVariable fv) {
		return new ExpressionNode(kernel, new MyDouble(kernel, 1), Operation.MINUS,
				fv);
	}

	@Override
	public String toString() {
		return value.toOutputValueString(StringTemplate.defaultTemplate);
	}
}
