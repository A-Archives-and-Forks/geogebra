package org.geogebra.common.kernel.arithmetic;


public class BernsteinPolynomial1Var implements BernsteinPolynomial {
	private final double xmin;
	private final double xmax;
	final int degree;
	final char variableName;
	private final double[][] bernsteinCoeffs;
	private BernsteinPolynomialFormatter formatter;

	public BernsteinPolynomial1Var(double[][] bernsteinCoeffs,
			char variableName, double xmin, double xmax) {
		this.variableName = variableName;
		this.xmin = xmin;
		this.xmax = xmax;
		this.degree = bernsteinCoeffs.length - 1;
		this.bernsteinCoeffs = bernsteinCoeffs;
		formatter = new BernsteinPolynomialFormatter(this);
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
		double scaledValue = (value - xmin) / (xmax - xmin);
		double result = 0;
		for (int i = degree; i >= 0; i--) {
			result += (bernsteinCoeffs[degree][i]
					* Math.pow(scaledValue, i)
					* Math.pow(1 - scaledValue, degree - i));
		}
		return result;
	}

	@Override
	public String coeffsToString() {
		return "";
	}

	@Override
	public ExpressionNode output() {
		return null;
	}

	@Override
	public String toString() {
		return formatter.toString();
	}

	@Override
	public BernsteinPolynomial derivative() {
		double[] derivedCoeffs = new double[degree];
//		double[] derivedCoeffs = new double[]{2,6};
		for (int i = 0; i < degree; i++) {
			double b1 = (degree - i) * bernsteinCoeffs[degree][i];
			double b2 = (i + 1) * bernsteinCoeffs[degree][i + 1];
			derivedCoeffs[i] = b2 - b1;
		}

		return new BernsteinPolynomial1Var(derivedCoeffs,
				variableName, xmin, xmax);
	}

	@Override
	public void addPowerBasis(int index, double value) {
		//powerBasisCoeffs[index] += value;
	}

	public double getBernsteinCoefficient(int i, int j) {
		return bernsteinCoeffs[i][j];
	}
}
