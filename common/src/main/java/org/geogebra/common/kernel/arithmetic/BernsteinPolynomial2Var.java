package org.geogebra.common.kernel.arithmetic;


import static org.geogebra.common.kernel.arithmetic.BernsteinPolynomial1Var.powerString;

import java.util.Arrays;


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
		BernsteinPolynomial[] derivedCoeffs = new BernsteinPolynomial[degreeX];
		for (int i = 0; i < degreeX; i++) {
			BernsteinPolynomial b1 = bernsteinCoeffs[degreeX][i].multiply(degreeX - i);
			BernsteinPolynomial b2 = bernsteinCoeffs[degreeX][i + 1].multiply(i + 1);
			derivedCoeffs[i] = b2.minus(b1);
		}
		return new BernsteinPolynomial2Var(toMatrix(derivedCoeffs),
				min, max, degreeX -1, degreeY);
	}


	private static BernsteinPolynomial[][] toMatrix(BernsteinPolynomial[] derivedCoeffs) {
		int degree = derivedCoeffs.length - 1;
		BernsteinPolynomial[][] bernsteinCoeffs = new BernsteinPolynomial[degree + 1][degree + 1];
		bernsteinCoeffs[degree] = derivedCoeffs;
		for (int i = degree; i > 0; i--) {
			bernsteinCoeffs[i - 1] = Arrays.copyOfRange(bernsteinCoeffs[i], 1, i + 1);
		}
		return bernsteinCoeffs;
	}


	private BernsteinPolynomial derivativeY() {
		BernsteinPolynomial[] derivedCoeffs = new BernsteinPolynomial[degreeX + 1];
		for (int i = 0; i <= degreeX; i++) {
			BernsteinPolynomial b2 = bernsteinCoeffs[degreeX][i];
			derivedCoeffs[i] = b2.derivative();
		}
		return new BernsteinPolynomial2Var(toMatrix(derivedCoeffs),
				min, max, degreeX, degreeY);
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
			if (degreeX > 0 && !c.isConstant()) {
				sb.append(" (");
				sb.append(c);
				sb.append(") ");
			} else if (!"1".equals(c.toString())){
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
		return "".equals(trimmed) ? "0": trimmed;
	}


	@Override
	public boolean isConstant() {
		return bernsteinCoeffs[degree].length == 1;
	}
}
