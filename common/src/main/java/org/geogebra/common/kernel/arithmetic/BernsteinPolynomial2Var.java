package org.geogebra.common.kernel.arithmetic;


import static org.geogebra.common.kernel.arithmetic.BernsteinPolynomial1Var.powerString;

import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial2Var implements BernsteinPolynomial {
	private final double min;
	private final double max;
	private final int degree;
	private final BernsteinPolynomial[][] bernsteinCoeffs;
	public BernsteinPolynomial2Var(BernsteinPolynomial[][] bernsteinCoeffs, double min, double max,
			int degreeX, int degreeY) {
		this.min = min;
		this.max = max;
		this.degree = Math.max(degreeX, degreeY);
		this.bernsteinCoeffs = bernsteinCoeffs;
//		debugBernsteinCoeffs();
	}

	public String coeffsToString(BernsteinPolynomial[][] bernsteinCoeffs) {
		StringBuilder sb = new StringBuilder();
		String fs = "";
		for (BernsteinPolynomial coeff : bernsteinCoeffs[degree]) {
			sb.append(fs);
			fs = ", ";
			sb.append(coeff);
		}
		return sb.toString();
	}

	private void debugBernsteinCoeffs() {
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

	public double evaluate(double valueX, double valueY) {
		double scaledValue = (valueX - min) / (max - min);
		double result = 0;

		for (int i = degree; i >= 0; i--) {
			double coeff = bernsteinCoeffs[degree][i].evaluate(valueY);
			result += (coeff
					* Math.pow(scaledValue, i)
					* Math.pow(1 - scaledValue, degree - i));
		}
		return result;
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
		return null;
	}

	@Override
	public BernsteinPolynomial plus(double value) {
		return null;
	}

	@Override
	public BernsteinPolynomial plus(BernsteinPolynomial value) {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = degree; i >= 0; i--) {
			BernsteinPolynomial c = bernsteinCoeffs[degree][i];
			if (c == null) {
				continue;
			}
			String fs = i == degree ? "" : "+";
			sb.append(fs);
			sb.append(" (");
			sb.append(c);
			sb.append(") ");
			String powerX = powerString("x", i);
			String powerOneMinusX = powerString("(1 - x)", degree - i);
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

	public static String debugArray(double... array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append("(");
			String fs = "";
			for (int j = 0; j <= i; j++) {
				sb.append(fs);
				fs=", ";
				sb.append(array[i]);
			}
			sb.append(")\n");
		}
		return sb.toString();
	}
}
