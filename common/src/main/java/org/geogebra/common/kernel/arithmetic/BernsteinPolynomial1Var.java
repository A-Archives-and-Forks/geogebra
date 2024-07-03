package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

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
		for (int i = 0; i < lastPartialEval.length; i++) {
			lastPartialEval[i] = lastPartialEval[i] / MyMath.binomial(degree, i);
		}

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
	public BernsteinPolynomial[] split() {
		BernsteinPolynomial[] bPlus = new BernsteinPolynomial[degree + 1];
		BernsteinPolynomial[] lastBPlus = new BernsteinPolynomial[degree + 1];
		for (int i = 0; i < degree + 1; i++) {
			double[] coeffs = new double[degree + 1];
			coeffs[i] = bernsteinCoeffs[i] / MyMath.binomial(degree, i);
			lastBPlus[i] = new BernsteinPolynomial1Var(coeffs, variableName, min, max);
			Log.debug("B+_" + i + ": " + lastBPlus[i]);
		}
		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				bPlus[j] = lastBPlus[j].plus(
						lastBPlus[j].plus(lastBPlus[j + 1]).multiply(0.5));
			}
			System.arraycopy(bPlus, 0, lastBPlus, 0, lastBPlus.length);
		}

		return new BernsteinPolynomial[]{bPlus[0], null};
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

	private BernsteinPolynomial1Var newInstance(double[] coeffs) {
		return new BernsteinPolynomial1Var(coeffs, variableName, min, max);
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
	public boolean isConstant() {
		return bernsteinCoeffs.length == 1;
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
				sb.append((int) Math.abs(c));
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
