package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.BernsteinPolynomial1Var.copyArrayTo;
import static org.geogebra.common.kernel.arithmetic.BernsteinPolynomial1Var.divWithBinomials;
import static org.geogebra.common.kernel.arithmetic.BernsteinPolynomial1Var.powerString;

import org.geogebra.common.util.MyMath;

public class BernsteinPolynomial2Var implements BernsteinPolynomial {
	private final double min;
	private final double max;
	private final int degreeX;
	private final BernsteinPolynomial[] bernsteinCoeffs;

	/**
	 * @param bernsteinCoeffs coefficients in x
	 * @param min min value for original x
	 * @param max max value for original x
	 * @param degreeX degree in x
	 */
	public BernsteinPolynomial2Var(BernsteinPolynomial[] bernsteinCoeffs, double min, double max,
			int degreeX) {
		this.min = min;
		this.max = max;
		this.degreeX = degreeX;
		this.bernsteinCoeffs = bernsteinCoeffs;
	}

	@Override
	public double evaluate(double valueX, double valueY) {
		double[] partialEval = new double[degreeX + 1];
		double[] lastPartialEval = new double[degreeX + 1];
		double scaledX = (valueX - min) / (max - min);
		double scaledOneMinusX = 1 - scaledX;

		for (int i = 0; i < degreeX + 1; i++) {
			lastPartialEval[i] = bernsteinCoeffs[i].evaluate(valueY);
		}
		divWithBinomials(lastPartialEval, degreeX);

		for (int i = 1; i <= degreeX + 1; i++) {
			for (int j = degreeX - i; j >= 0; j--) {
				partialEval[j] = scaledOneMinusX * lastPartialEval[j]
						+ scaledX * lastPartialEval[j + 1];
			}
			copyArrayTo(partialEval, lastPartialEval);
		}
		return partialEval[0];
	}

	@Override
	public double evaluate(double value) {
		return 0;
	}

	@Override
	public BernsteinPolynomial derivative() {
		return null;
	}

