package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial {
	private final Polynomial polynomial;
	private final double xmin;
	private final double xmax;
	private final int degX;
	private int degree;
	private ExpressionNode output;
	private final Kernel kernel;
	private final int degY;
	private final FunctionVariable[] functionVariables;
	public BernsteinPolynomial(Polynomial polynomial, Kernel kernel,
			double xmin, double xmax, int degX, int degY, FunctionVariable[] functionVariables) {
		this.polynomial = polynomial;
		this.xmin = xmin;
		this.xmax = xmax;
		this.degX = degX;
		this.kernel = kernel;
		this.degY = degY;
		this.functionVariables = functionVariables;
	}

	void construct(int n) {
		degree = n;
		for (int i = 0; i <= degree; i++) {
			BernsteinBasisPolynomial basis = new BernsteinBasisPolynomial(degree, i,
					functionVariables[0]);
			double bernsteinCoefficient = bernsteinCoefficient(degree, i);
			ExpressionNode beta = new MyDouble(kernel, bernsteinCoefficient).wrap();
			ExpressionNode result = basis.multiply(beta);
			addToOutput(result);
		}

		output.simplifyLeafs();
		Log.debug("Out: " + output);
	}

	private void addToOutput(ExpressionNode result) {
		output = output == null ? result : output.plus(result);
	}

	private double bernsteinCoefficient(int i, int j) {
		double xl = 1;
		double xh = 1;
		if (i == 0 && j == 0) {
			return coeffX(degree - 1);
		}
		int a_i = coeffX(degree - i);
		if (j == 0) {
			return a_i + xl * bernsteinCoefficient(i - 1, 0);
		}

		if (j == i) {
			return a_i + xh * bernsteinCoefficient(i - 1, i - 1);
		}

		return MyMath.binomial(degree - i, j) * a_i
				+ xl * bernsteinCoefficient(i - 1, j)
				+ xh * bernsteinCoefficient(i - 1, i - 1);
	}

	private int coeffX(int i) {
		Term term = polynomial.getTerm(i);
		return term != null ? (int) term.coefficient.evaluateDouble() : 0;
	}

	public double evaluate(double value) {
		double y = (value - xmax) / (xmax - xmin);
		FunctionVariable fv = functionVariables[0];
		fv.set(y);
		return output.evaluateDouble();
	}
}
