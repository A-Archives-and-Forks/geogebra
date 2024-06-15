package org.geogebra.common.kernel.arithmetic;


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
		debugBernsteinCoeffs();
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

	public double evaluate(double value) {
		double y = (value - min) / (max - min);
//		variable.set(y);
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
}
