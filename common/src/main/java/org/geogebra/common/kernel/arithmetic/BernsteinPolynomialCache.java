package org.geogebra.common.kernel.arithmetic;

public final class BernsteinPolynomialCache {
	BernsteinPolynomial[] current;
	BernsteinPolynomial[] last;

	public BernsteinPolynomialCache(int size) {
		current = new BernsteinPolynomial[size];
		last = new BernsteinPolynomial[size];
	}

	public void update() {
		System.arraycopy(current, 0, last, 0, last.length);
	}

	public void set(int i, BernsteinPolynomial bernsteinPolynomial) {
		current[i] = bernsteinPolynomial;
	}

	public void setLast(int i, BernsteinPolynomial bernsteinPolynomial) {
		last[i] = bernsteinPolynomial;
	}
}