	@Override
	public BernsteinPolynomial multiply(double value) {
		BernsteinPolynomial[] coeffs = new BernsteinPolynomial[bernsteinCoeffs.length];
		for (int i = 0; i < bernsteinCoeffs.length; i++) {
			coeffs[i] = bernsteinCoeffs[i] != null ? bernsteinCoeffs[i].multiply(value) : null;
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
		BernsteinPolynomial[] coeffs = new BernsteinPolynomial[bernsteinCoeffs.length];
		if (bernsteinPolynomial.numberOfVariables() == 2) {
			BernsteinPolynomial2Var bpoly2Var =
					(BernsteinPolynomial2Var) bernsteinPolynomial;
			for (int i = 0; i < bernsteinCoeffs.length; i++) {
				BernsteinPolynomial coeff = bernsteinCoeffs[i];
				BernsteinPolynomial otherCoeff = bpoly2Var.bernsteinCoeffs[i];
				if (coeff != null && otherCoeff != null) {
					coeffs[i] = coeff.plus(otherCoeff);
				} else if (coeff == null) {
					coeffs[i] = otherCoeff;
				} else {
					coeffs[i] = coeff;

				}
			}
		} else {
			throw new IllegalArgumentException("Incompatible addition");
		}
		return newInstance(coeffs);
	}

	@Override
	public int numberOfVariables() {
		return 2;
	}

	@Override
	public boolean hasSolution() {
		return false;
	}

	@Override
	public BernsteinPolynomial derivative(String variable) {
		if ("x".equals(variable)) {
			return derivativeX();
		}
		return derivativeY();
	}

	@Override
	public BernsteinPolynomial[] split() {
		BernsteinPolynomialCache bPlus = new BernsteinPolynomialCache(degreeX + 1);
		BernsteinPolynomialCache bMinus = new BernsteinPolynomialCache(degreeX + 1);

		for (int i = 0; i < degreeX + 1; i++) {
			BernsteinPolynomial[] coeffs = new BernsteinPolynomial[1];
			coeffs[0] = bernsteinCoeffs[i].divide(MyMath.binomial(degreeX, i));
			bPlus.setLast(i, newInstance(coeffs));
			bMinus.setLast(i, newInstance(coeffs));
		}

		for (int i = 1; i <= degreeX + 1; i++) {
			for (int j = degreeX - i; j >= 0; j--) {
				bPlus.set(j, bPlus.last[j].multiplyByOneMinusX()
						.plus(
								bPlus.last[j].plus(bPlus.last[j + 1])
										.multiplyByX().divide(2)
						)
				);

				bMinus.set(j,
						bMinus.last[j].plus(bMinus.last[j + 1]).multiplyByOneMinusX()
								.divide(2).plus(bMinus.last[j + 1].multiplyByX()));
			}
			bPlus.update();
			bMinus.update();
		}
		return new BernsteinPolynomial[]{bPlus.last[0], bMinus.last[0]};
	}

	private BernsteinPolynomial newInstance(BernsteinPolynomial[] coeffs) {
		return new BernsteinPolynomial2Var(coeffs, min, max, degreeX);
	}

	@Override
	public BernsteinPolynomial[][] split2D() {
		BernsteinPolynomial[] mainSplit = split();
		BernsteinPolynomial2Var bPlus = (BernsteinPolynomial2Var) mainSplit[0];
		BernsteinPolynomial2Var bMinus = (BernsteinPolynomial2Var) mainSplit[1];
		return new BernsteinPolynomial[][] {bPlus.splitCoefficients(), bMinus.splitCoefficients()};
	}

	private BernsteinPolynomial[] splitCoefficients() {
		BernsteinPolynomial[] bPlusCoeffs = new BernsteinPolynomial[degreeX + 1];
		BernsteinPolynomial[] bMinusCoeffs = new BernsteinPolynomial[degreeX + 1];

		for (int i = 0; i < bernsteinCoeffs.length; i++) {
			BernsteinPolynomial coeff = bernsteinCoeffs[i];
			if (coeff != null) {
				BernsteinPolynomial[] splitCoeffs = coeff.split();
				bPlusCoeffs[i] = splitCoeffs[0];
				bMinusCoeffs[i] = splitCoeffs[1];
			}
		}

		BernsteinPolynomial bPlusInY =
				new BernsteinPolynomial2Var(bPlusCoeffs, min, max, degreeX);

		BernsteinPolynomial bMinusInY =
				new BernsteinPolynomial2Var(bMinusCoeffs, min, max, degreeX);

		return new BernsteinPolynomial[] {bPlusInY, bMinusInY};
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
		BernsteinPolynomial[] shifted = new BernsteinPolynomial[bernsteinCoeffs.length + 1];
		System.arraycopy(bernsteinCoeffs, 0, shifted, destPos, bernsteinCoeffs.length);
		return newInstance(shifted);
	}

	private BernsteinPolynomial derivativeX() {
		BernsteinPolynomial[] derivedCoeffs = new BernsteinPolynomial[degreeX];
		for (int i = 0; i < degreeX; i++) {
			BernsteinPolynomial b1 = bernsteinCoeffs[i].multiply(-(degreeX - i));
			BernsteinPolynomial b2 = bernsteinCoeffs[i + 1].multiply(i + 1);
			derivedCoeffs[i] = b2.plus(b1);
		}
		return new BernsteinPolynomial2Var(derivedCoeffs, min, max, degreeX - 1);
	}

	private BernsteinPolynomial derivativeY() {
		BernsteinPolynomial[] derivedCoeffs = new BernsteinPolynomial[degreeX + 1];
		for (int i = 0; i <= degreeX; i++) {
			BernsteinPolynomial b2 = bernsteinCoeffs[i];
			derivedCoeffs[i] = b2.derivative();
		}
		return new BernsteinPolynomial2Var(derivedCoeffs, min, max, degreeX);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = degreeX; i >= 0; i--) {
			BernsteinPolynomial c = i < bernsteinCoeffs.length ? bernsteinCoeffs[i] : null;
			if (c == null || "0".equals(c.toString())) {
				continue;
			}
			String fs = sb.length() == 0 ? "" : "+";
			sb.append(fs);
			if (degreeX > 0 && !c.isConstant()) {
				sb.append(" (");
				sb.append(c);
				sb.append(") ");
			} else if (!"1".equals(c.toString())) {
				sb.append(c);
			} else {
				sb.append(" ");
			}

			String powerX = powerString("x", i);
			String powerOneMinusX = powerString("(1 - x)", degreeX - i);
			sb.append(powerX);
			if (!powerX.isEmpty()) {
				sb.append(" ");
			}
			sb.append(powerOneMinusX);
			if (!powerOneMinusX.isEmpty()) {
				sb.append(" ");
			}
		}
		String trimmed = sb.toString().trim();
		return trimmed.isEmpty() ? "0" : trimmed;
	}

	@Override
	public boolean isConstant() {
		return bernsteinCoeffs.length == 1;
	}
}
