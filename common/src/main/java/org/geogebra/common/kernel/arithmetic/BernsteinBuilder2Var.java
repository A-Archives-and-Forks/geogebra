package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.util.MyMath;

public class BernsteinBuilder2Var {
	private final BernsteinBuilder1Var builder1Var;
	private BernsteinPolynomial[] powerBasisCoeffs;
	private BernsteinPolynomial[][] bernsteinCoeffs;

	public BernsteinBuilder2Var(BernsteinBuilder1Var builder1Var) {
		this.builder1Var = builder1Var;
	}


	BernsteinPolynomial build(Polynomial polynomial, int degree, double min, double max) {
		double[][] powerCoeffs = coeffsFromTwoVarPolynomial(polynomial, degree);
		powerBasisCoeffs = coeffsToBernsteinCoeffs(powerCoeffs, degree, min, max);
		createBernsteinCoeffs2Var(degree, min, max);
		return new BernsteinPolynomial2Var(bernsteinCoeffs, min, max, degree, degree);
	}


	double[][] coeffsFromTwoVarPolynomial(Polynomial polynomial, int degree) {
		double[][] coeffs = new double[degree + 1][2];
		for (int i = 0; i <= degree; i++) {
			Term term = i < polynomial.length() ? polynomial.getTerm(i) : null;
			if (term != null) {
				int powerX = term.degree('x');
				int powerY = term.degree('y');
				if (powerY != 0) {
					coeffs[powerY][1] += term.coefficient.evaluateDouble();
				} else if (powerX != 0) {
					coeffs[powerX][0] += term.coefficient.evaluateDouble();

				}
			}
		}
		return coeffs;
	}

	private BernsteinPolynomial[] coeffsToBernsteinCoeffs(double[][] coeffs, int degree, double min,
			double max) {
		BernsteinPolynomial[] polys = new BernsteinPolynomial[degree + 1];
		for (int i = 0; i <= degree; i++) {
			double[] bCoeffs = new double[degree + 1];
			double x = coeffs[i][0];
			double y = coeffs[i][1];
			if (y == 0) {
				bCoeffs[i] = x;
				BernsteinPolynomial constantPoly = builder1Var.build(bCoeffs, degree, 'y', min, max);
				if (polys[i] == null) {
					polys[i] = constantPoly;
				} else {
					polys[i].plus(constantPoly);
				}
			} else {
				bCoeffs[i] = y;
				BernsteinPolynomial poly =
						builder1Var.build(bCoeffs, degree, 'y', min, max).multiply(x);
				if (polys[i] == null) {
					polys[i] = poly;
				} else {
					polys[i].plus(poly);
				}
			}
		}
		return polys;
	}

	private void createBernsteinCoeffs2Var(int degree, double min, double max) {
		bernsteinCoeffs = new BernsteinPolynomial[degree + 1][degree + 1];
		for (int i = 0; i <= degree; i++) {
			for (int j = 0; j <= i; j++) {
				BernsteinPolynomial b_ij = bernsteinCoefficient(i, j, degree, min, max);
				bernsteinCoeffs[i][j] = b_ij;
			}
		}
	}

	private BernsteinPolynomial bernsteinCoefficient(int i, int j, int degree, double min, double max) {
		if (i == 0 && j == 0) {
			return powerBasisCoeffs[degree];
		}

		BernsteinPolynomial a_nMinusI = powerBasisCoeffs[degree - i];

		if (j == 0) {
			return bernsteinCoeffs[i - 1][0].multiply(min).plus(a_nMinusI);
		}

		if (j == i) {
			return bernsteinCoeffs[i - 1][i - 1].multiply(max).plus(a_nMinusI);
		}

		double binomial = MyMath.binomial(i, j);
		return a_nMinusI.multiply(binomial)
				.plus(bernsteinCoeffs[i - 1][j].multiply(min))
				.plus(bernsteinCoeffs[i - 1][j - 1].multiply(max));
	}
}
