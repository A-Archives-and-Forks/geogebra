package org.geogebra.common.kernel.arithmetic;


import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial1Var implements BernsteinPolynomial {
	private final double xmin;
	private final double xmax;
	private final int degree;
	private final char variableName;
	private final double[] powerBasisCoeffs;
	private double[][] bernsteinCoeffs;
	private List<BernsteinBasisPolynomial> bases = new ArrayList<>();
	public BernsteinPolynomial1Var(Polynomial polynomial,
			char variableName, double xmin, double xmax, int degree) {
		this.variableName = variableName;
		this.xmin = xmin;
		this.xmax = xmax;
		this.degree = degree;
		powerBasisCoeffs = coeffsFromPolynomial(polynomial);
		construct();
	}

	public BernsteinPolynomial1Var(double[] coeffs,
			char variableName, double xmin, double xmax, int degree) {
		this.variableName = variableName;
		this.xmin = xmin;
		this.xmax = xmax;
		this.degree = degree;
		this.powerBasisCoeffs = coeffs;
		construct();
	}

	double[] coeffsFromPolynomial(Polynomial polynomial) {
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
		debugBernsteinCoeffs();
		createBernsteinPolynomial();
		debugBases();
 	}

	private void debugBases() {
		for (BernsteinBasisPolynomial basis: bases) {
			Log.debug(basis.toString());
		}
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
		}
	}

	void createB(int i) {
		for (int j = i; j >= 0; j--) {
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
		double y = (value - xmin) / (xmax - xmin);
		return 0;
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
		return null;
	}

	@Override
	public ExpressionNode derivative() {
		return null;
	}
}
