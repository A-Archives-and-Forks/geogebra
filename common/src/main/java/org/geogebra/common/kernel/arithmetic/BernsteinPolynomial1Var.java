package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

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

		return new BernsteinPolynomial1Var(derivedCoeffs,
				variableName, min, max);
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
		}
		return sb.toString().trim();
	}

	private String powerString(String base, int i) {
		if (i == 0) {
			return "";
		}
		if (i == 1) {
			return base;
		}
		return base + StringUtil.numberToIndex(i);
	}

	void debugBernsteinCoeffs() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= degree; i++) {
			sb.append("(");
			String fs = "";
			for (int j = 0; j <= i; j++) {
				sb.append(fs);
				fs=", ";
				sb.append(bernsteinCoeffs[i][j]);
			}
			sb.append(")\n");
		}
		Log.debug(sb);
	}
}
