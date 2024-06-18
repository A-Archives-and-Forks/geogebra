package org.geogebra.common.kernel.arithmetic;
import org.geogebra.common.util.MyMath;

public class BernsteinBuilder2Var {
	private final BernsteinBuilder1Var builder1Var;
	private BernsteinPolynomial[] powerBasisCoeffs;
	private BernsteinPolynomial[][] bernsteinCoeffs;

	public BernsteinBuilder2Var(BernsteinBuilder1Var builder1Var) {
		this.builder1Var = builder1Var;
	}


	BernsteinPolynomial build(Polynomial polynomial, int degreeX, int degreeY,
			double min, double max) {
		double[][] powerCoeffs = coeffsFromTwoVarPolynomial(polynomial, degreeX, degreeY);
		powerBasisCoeffs = coeffsToBernsteinCoeffs(powerCoeffs, degreeX, degreeY, min, max);
		createBernsteinCoeffs2Var(degreeX, degreeY, min, max);
		return new BernsteinPolynomial2Var(bernsteinCoeffs, min, max, degreeX, degreeY);
	}


	double[][] coeffsFromTwoVarPolynomial(Polynomial polynomial, int degreeX, int degreeY) {
		double[][] coeffs = new double[degreeX + 1][degreeY + 1];
		for (int i = 0; i < polynomial.length(); i++) {
			Term term = polynomial.getTerm(i);
			if (term != null) {
				int powerX = term.degree('x');
				int powerY = term.degree('y');
					coeffs[powerX][powerY] += term.coefficient.evaluateDouble();
				}
			}

		return coeffs;
	}

	private BernsteinPolynomial[] coeffsToBernsteinCoeffs(double[][] coeffs, int degreeX,
			int degreeY, double min, double max) {
		BernsteinPolynomial[] polys = new BernsteinPolynomial[degreeX + 1];
		for (int i = 0; i <= degreeX; i++) {
			polys[i] = builder1Var.build(coeffs[i], degreeY, 'y', min, max);
		}
		return polys;
	}

	private void createBernsteinCoeffs2Var(int degreeX, int degreeY, double min, double max) {
		bernsteinCoeffs = new BernsteinPolynomial[degreeX + 1][degreeX + 1];
		for (int i = 0; i <= degreeX; i++) {
			for (int j = 0; j <= i; j++) {
				BernsteinPolynomial b_ij = bernsteinCoefficient(i, j, degreeX, min, max);
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
