package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomialFormatter {
	private final BernsteinPolynomial1Var bernstein;

	public BernsteinPolynomialFormatter(BernsteinPolynomial1Var bernstein) {
		this.bernstein = bernstein;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int degree = bernstein.degree;
		for (int i = degree; i >= 0; i--) {
			double c = bernstein.getBernsteinCoefficient(degree, i);
			if (c == 0) {
				continue;
			}
			String fs = i == degree ? ""
					: c > 0 ? "+ " : " - ";
			sb.append(fs);
			if (c != 1 && c != -1) {
				sb.append((int) Math.abs(c));
			}
			String powerX = powerString(bernstein.variableName + "", i);
			String powerOneMinusX = powerString("(1 - " + bernstein.variableName + ")", degree - i);
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
		for (int i = 0; i <= bernstein.degree; i++) {
			sb.append("(");
			String fs = "";
			for (int j = 0; j <= i; j++) {
				sb.append(fs);
				fs=", ";
				sb.append(bernstein.getBernsteinCoefficient(i, j));
			}
			sb.append(")\n");
		}
		Log.debug(sb);
	}

}
