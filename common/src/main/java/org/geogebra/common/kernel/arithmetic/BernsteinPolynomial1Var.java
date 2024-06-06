package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial1Var implements BernsteinPolynomial {
	private final double xmin;
	private final double xmax;
	private int degree;
	private ExpressionNode output;
	private final Kernel kernel;
	private final FunctionVariable variable;
	private final char variableName;
	private double[] coeffs;
	private double[][] bernsteinCoeffs;
	public BernsteinPolynomial1Var(Kernel kernel, Polynomial polynomial,
			FunctionVariable variable, double xmin, double xmax, int degree) {
		this.kernel = kernel;
		this.variable = variable;
		this.variableName = variable.getSetVarString().charAt(0);
		this.xmin = xmin;
		this.xmax = xmax;
		this.degree = degree;
		coeffs = getCoeffs(polynomial);
		construct();
	}

	public BernsteinPolynomial1Var(Kernel kernel, double[] coeffs,
			FunctionVariable variable, double xmin, double xmax, int degree) {
		this.kernel = kernel;
		this.variable = variable;
		this.variableName = variable.getSetVarString().charAt(0);
		this.xmin = xmin;
		this.xmax = xmax;
		this.degree = degree;
		this.coeffs = coeffs;
		construct();
	}

	void construct() {
		createBernsteinCoeffs();
		createBernsteinPolynomial();
 	}

	double[] getCoeffs(Polynomial polynomial) {
		double[] coeffs = new double[degree + 1];
		for (int i = 0; i <= degree; i++) {
			Term term = i < polynomial.length() ? polynomial.getTerm(i): null;
			if (term != null) {
				int power = term.degree(variableName);
				coeffs[power] = term.coefficient.evaluateDouble();
			}
		}
		return coeffs;
	}

	private void createBernsteinCoeffs() {
		bernsteinCoeffs = new double[degree + 1][degree + 1];
		for (int i = 0; i <= degree; i++) {
			for (int j = 0; j <= i; j++) {
				double b_ij = bernsteinCoefficient(i, j);
				bernsteinCoeffs[i][j] = b_ij;
			}
		}
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
//			Log.debug("B_" + i + " = " + output);
		}


		if (output != null) {
			output.simplifyLeafs();
		}
	}

	ExpressionNode createB(int i) {
		output = null;
		for (int j = i; j >= 0; j--) {
			ExpressionNode beta = new MyDouble(kernel, bernsteinCoeffs[i][j]).wrap();
			BernsteinBasisPolynomial basis = new BernsteinBasisPolynomial(variable, i, j
			);
			addToOutput(basis.multiply(beta));
		}
 		return output;
	}

	private void addToOutput(ExpressionNode result) {
		output = output == null ? result : output.plus(result);
	}

	double bernsteinCoefficient(int i, int j) {
		double xl = xmin;
		double xh = xmax;
		if (i == 0 && j == 0) {
			return coeffs[degree];
		}

		double a_nMinusI = coeffs[degree - i];

		if (j == 0) {
			return a_nMinusI + xl * bernsteinCoeffs[i - 1][0];
		}

		if (j == i) {
			return a_nMinusI + xh * bernsteinCoeffs[i - 1][i - 1];
		}

		double binomial = MyMath.binomial(i, j);
		return binomial * a_nMinusI
				+ xl * bernsteinCoeffs[i - 1][j]
				+ xh * bernsteinCoeffs[i - 1][j - 1];
	}

	@Override
	public double evaluate(double value) {
		double y = (value - xmin) / (xmax - xmin);
		variable.set(y);
		return output.evaluateDouble();
	}

	@Override
	public String coeffsToString() {
		return "";
	}

	@Override
	public ExpressionNode output() {
		return output;
	}

	@Override
	public String toString() {
		return output.toString(StringTemplate.defaultTemplate);
	}
}
