package org.geogebra.common.kernel.arithmetic;


import static org.geogebra.common.kernel.arithmetic.BernsteinPolynomial1Var.powerString;

import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial2Var implements BernsteinPolynomial {
	private final double min;
	private final double max;
	private final int degreeX;
	private final int degreeY;
	private final int degree;
	private final BernsteinPolynomial[][] bernsteinCoeffs;
	public BernsteinPolynomial2Var(BernsteinPolynomial[][] bernsteinCoeffs, double min, double max,
			int degreeX, int degreeY) {
		this.min = min;
		this.max = max;
		this.degreeX = degreeX;
		this.degreeY = degreeY;
		this.degree = Math.max(degreeX, degreeY);
		this.bernsteinCoeffs = bernsteinCoeffs;
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
	public BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial) {
		return null;
	}

	@Override
	public BernsteinPolynomial derivative(String variable) {
		if ("x".equals(variable)) {
			return derivativeX();
		}
		return derivativeY();
	}

	@Override
	public BernsteinPolynomial minus(BernsteinPolynomial bernsteinPolynomial) {
		return null;
	}

	private BernsteinPolynomial derivativeX() {
		BernsteinPolynomial[] derivedCoeffs = new BernsteinPolynomial[degree];
		for (int i = 0; i < degree; i++) {
			BernsteinPolynomial b1 = bernsteinCoeffs[degree][i].multiply(degree - i);
			BernsteinPolynomial b2 = bernsteinCoeffs[degree][i + 1].multiply(i + 1);
			derivedCoeffs[i] = b2.minus(b1);
		}
		return new BernsteinPolynomial2Var(toMatrix(derivedCoeffs),
				min, max, degreeX -1, degreeY);
	}


	private static BernsteinPolynomial[][] toMatrix(BernsteinPolynomial[] derivedCoeffs) {
		int degree = derivedCoeffs.length - 1;
		BernsteinPolynomial[][] bernsteinCoeffs = new BernsteinPolynomial[degree + 1][degree + 1];
		bernsteinCoeffs[degree] = derivedCoeffs;
		for (int i = degree - 1; i > 0; i--) {
			for (int j = degree; j > i ; j--) {
				bernsteinCoeffs[i][j] = derivedCoeffs[i];
			}
		}
		return bernsteinCoeffs;
	}


	private BernsteinPolynomial derivativeY() {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = degreeX; i >= 0; i--) {
			BernsteinPolynomial c = bernsteinCoeffs[degreeX][i];
			if ("0".equals(c.toString())) {
				continue;
			}
			String fs = i == degreeX ? "" : "+";
			sb.append(fs);
			if (degreeX > 1) {
				sb.append(" (");
				sb.append(c);
				sb.append(") ");
			} else {
				sb.append(c);
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
