package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.util.MyMath;

public class BernsteinPolynomialConverter {

	private final BernsteinBuilder1Var builder1Var = new BernsteinBuilder1Var();
	private BernsteinPolynomial[] powerBasisCoeffs2Var;
	private BernsteinPolynomial[][] bernsteinCoeffs2Var;

	BernsteinPolynomial fromImplicitCurve(GeoImplicitCurve curve, double min, double max) {
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		Polynomial polynomial = functionNVar.getPolynomial();
		return fromPolynomial(polynomial, curve.getDegX(), curve.getDegY(), min, max);
	}

	BernsteinPolynomial fromPolynomial(Polynomial polynomial, int degreeX, int degreeY, double min,
			double max) {
		if (degreeX !=0 && degreeY != 0) {
			double[] powerCoeffs = coeffsFromTwoVarPolynomial(polynomial, degreeX);
			return new2VarFromPowerBasisCoefficients(powerCoeffs,
					Math.max(degreeX, degreeY), min, max);
		}

		if (degreeY == 0) {
			return builder1Var.newFromPowerBasisCoefficients(coeffsFromPolynomial(polynomial, degreeX, 'x'),
					degreeX, 'x', min, max);
		}

		return builder1Var.newFromPowerBasisCoefficients(coeffsFromPolynomial(polynomial, degreeY, 'y'),
				degreeY, 'y', min, max);
	}

	private double[] coeffsFromPolynomial(Polynomial polynomial, int degree, char variableName) {
		double[] coeffs = new double[degree + 1];
		for (int i = 0; i <= degree; i++) {
			Term term = i < polynomial.length() ? polynomial.getTerm(i) : null;
			if (term != null) {
				int power = term.degree(variableName);
				coeffs[power] = term.coefficient.evaluateDouble();
			}
		}
		return coeffs;
	}



	BernsteinPolynomial new2VarFromPowerBasisCoefficients(double[] powerCoeffs,
			int degree, double min, double max) {
		powerBasisCoeffs2Var = coeffsToBernsteinCoeffs(powerCoeffs, degree, min, max);
		createBernsteinCoeffs2Var(degree, min, max);
		return new BernsteinPolynomial2Var(bernsteinCoeffs2Var, min, max, degree, degree);
	}


	double[] coeffsFromTwoVarPolynomial(Polynomial polynomial, int degree) {
		double[] coeffs = new double[degree + 1];
		for (int i = 0; i <= degree; i++) {
			Term term = i < polynomial.length() ? polynomial.getTerm(i) : null;
			if (term != null) {
				int powerY = term.degree('y');
				if (powerY != 0) {
					coeffs[powerY] += term.coefficient.evaluateDouble();
				}
			}
		}
		return coeffs;
	}

	private BernsteinPolynomial[] coeffsToBernsteinCoeffs(double[] coeffs, int degree, double min,
			double max) {
		BernsteinPolynomial[] polys = new BernsteinPolynomial[degree + 1];
		for (int i = 0; i <= degree; i++) {
			double[] yCoeffs = new double[degree + 1];
			yCoeffs[i] = coeffs[i];
			polys[i] = builder1Var.newFromPowerBasisCoefficients(yCoeffs, degree, 'y', min, max);
		}
		return polys;
	}

	private void createBernsteinCoeffs2Var(int degree, double min, double max) {
		bernsteinCoeffs2Var = new BernsteinPolynomial[degree + 1][degree + 1];
		for (int i = 0; i <= degree; i++) {
			for (int j = 0; j <= i; j++) {
				BernsteinPolynomial b_ij = bernsteinCoefficient2Var(i, j, degree, min, max);
				bernsteinCoeffs2Var[i][j] = b_ij;
			}
		}
	}

	private BernsteinPolynomial bernsteinCoefficient2Var(int i, int j, int degree, double min, double max) {
		if (i == 0 && j == 0) {
			return powerBasisCoeffs2Var[degree];
		}

		BernsteinPolynomial a_nMinusI = powerBasisCoeffs2Var[degree - i];

		if (j == 0) {
			return bernsteinCoeffs2Var[i - 1][0].multiply(min).plus(a_nMinusI);
		}

		if (j == i) {
			return bernsteinCoeffs2Var[i - 1][i - 1].multiply(max).plus(a_nMinusI);
		}

		double binomial = MyMath.binomial(i, j);
		return a_nMinusI.multiply(binomial)
				.plus(bernsteinCoeffs2Var[i - 1][j].multiply(min))
				.plus(bernsteinCoeffs2Var[i - 1][j - 1].multiply(max));
	}

}
