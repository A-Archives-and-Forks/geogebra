package org.geogebra.common.kernel.arithmetic;

public class BernsteinCoefficientCache {
	double[][] current;
	double[][] last;

	public BernsteinCoefficientCache(int size) {
		current = new double[size + 1][size + 1];
		last = new double[size + 1][size + 1];
	}


	public void update() {
		System.arraycopy(current, 0, last, 0, last.length);
	}

	public void set(int i, double[] coeffs) {
		current[i] = coeffs;
	}

	public void setLast(int i, double[] coeffs) {
		last[i] = coeffs;
	}

}
