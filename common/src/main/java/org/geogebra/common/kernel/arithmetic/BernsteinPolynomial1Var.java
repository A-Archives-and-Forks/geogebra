package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.util.StringUtil;

public class BernsteinPolynomial1Var implements BernsteinPolynomial {
	private final double min;
	private final double max;
	final int degree;
	final char variableName;
	private final double[][] bernsteinCoeffs;

	public BernsteinPolynomial1Var(double[][] bernsteinCoeffs,
			char variableName, double min, double max) {
		this.variableName = variableName;
		this.min = min;
		this.max = max;
		this.degree = bernsteinCoeffs.length - 1;
		this.bernsteinCoeffs = bernsteinCoeffs;
	}

	public BernsteinPolynomial1Var(double[] derivedCoeffs, char variableName, double min,
			double max) {
		this(toMatrix(derivedCoeffs), variableName, min, max);

	}
	private static double[][] toMatrix(double[] derivedCoeffs) {
		int degree = derivedCoeffs.length - 1;
		double[][] bernsteinCoeffs = new double[degree + 1][degree + 1];
		bernsteinCoeffs[degree] = derivedCoeffs;
		for (int i = degree - 1; i > 0; i--) {
			for (int j = degree; j > i ; j--) {
				bernsteinCoeffs[i][j] = derivedCoeffs[i];
			}
		}
		return bernsteinCoeffs;
	}

	public BernsteinPolynomial1Var(int degree, char variableName, double min,
			double max) {
		this(new double[degree + 1][degree + 1], variableName, min, max);

	}

	@Override
	public double evaluate(double value) {
		double scaledValue = (value - min) / (max - min);
		double result = 0;
		for (int i = degree; i >= 0; i--) {
			result += (bernsteinCoeffs[degree][i]
					* Math.pow(scaledValue, i)
					* Math.pow(1 - scaledValue, degree - i));
		}
		return result;
	}

	@Override
	public BernsteinPolynomial derivative() {
		double[] derivedCoeffs = new double[degree];
		for (int i = 0; i < degree; i++) {
			double b1 = (degree - i) * bernsteinCoeffs[degree][i];
			double b2 = (i + 1) * bernsteinCoeffs[degree][i + 1];
			derivedCoeffs[i] = b2 - b1;
		}

		return newInstance(derivedCoeffs);
	}

	@Override
	public BernsteinPolynomial multiply(double value) {
		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[degree][i] * value;
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
			coeffs[i] = bernsteinCoeffs[degree][i] + value;
		}
		return newInstance(coeffs);
	}

	@Override
	public BernsteinPolynomial plus(BernsteinPolynomial value) {
		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[degree][i]
					+ ((BernsteinPolynomial1Var) value).bernsteinCoeffs[degree][i];
		}
		return newInstance(coeffs);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = degree; i >= 0; i--) {
			double c = bernsteinCoeffs[degree][i];
			if (c == 0) {
				continue;
			}
			String fs = i == degree ? ""
					: c > 0 ? "+ " : " - ";
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
		return "".equals(trimmed) ? "0": trimmed;
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
