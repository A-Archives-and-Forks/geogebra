package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.util.MyMath;

public class BernsteinBuilder1Var {

	private double[] powerBasisCoeffs;
	private double[][] bernsteinCoeffs;


	BernsteinPolynomial build(double[] powerCoeffs,
			int degree, char variable, double min, double max) {
		powerBasisCoeffs = powerCoeffs;
		createBernsteinCoeffs(degree, min, max);
		return new BernsteinPolynomial1Var(bernsteinCoeffs, variable, min, max);
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
