package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;

public final class BernsteinPolynomial1Var implements BernsteinPolynomial {
	private final double min;
	private final double max;
	final int degree;
	final char variableName;
	private final double[] bernsteinCoeffs;

	public BernsteinPolynomial1Var(double[] bernsteinCoeffs,
			char variableName, double min, double max) {
		this.variableName = variableName;
		this.min = min;
		this.max = max;
		this.degree = bernsteinCoeffs.length - 1;
		this.bernsteinCoeffs = bernsteinCoeffs;
	}

	public BernsteinPolynomial1Var(int degree, char variableName, double min,
			double max) {
		this(new double[degree + 1], variableName, min, max);
	}


	@Override
	public double evaluate(double value) {
		double[] partialEval = new double[degree + 1];
		double[] lastPartialEval = new double[degree + 1];
		double scaledValue = (value - min) / (max - min);
		double oneMinusScaledValue = 1 - scaledValue;

		copyArrayTo(bernsteinCoeffs, lastPartialEval);
		divWithBinomials(lastPartialEval);

		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				partialEval[j] = oneMinusScaledValue * lastPartialEval[j]
						+ scaledValue * lastPartialEval[j + 1];
			}
			copyArrayTo(partialEval, lastPartialEval);
		}
		return partialEval[0];
	}

	private void divWithBinomials(double[] lastPartialEval) {
		for (int i = 0; i < lastPartialEval.length; i++) {
			lastPartialEval[i] = lastPartialEval[i] / MyMath.binomial(degree, i);
		}
	}

	@Override
	public BernsteinPolynomial[] split() {
		BernsteinPolynomialCache bPlus = new BernsteinPolynomialCache(degree + 1);
		BernsteinPolynomialCache bMinus = new BernsteinPolynomialCache(degree + 1);

		for (int i = 0; i < degree + 1; i++) {
			double[] coeffs = new double[1];
			coeffs[0] = bernsteinCoeffs[i] / MyMath.binomial(degree, i);
			bPlus.setLast(i, newInstance(coeffs));
			bMinus.setLast(i, newInstance(coeffs));
		}

		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				bPlus.set(j,
						bPlus.last[j].multiplyByOneMinusX()
						.plus(bPlus.last[j].plus(bPlus.last[j + 1]).multiplyByX().multiply(0.5)));

				bMinus.set(j,
						bMinus.last[j].plus(bMinus.last[j + 1]).multiplyByOneMinusX()
						.multiply(0.5).plus(bMinus.last[j + 1].multiplyByX()));
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
	public BernsteinPolynomial plus(double value) {
		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] + value;
		}
		return newInstance(coeffs);
	}

	@Override
	public BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial) {
		if (bernsteinPolynomial == null) {
			return this;
		}
		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i]
					+ ((BernsteinPolynomial1Var) bernsteinPolynomial).bernsteinCoeffs[i];
		}
		return newInstance(coeffs);
	}

	@Override
	public BernsteinPolynomial derivative(String variable) {
		return derivative();
	}

	@Override
	public BernsteinPolynomial minus(BernsteinPolynomial bernsteinPolynomial) {
		return plus(bernsteinPolynomial.multiply(-1));
	}

	@Override
	public BernsteinPolynomial multiplyByOneMinusX() {
		return shiftFrom(0);
	}

	@Override
	public BernsteinPolynomial multiplyByX(){
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
	public String toString() {
		if (isConstant()) {
			return "" + (int) bernsteinCoeffs[0];
		}
		StringBuilder sb = new StringBuilder();
		for (int i = degree; i >= 0; i--) {
			double c = bernsteinCoeffs[i];
			if (c == 0) {
				continue;
			}
			String fs = sb.length() == 0 && c > 0 ? ""
					: c > 0 ? "+ " : "- ";
			sb.append(fs);
			if (c != 1 && c != -1) {
				double abs = Math.abs(c);
				sb.append((abs - (int) abs) > 1E-6 ? abs : String.valueOf((int) abs));
			}
			String powerX = powerString(variableName + "", i);
			String powerOneMinusX = powerString("(1 - " + variableName + ")", degree - i);
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
		return trimmed.isEmpty() ? "0": trimmed;
	}

	static String powerString(String base, int i) {
		if (i == 0) {
			return "";
		}
		if (i == 1) {
			return base;
		}
		return base + StringUtil.numberToIndex(i);
	}
}