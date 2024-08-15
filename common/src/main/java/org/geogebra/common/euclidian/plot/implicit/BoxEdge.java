package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;

public final class BoxEdge implements Splittable<BoxEdge> {
	private final BernsteinPolynomial polynomial;
	private final double coordMin;
	private final double coordMax;
	private final double fixedCoord;
	private final boolean horizontal;
	private final double length;

	public static BoxEdge createHorizontal(BernsteinPolynomial polynomial, double x1, double x2,
			double y) {

		return new BoxEdge(polynomial.substitute("y", y),
				x1, x2, y, true);
	}

	public static BoxEdge createVertical(BernsteinPolynomial polynomial, double y1, double y2,
			double x) {
		return new BoxEdge(polynomial.substitute("x", x), y1, y2, x, false);
	}

	private BoxEdge(BernsteinPolynomial polynomial, double coordMin, double coordMax, double fixedCoord,
			boolean horizontal) {
		this.polynomial = polynomial;
		this.coordMin = coordMin;
		this.coordMax = coordMax;
		this.fixedCoord = fixedCoord;
		this.horizontal = horizontal;
		length = coordMax - coordMin;
	}

	public BoxEdge[] split() {
		BernsteinPolynomial[] polynomials = polynomial.split();
		BoxEdge[] edges = new BoxEdge[2];
		double half = length / 2;
		edges[0] = new BoxEdge(polynomials[0], coordMin, coordMin + half, fixedCoord, horizontal);
		edges[1] = new BoxEdge(polynomials[1], coordMin + half, coordMax, fixedCoord, horizontal);
		return edges;
	}

	@Override
	public String toString() {
		return "HorizontalEdge{" +
				"polynomial=" + polynomial +
				", x1=" + coordMin +
				", x2=" + coordMax +
				", y=" + fixedCoord +
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

	public boolean isUnderSize(GPoint2D pixelInRW) {
		return length < (horizontal ? pixelInRW.x : pixelInRW.y);
	}

	public GPoint2D startPoint() {
		return horizontal ? new GPoint2D(coordMin, fixedCoord) : new GPoint2D(fixedCoord, coordMin);
	}

	public double length() {
		return length;
	}
}
