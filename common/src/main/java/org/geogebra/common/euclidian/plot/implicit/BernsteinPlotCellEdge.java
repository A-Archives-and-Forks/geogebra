package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.Splittable;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.util.DoubleUtil;

/**
 * Edge of a cell to find solutions on it.
 * Edge is represented by a one variable polynomial substituting x or y variables
 * of the parent cell polynomial.
 * Solution can be found to split this edge and checking the derivative.
 */
public final class BernsteinPlotCellEdge implements Splittable<BernsteinPlotCellEdge> {
	final BernsteinPlotCell parent;
	private final BernsteinPolynomial polynomial;
	private final double coordMin;
	private final double coordMax;
	private final double fixedCoord;
	private final double length;
	private final EdgeKind kind;
	private GPoint2D startPoint = null;


	public static BernsteinPlotCellEdge create(BernsteinPlotCell parent, BernsteinPolynomial polynomial, double coordMin, double coordMax,
			double fixedCoord, EdgeKind kind) {

		String varName = kind.isHorizontal() ? "y" : "x";
		return new BernsteinPlotCellEdge(parent, polynomial.substitute(varName, fixedCoord), coordMin, coordMax,
				fixedCoord, kind);
	}

	private BernsteinPlotCellEdge(BernsteinPlotCell parent, BernsteinPolynomial polynomial, double coordMin, double coordMax, double fixedCoord,
			EdgeKind kind) {
		this.parent = parent;
		this.polynomial = polynomial;
		this.coordMin = coordMin;
		this.coordMax = coordMax;
		this.fixedCoord = fixedCoord;
		this.kind = kind;
		length = coordMax - coordMin;
	}

	public BernsteinPlotCellEdge[] split() {
		BernsteinPolynomial[] polynomials = polynomial.split();
		BernsteinPlotCellEdge[] edges = new BernsteinPlotCellEdge[2];
		double half = length / 2;
		edges[0] = new BernsteinPlotCellEdge(parent, polynomials[0], coordMin, coordMin + half, fixedCoord, kind);
		edges[1] = new BernsteinPlotCellEdge(parent, polynomials[1], coordMin + half, coordMax, fixedCoord, kind);
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
		if (startPoint == null) {
			startPoint = kind.isHorizontal()
					? new GPoint2D(coordMin, fixedCoord)
					: new GPoint2D(fixedCoord, coordMax);
		}
		return startPoint;
	}

	public double length() {
		return length;
	}

	public EdgeKind getKind() {
		return kind;
	}

	public boolean isHorizontal() {
		return kind.isHorizontal();
	}

	public boolean hasIntersect() {
		GPoint2D p = startPoint();
		double eps =1E-4;
		return !(isHorizontalEqual(p, eps) || isVerticalEqual(p, eps));
	}

	private boolean isVerticalEqual(GPoint2D p, double eps) {
		return DoubleUtil.isEqual(p.x, parent.boundingBox.getX1(), eps)
				&& DoubleUtil.isEqual(p.y, parent.boundingBox.getY2(), eps)
				|| DoubleUtil.isEqual(p.x, parent.boundingBox.getX2(), eps)
				&& DoubleUtil.isEqual(p.y, parent.boundingBox.getY2(), eps);
	}

	private boolean isHorizontalEqual(GPoint2D p, double eps) {
		return DoubleUtil.isEqual(p.x, parent.boundingBox.getX1(), eps)
				&& DoubleUtil.isEqual(p.y, parent.boundingBox.getY1(), eps)
				|| DoubleUtil.isEqual(p.x, parent.boundingBox.getX2(), eps)
				&& DoubleUtil.isEqual(p.y, parent.boundingBox.getY1(), eps) ;
	}
}
