package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;

public final class BoxEdge implements Splittable<BoxEdge> {
	private final BernsteinPolynomial polynomial;
	private final double coordMin;
	private final double coordMax;
	private final double fixedCoord;
	private final double length;
	private final EdgeKind kind;


	public static BoxEdge create(BernsteinPolynomial polynomial, double coordMin, double coordMax,
			double fixedCoord, EdgeKind kind) {

		String varName = kind.isHorizontal() ? "y" : "x";
		return new BoxEdge(polynomial.substitute(varName, fixedCoord), coordMin, coordMax,
				fixedCoord, kind);
	}

	private BoxEdge(BernsteinPolynomial polynomial, double coordMin, double coordMax, double fixedCoord,
			EdgeKind kind) {
		this.polynomial = polynomial;
		this.coordMin = coordMin;
		this.coordMax = coordMax;
		this.fixedCoord = fixedCoord;
		this.kind = kind;
		length = coordMax - coordMin;
	}

	public BoxEdge[] split() {
		BernsteinPolynomial[] polynomials = polynomial.split();
		BoxEdge[] edges = new BoxEdge[2];
		double half = length / 2;
		edges[0] = new BoxEdge(polynomials[0], coordMin, coordMin + half, fixedCoord, kind);
		edges[1] = new BoxEdge(polynomials[1], coordMin + half, coordMax, fixedCoord, kind);
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
		return length <= (kind.isHorizontal() ? pixelInRW.x : pixelInRW.y);
	}

	public GPoint2D startPoint() {
		return kind.isHorizontal()
				? new GPoint2D(coordMin, fixedCoord)
				: new GPoint2D(fixedCoord, coordMax);
	}

	public double length() {
		return length;
	}

	public EdgeKind getKind() {
		return kind;
	}

	public void draw(GGraphics2D g2, EuclidianViewBounds bounds) {
		g2.setColor(kind.getColor());
		if (kind.isHorizontal()) {
			g2.fillRect((int) bounds.toScreenCoordXd(coordMin) + 4,
					(int) bounds.toScreenCoordYd(fixedCoord) + (kind == EdgeKind.TOP ? -2 : 2),
					(int) (bounds.toScreenCoordXd(coordMax)
							- bounds.toScreenCoordXd(coordMin)) - 4, 2);
		} else {
			double height = bounds.toScreenCoordYd(coordMax)
					- bounds.toScreenCoordYd(coordMin);

			g2.fillRect((int) bounds.toScreenCoordXd(fixedCoord)
							+ (kind == EdgeKind.LEFT ? 2 : -2),
					(int) bounds.toScreenCoordYd(coordMax),
					2, (int) height);

		}
	}
}
