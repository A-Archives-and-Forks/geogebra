package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.Operation;

public final class BernsteinBasisPolynomial {
	private final int index;
	private final int degree;
	private final FunctionVariable fv;
	private final Kernel kernel;
	private final ExpressionNode node;

	public BernsteinBasisPolynomial(int degree, int index, FunctionVariable fv) {
		this.index = index;
		this.degree = degree;
		this.fv = fv;
		kernel = fv.kernel;
		node = compute();
	}

	private ExpressionNode compute() {
		ExpressionNode powerOfX = powerOf(fv.wrap(), index);
		ExpressionNode powerOfOneMinusX = powerOf(getOneMinusX(fv), degree - index);
		return powerOfOneMinusX.multiply(powerOfX);
	}

	private ExpressionNode powerOf(ExpressionNode node, int power) {
		if (power == 0) {
			return one().wrap();
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
		return new ExpressionNode(kernel, one(), Operation.MINUS,
				fv);
	}

	private MyDouble one() {
		return new MyDouble(kernel, 1);
	}

	@Override
	public String toString() {
		return node.toOutputValueString(StringTemplate.defaultTemplate);
	}

	public ExpressionNode getNode() {
		return node;
	}

	public ExpressionNode multiply(ExpressionNode arg) {
		return node.multiply(arg);
	}

	public double evaluate(double value) {
		if (value < 0 || value > 1) {
			return 0;
		}
		fv.set(value);
		return node.evaluateDouble();
	}
}
