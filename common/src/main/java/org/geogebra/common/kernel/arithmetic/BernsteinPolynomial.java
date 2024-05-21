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
		Bernstein(3);
//		output.simplifyLeafs();
		Log.debug("Out: " + output);
	}

	private void Bernstein(int i) {
		degree = i;
		for (int j = 0; j <= i; j++) {
			double b = bernsteinCoefficient(i, j);
			ExpressionNode beta = new MyDouble(kernel, b).wrap();
			BernsteinBasisPolynomial basis = new BernsteinBasisPolynomial(i, j,
					functionVariables[0]);
			addToOutput(basis.multiply(beta));
		}
	}

	private void addToOutput(ExpressionNode result) {
		output = output == null ? result : output.plus(result);
	}

	private double bernsteinCoefficient(int i, int j) {
		double xl = xmin;
		double xh = xmax;
		if (i == 0 && j == 0) {
			return coeffX(degree - 1);
		}
		int a_nMinusI = coeffX(degree - i);
		if (j == 0) {
			return a_nMinusI + xl * bernsteinCoefficient(i - 1, 0);
		}

		if (j == i) {
			return a_nMinusI + xh * bernsteinCoefficient(i - 1, i - 1);
		}

		return MyMath.binomial(degree - i, j) * a_nMinusI
				+ xl * bernsteinCoefficient(i - 1, j)
				+ xh * bernsteinCoefficient(i - 1, i - 1);
	}

	private int coeffX(int i) {
		Term term = polynomial.getTerm(i);
		return term != null ? (int) term.coefficient.evaluateDouble() : 0;
	}

	public double evaluate(double value) {
		double y = (value - xmax) / Math.abs(xmax - xmin);
		FunctionVariable fv = functionVariables[0];
		fv.set(y);
		return output.evaluateDouble();
	}
}
