package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial2Var implements BernsteinPolynomial {
	private final double xmin;
	private final double xmax;
	private int degree;
	private ExpressionNode output;
	private final Kernel kernel;
	private final FunctionVariable[] variables;
	private BernsteinPolynomial[] coeffs;
	private final int degreeX;
	private final int degreeY;
	private ExpressionNode[][] bernsteinCoeffs;
	public BernsteinPolynomial2Var(Kernel kernel, Polynomial polynomial,
			FunctionVariable[] variables, double xmin, double xmax, int degreeX, int degreeY) {
		this.kernel = kernel;
		this.variables = variables;
		this.xmin = xmin;
		this.xmax = xmax;
		this.degreeX = degreeX;
		this.degreeY = degreeY;
		this.degree = Math.max(degreeX, degreeY);
		coeffs = getCoeffs(polynomial);
		construct();
	}

	void construct() {
		Log.debug("coeffs of x:\n" + coeffsToString());
//		Log.debug("Berstein coeffs:\n");
//		createBernsteinCoeffs();
//		debugBernsteinCoeffs();
//		createBernsteinPolynomial();
//		Log.debug("Final output:");
//		Log.debug(output.toString(StringTemplate.defaultTemplate));
 	}

	BernsteinPolynomial[] getCoeffs(Polynomial polynomial) {
		double[] coeffs = new double[degree + 1];
		BernsteinPolynomial[] polys = new BernsteinPolynomial1Var[degree + 1];


		for (int i = 0; i <= degree; i++) {
			Term term = i < polynomial.length() ? polynomial.getTerm(i): null;
			if (term != null) {
				int powerX = term.degree('x');
				int powerY = term.degree('y');
				if (powerY == 0) {
//					coeff.addPowerBasis(powerX, term.coefficient.evaluateDouble());
				} else {
					coeffs[powerY] += term.coefficient.evaluateDouble();
				}
			}
		}

		return null;//coeffs;
	}


	private void createBernsteinCoeffs() {
//		bernsteinCoeffs = new ExpressionNode[degree + 1][degree + 1];
//		for (int i = 0; i <= degree; i++) {
//			for (int j = 0; j <= i; j++) {
//				ExpressionNode node = bernsteinCoefficient(i, j);
//				BernsteinPolynomial1Var polynomial =
//						node2BernsteinPolynomial(node);
//				bernsteinCoeffs[i][j] = polynomial.output();
//			}
//		}
	}

	public String coeffsToString() {
		StringBuilder sb = new StringBuilder();
		String fs = "";
		for (BernsteinPolynomial coeff : coeffs) {
			sb.append(fs);
			fs = ", ";
			sb.append(coeff);
		}
		return sb.toString();
	}

	private void debugBernsteinCoeffs() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= degree; i++) {
			sb.append("(");
			String fs = "";
			for (int j = 0; j <= i; j++) {
				sb.append(fs);
				fs=", ";
				sb.append(bernsteinCoeffs[i][j]);
			}
			sb.append(")\n");
		}
		Log.debug(sb);
	}

	private void createBernsteinPolynomial() {
		for (int i = 0; i <= degree; i++) {
			output = createB(i);
			Log.debug("B_" + i + " = " + output);
		}


		if (output != null) {
			output.simplifyLeafs();
		}
	}

	ExpressionNode createB(int i) {
		output = null;
		for (int j = i; j >= 0; j--) {
			ExpressionNode beta = bernsteinCoeffs[i][j];
//			BernsteinBasisPolynomial basis = new BernsteinBasisPolynomial(fvX, i, j
//			);
//			addToOutput(basis.multiply(beta));
		}
 		return output;
	}

	private void addToOutput(ExpressionNode result) {
		output = output == null ? result : output.plus(result);
	}

	ExpressionNode bernsteinCoefficient(int i, int j) {
//		double xl = xmin;
//		double xh = xmax;
//		if (i == 0 && j == 0) {
//			return coeffs[degree].output();
//		}
//
//		ExpressionNode a_nMinusI = coeffs[degree - i].output();
//
//		if (j == 0) {
//			return a_nMinusI.plus(bernsteinCoeffs[i - 1][0].multiply(xl));
//		}
//
//		if (j == i) {
//			return a_nMinusI.plus(bernsteinCoeffs[i - 1][i - 1].multiply(xh));
//		}
//
//		double binomial = MyMath.binomial(i, j);
//		return a_nMinusI.multiply(binomial)
//				.plus(bernsteinCoeffs[i - 1][j].multiply(xl))
//				.plus(bernsteinCoeffs[i - 1][j - 1].multiply(xh));
		return null;
	}

	public double evaluate(double value) {
		double y = (value - xmin) / (xmax - xmin);
//		variable.set(y);
		return output.evaluateDouble();
	}

	@Override
	public BernsteinPolynomial derivative() {
		return null;
	}
}
