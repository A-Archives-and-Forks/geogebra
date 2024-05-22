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
	private double[][] bcoeffsX;
	private double[] coeffsX;
	public BernsteinPolynomial(Polynomial polynomial, Kernel kernel,
			double xmin, double xmax, int degX, int degY, FunctionVariable[] functionVariables) {
		this.polynomial = polynomial;
		this.xmin = xmin;
		this.xmax = xmax;
		this.degX = degX;
		this.kernel = kernel;
		this.degY = degY;
		this.functionVariables = functionVariables;
		construct(degX);
	}

	void construct(int n) {
		degree = n;
		coeffsX = getCoeffsX();
		createBernsteinCoeffs(n);
		createBernsteinPolynomial();
		Log.debug("Out: " + output);
	}

	double[] getCoeffsX() {
		double[] coeffs = new double[polynomial.length()];
		for (int i = 0; i < polynomial.length(); i++) {
			Term term = polynomial.getTerm(i);
			coeffs[degree - i] = term != null ? (int) term.coefficient.evaluateDouble() : 0;
		}
		return coeffs;
	}

	private void createBernsteinCoeffs(int n) {
		bcoeffsX = new double[n + 1][n + 1];
		for (int i = 0; i <= n; i++) {
			for (int j = 0; j <= i; j++) {
				bcoeffsX[i][j] = bernsteinCoefficient(i, j);
			}
		}
	}

	private void createBernsteinPolynomial() {
		int i = degree;

		for (int j = 0; j <= i; j++) {
			ExpressionNode beta = new MyDouble(kernel, bcoeffsX[i][j]).wrap();
			BernsteinBasisPolynomial basis = new BernsteinBasisPolynomial(i, j,
					functionVariables[0]);
			ExpressionNode result = basis.multiply(beta);
			addToOutput(result);
			Log.debug("B_" + i + "" + j + " = " + result);
		}

		if (output != null) {
			output.simplifyLeafs();
		}
	}

	private void addToOutput(ExpressionNode result) {
		output = output == null ? result : output.plus(result);
	}

	private double bernsteinCoefficient(int i, int j) {
		double xl = xmin;
		double xh = xmax;
		if (i == 0 && j == 0) {
			return coeffsX[degree];
		}

		double a_nMinusI = coeffsX[degree - i];

		if (j == 0) {
			return a_nMinusI + xl * bcoeffsX[i - 1][0];
		}

		if (j == i) {
			return a_nMinusI + xh * bcoeffsX[i - 1][i - 1];
		}

		return MyMath.binomial(degree - i, j) * a_nMinusI
				+ xl * bcoeffsX[i - 1][j]
				+ xh * bcoeffsX[i - 1][i - 1];
	}

	public double evaluate(double value) {
		double y = (value - xmin) / (xmax - xmin);
		FunctionVariable fv = functionVariables[0];
		fv.set(y);
		return output.evaluateDouble();
	}
}
