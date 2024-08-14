package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;

public class BoxEdge implements Splittable<BoxEdge> {
	BernsteinPolynomial polynomial;
	double size;
	boolean horizontal;

	public BoxEdge(BernsteinPolynomial polynomial, double size, boolean horizontal) {
		this.polynomial = polynomial;
		this.size = size;
		this.horizontal = horizontal;
	}

	@Override
	public BoxEdge[] split() {
		BernsteinPolynomial[] polynomials = polynomial.split();
		BoxEdge[] edges = new BoxEdge[2];
		edges[0] = new BoxEdge(polynomials[0], size / 2.0, horizontal);
		edges[1] = new BoxEdge(polynomials[1], size / 2.0, horizontal);
		return edges;
	}

	public boolean greaterThanLimit() {
		return size > 0.001;
	}

	@Override
	public String toString() {
		return "BoxEdge{" +
				"polynomial=" + polynomial +
				", size=" + size +
				", horizontal=" + horizontal +
				'}';
	}

	public boolean mightHaveSolutions() {
		return !polynomial.hasNoSolution();
	}

	public boolean isDerivativeSignDiffer() {
		BernsteinPolynomial dx = polynomial.derivative("x");
		BernsteinPolynomial dy = polynomial.derivative("y");
		return dx.getSign() != dy.getSign();
	}

}
