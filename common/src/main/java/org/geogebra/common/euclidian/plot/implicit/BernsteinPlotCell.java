package org.geogebra.common.euclidian.plot.implicit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;

public class BernsteinPlotCell implements Splittable<BernsteinPlotCell> {
	BernsteinBoundingBox boundingBox;
	BernsteinPolynomial polynomial;
	private final BernsteinPolynomial dx;
	private final BernsteinPolynomial dy;
	private CurvePlotCellKind kind = CurvePlotCellKind.NONE;
	private final Map<EdgeKind, BoxEdge> edges = new HashMap<>();

	CurvePlotCellKind getKind() {
		return kind;
	}

	public void markCellUnused() {
		kind = CurvePlotCellKind.NONE;
	}

	public Collection<BoxEdge> getEdges() {
		return edges.values();
	}

	public GPoint2D center() {
		return new GPoint2D(boundingBox.getXHalf(), boundingBox.getYHalf());
	}


	enum CurvePlotCellKind {
		NONE,
		CELL0,
		CELL1,
		CELL2

	}
	public BernsteinPlotCell(BernsteinBoundingBox box, BernsteinPolynomial polynomial) {
		boundingBox = box;
		this.polynomial = polynomial;
		dx = polynomial.derivative("x");
		dy = polynomial.derivative("y");
		kind = classify();
	}

	@Override
	public BernsteinPlotCell[] split() {
		BernsteinPolynomial[][] polynomials = polynomial.split2D();
		BernsteinBoundingBox[] boxes = boundingBox.split();
		BernsteinPlotCell[] contexts = new BernsteinPlotCell[4];
		contexts[0] = new BernsteinPlotCell(boxes[0], polynomials[0][0]);
		contexts[1] = new BernsteinPlotCell(boxes[1], polynomials[1][0]);
		contexts[2] = new BernsteinPlotCell(boxes[2], polynomials[0][1]);
		contexts[3] = new BernsteinPlotCell(boxes[3], polynomials[1][1]);
		return contexts;
	}

	private CurvePlotCellKind classify() {
		if (polynomial.hasNoSolution()) {
			return CurvePlotCellKind.CELL2;
		}

		if (dx.hasNoSolution() != dy.hasNoSolution()) {
			return CurvePlotCellKind.CELL1;
		}

		return CurvePlotCellKind.CELL0;
	}



	void createEdges() {
		edges.put(EdgeKind.TOP, BoxEdge.create(this, polynomial, boundingBox.getX1(),
				boundingBox.getX2(),
				boundingBox.getY1(), EdgeKind.TOP));

		edges.put(EdgeKind.LEFT, BoxEdge.create(this, polynomial, boundingBox.getY1(), boundingBox.getY2(),
				boundingBox.getX1(), EdgeKind.LEFT));

		edges.put(EdgeKind.BOTTOM, BoxEdge.create(this, polynomial, boundingBox.getX1(),
				boundingBox.getX2(),
				boundingBox.getY2(), EdgeKind.BOTTOM));

		edges.put(EdgeKind.RIGHT, BoxEdge.create(this, polynomial, boundingBox.getY1(), boundingBox.getY2(),
				boundingBox.getX2(), EdgeKind.RIGHT));

	}


	@Override
	public String toString() {
		return "CurvePlotContext{" +
				"boundingBox=" + boundingBox +
				", polynomial=" + polynomial +
				", contextCass=" + kind +
				'}';
	}

	public boolean mightHaveSolution() {
		return kind != CurvePlotCellKind.CELL2;
	}
}
