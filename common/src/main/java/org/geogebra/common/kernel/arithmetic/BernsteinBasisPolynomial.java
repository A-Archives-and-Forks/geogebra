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
	private final ExpressionNode partial;
	private final ExpressionNode node;

	public BernsteinBasisPolynomial(FunctionVariable fv, int degree, int index) {
		this.index = index;
		this.degree = degree;
		this.fv = fv;
		kernel = fv.kernel;
		node = compute();
		partial = computePartial();
	}

	private ExpressionNode computePartial() {
		return getPowerOfOneMinusX().multiply(getPowerOfX());
	}

	private ExpressionNode compute() {
		double binomial = MyMath.binomial(degree, index);
		return getPowerOfOneMinusX().multiply(
				getPowerOfX().multiply(binomial));
	}

	private ExpressionNode getPowerOfX() {
		return powerOf(fv.wrap(), index);
	}

	private ExpressionNode getPowerOfOneMinusX() {
		return powerOf(getOneMinusX(fv), degree - index);
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

	public ExpressionNode getPartial() {
		return partial;
	}
}
