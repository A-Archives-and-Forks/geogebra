package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.util.MyMath;

public class BernsteinPolynomialConverter {

	private double[] powerBasisCoeffs;
	private double[][] bernsteinCoeffs;

	BernsteinPolynomial fromImplicitCurve(GeoImplicitCurve curve, double min, double max) {
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		Polynomial polynomial = functionNVar.getPolynomial();
		return fromPolynomial(polynomial, curve.getDegX(), curve.getDegY(), min, max);
	}

	BernsteinPolynomial fromPolynomial(Polynomial polynomial, int degreeX, int degreeY, double min,
			double max) {
		return fromPowerBasisCoefficients(coeffsFromPolynomial(polynomial, degreeX, 'x'),
					degreeX, degreeY, min, max);
	}

	private double[] coeffsFromPolynomial(Polynomial polynomial, int degree, char variableName) {
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

	BernsteinPolynomial fromPowerBasisCoefficients(double[] powerCoeffs,
			int degreeX, int degreeY, double min, double max) {
		powerBasisCoeffs = powerCoeffs;
		createBernsteinCoeffs(degreeX, min, max);
		return new BernsteinPolynomial1Var(bernsteinCoeffs, 'x', min, max);
	}

	private void createBernsteinCoeffs(int degree, double min, double max) {
		bernsteinCoeffs = new double[degree + 1][degree + 1];
		for (int i = 0; i <= degree; i++) {
			for (int j = 0; j <= i; j++) {
				double b_ij = bernsteinCoefficient(i, j, degree, min, max);
				bernsteinCoeffs[i][j] = b_ij;
			}
		}
	}

	private double bernsteinCoefficient(int i, int j, int degree, double min, double max) {
		if (i == 0 && j == 0) {
			return powerBasisCoeffs[degree];
		}

		double a_nMinusI = powerBasisCoeffs[degree - i];

		if (j == 0) {
			return a_nMinusI + min * bernsteinCoeffs[i - 1][0];
		}

		if (j == i) {
			return a_nMinusI + max * bernsteinCoeffs[i - 1][i - 1];
		}

		double binomial = MyMath.binomial(i, j);
		return binomial * a_nMinusI
				+ min * bernsteinCoeffs[i - 1][j]
				+ max * bernsteinCoeffs[i - 1][j - 1];
	}
}
