package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.util.MyMath;

public class BernsteinPolynomial1Var implements BernsteinPolynomial {
	private final double xmin;
	private final double xmax;
	final int degree;
	final char variableName;
	private final double[] powerBasisCoeffs;
	private double[][] bernsteinCoeffs;
	private BernsteinPolynomialFormatter formatter;

	public BernsteinPolynomial1Var(Polynomial polynomial,
			char variableName, double xmin, double xmax, int degree) {
		this(coeffsFromPolynomial(polynomial, degree, variableName),
				variableName, xmin, xmax, degree);
	}

	public BernsteinPolynomial1Var(double[] coeffs,
			char variableName, double xmin, double xmax, int degree) {
		this.variableName = variableName;
		this.xmin = xmin;
		this.xmax = xmax;
		this.degree = degree;
		this.powerBasisCoeffs = coeffs;
		formatter = new BernsteinPolynomialFormatter(this);
		construct();
	}

	public BernsteinPolynomial1Var(int degree, char variableName1) {
		this(new double[degree + 1], variableName1, 0, 1, degree);
	}

	static double[] coeffsFromPolynomial(Polynomial polynomial, int degree, char variableName) {
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

	void construct() {
		createBernsteinCoeffs();
		formatter.debugBernsteinCoeffs();
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

	double bernsteinCoefficient(int i, int j) {
		double xl = xmin;
		double xh = xmax;
		if (i == 0 && j == 0) {
			return powerBasisCoeffs[degree];
		}

		double a_nMinusI = powerBasisCoeffs[degree - i];

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
		if (bernsteinCoeffs == null) {
			return 0;
		}

		double scaledValue = (value - xmin) / (xmax - xmin);
		double result = 0;
		for (int i = degree; i >= 0; i--) {
			result += (bernsteinCoeffs[degree][i]
					* Math.pow(scaledValue, i)
					* Math.pow(1 - scaledValue, degree - i));
		}
		return result;
	}

	@Override
	public String coeffsToString() {
		return "";
	}

	@Override
	public ExpressionNode output() {
		return null;
	}

	@Override
	public String toString() {
		return formatter.toString();
	}

	@Override
	public BernsteinPolynomial derivative() {
		if (bernsteinCoeffs == null) {
			return this;
		}

		double[] derivedCoeffs = new double[degree];
//		double[] derivedCoeffs = new double[]{2,6};
		for (int i = 0; i < degree; i++) {
			double b1 = (degree - i) * bernsteinCoeffs[degree][i];
			double b2 = (i + 1) * bernsteinCoeffs[degree][i + 1];
			derivedCoeffs[i] = b2 - b1;
		}

		BernsteinPolynomial1Var polynomial1Var = new BernsteinPolynomial1Var(derivedCoeffs,
				variableName, xmin, xmax, degree - 1);
		polynomial1Var.bernsteinCoeffs[degree - 1] = derivedCoeffs;
		return polynomial1Var;
	}

	@Override
	public void addPowerBasis(int index, double value) {
		powerBasisCoeffs[index] += value;
	}

	public double getBernsteinCoefficient(int i, int j) {
		return bernsteinCoeffs[i][j];
	}
}
