package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.BinomialCoefficientsSign.from1Var;

import org.geogebra.common.util.MyMath;

public final class BernsteinPolynomial1Var implements BernsteinPolynomial {
	private final double min;
	private final double max;
	final int degree;
	final char variableName;
	final double[] bernsteinCoeffs;
	double[] dividedCoeffs;
	private final BinomialCoefficientsSign sign;

	/**
	 * @param bernsteinCoeffs coeffs for x^k (1-x)^(degree - k), NOT divided by binomial coeffs
	 * @param variableName variable name
	 * @param min min value for original variable
	 * @param max max value for original variable
	 */
	public BernsteinPolynomial1Var(double[] bernsteinCoeffs,
			char variableName, double min, double max) {
		this.variableName = variableName;
		this.min = min;
		this.max = max;
		this.degree = bernsteinCoeffs.length - 1;
		this.bernsteinCoeffs = bernsteinCoeffs;
		this.dividedCoeffs = null;
		sign = from1Var(bernsteinCoeffs, degree);
	}

	/**
	 * Divided coefficients are needed only for evaluation and splitting.
	 *
	 */
	private void createLazyDivideCoeffs() {
		if (dividedCoeffs != null) {
			return;
		}
		dividedCoeffs = new double[degree + 1];
		for (int i = 0; i < degree + 1; i++) {
			dividedCoeffs[i] = bernsteinCoeffs[i] / MyMath.binomial(degree, i);
		}
	}

	@Override
	public double evaluate(double value) {
		double[] partialEval = new double[degree + 1];
		double[] lastPartialEval = new double[degree + 1];
		double scaledValue = (value - min) / (max - min);
		double oneMinusScaledValue = 1 - scaledValue;

		createLazyDivideCoeffs();
		copyArrayTo(dividedCoeffs, lastPartialEval);

		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				partialEval[j] = oneMinusScaledValue * lastPartialEval[j]
						+ scaledValue * lastPartialEval[j + 1];
			}
			copyArrayTo(partialEval, lastPartialEval);
		}
		return partialEval[0];
	}

	@Override
	public double evaluate(double x, double y) {
		// y is ignored in 1var case.
		return evaluate(x);
	}

	@Override
	public BernsteinPolynomial[] split() {
		BernsteinPolynomialCache bPlus = new BernsteinPolynomialCache(degree + 1);
		BernsteinPolynomialCache bMinus = new BernsteinPolynomialCache(degree + 1);

		createLazyDivideCoeffs();
		for (int i = 0; i < degree + 1; i++) {
			double[] coeffs = new double[1];
			coeffs[0] = dividedCoeffs[i];
			bPlus.setLast(i, newInstance(coeffs));
			bMinus.setLast(i, newInstance(coeffs));
		}

		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				bPlus.set(j,
						bPlus.last[j].multiplyByOneMinusX()
						.plus(bPlus.last[j].plus(bPlus.last[j + 1]).multiplyByX().divide(2)));

				bMinus.set(j,
						bMinus.last[j].plus(bMinus.last[j + 1]).multiplyByOneMinusX()
						.divide(2).plus(bMinus.last[j + 1].multiplyByX()));
			}
			bPlus.update();
			bMinus.update();
		}

		return new BernsteinPolynomial[]{bPlus.last[0], bMinus.last[0]};
	}

	private BernsteinPolynomial newInstance(double[] coeffs) {
		return new BernsteinPolynomial1Var(coeffs, variableName, min, max);
	}

	@Override
	public BernsteinPolynomial[][] split2D() {
		return null;
	}

	static void copyArrayTo(double[] src, double[] dest) {
		System.arraycopy(src, 0, dest, 0, dest.length);
	}

	@Override
	public BernsteinPolynomial derivative() {
		double[] derivedCoeffs = new double[degree];
		for (int i = 0; i < degree; i++) {
			double b1 = (degree - i) * bernsteinCoeffs[i];
			double b2 = (i + 1) * bernsteinCoeffs[i + 1];
			derivedCoeffs[i] = b2 - b1;
		}

		return newInstance(derivedCoeffs);
	}

	@Override
	public BernsteinPolynomial multiply(double value) {
		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] * value;
		}
		return newInstance(coeffs);

	}

	@Override
	public BernsteinPolynomial divide(double value) {
		return multiply(1.0 / value);
	}

	@Override
	public BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial) {
		if (bernsteinPolynomial == null) {
			return this;
		}
		double[] coeffs = new double[degree + 1];
		BernsteinPolynomial1Var polynomial1Var =
				(BernsteinPolynomial1Var) bernsteinPolynomial;

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i]
					+ polynomial1Var.bernsteinCoeffs[i];
		}
		return newInstance(coeffs);
	}

	@Override
	public BernsteinPolynomial derivative(String variable) {
		return derivative();
	}

	@Override
	public BernsteinPolynomial multiplyByOneMinusX() {
		return shiftFrom(0);
	}

	@Override
	public BernsteinPolynomial multiplyByX() {
		return shiftFrom(1);
	}

	private BernsteinPolynomial shiftFrom(int destPos) {
		double[] shifted = new double[degree + 2];
		System.arraycopy(bernsteinCoeffs, 0, shifted, destPos, bernsteinCoeffs.length);
		return newInstance(shifted);
	}

	@Override
	public boolean isConstant() {
		return bernsteinCoeffs.length == 1;
	}

	@Override
	public int numberOfVariables() {
		return 1;
	}

	@Override
	public boolean hasNoSolution() {
		return sign.monotonic();
	}

	@Override
	public BernsteinPolynomial substitute(String variable, double value) {
		return null;
	}

	@Override
	public BernsteinPolynomial linearCombination(BernsteinPolynomial coeffs,
			BernsteinPolynomial otherPoly, BernsteinPolynomial otherCoeffs) {
		return null;
	}

	@Override
	public String toString() {
		return BernsteinToString.toString1Var(this);
	}

	@Override
	public BinomialCoefficientsSign getSign() {
		return sign;
	}

	@Override
	public BernsteinPolynomial linearCombination(int coeff, BernsteinPolynomial otherPoly,
			int otherCoeff) {
		if (otherPoly == null) {
			return this;
		}
		double[] coeffs = new double[degree + 1];

		BernsteinPolynomial1Var otherPoly1 = (BernsteinPolynomial1Var) otherPoly;
		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] * coeff
					+ otherPoly1.bernsteinCoeffs[i] * otherCoeff;
		}
		return newInstance(coeffs);
	}
}