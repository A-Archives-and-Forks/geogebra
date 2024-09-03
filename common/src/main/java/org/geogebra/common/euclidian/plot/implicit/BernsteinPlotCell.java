package org.geogebra.common.euclidian.plot.implicit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;

public class BernsteinPlotCell implements Splittable<BernsteinPlotCell> {
	final BernsteinBoundingBox boundingBox;
	final BernsteinPolynomial polynomial;
	private final BernsteinPlotCellKind kind;
	private final Map<EdgeKind, BoxEdge> edges = new HashMap<>();

	BernsteinPlotCellKind getKind() {
		return kind;
	}

	public Collection<BoxEdge> getEdges() {
		return edges.values();
	}

	public GPoint2D center() {
		return new GPoint2D(boundingBox.getXHalf(), boundingBox.getYHalf());
	}


	enum BernsteinPlotCellKind {
		CELL0,
		CELL1,
		CELL2

	}

	public BernsteinPlotCell(BernsteinBoundingBox box, BernsteinPolynomial polynomial) {
		boundingBox = box;
		this.polynomial = polynomial;
		kind = classify();
	}

	private BernsteinPlotCellKind classify() {
		if (polynomial.hasNoSolution()) {
			return BernsteinPlotCellKind.CELL2;
		}

		BernsteinPolynomial dx = polynomial.derivative("x");
		BernsteinPolynomial dy = polynomial.derivative("y");

		if (dx.hasNoSolution() != dy.hasNoSolution()) {
			return BernsteinPlotCellKind.CELL1;
		}

		return BernsteinPlotCellKind.CELL0;
	}

	@Override
	public BernsteinPlotCell[] split() {
		BernsteinPolynomial[][] polynomials = polynomial.split2D();
		BernsteinBoundingBox[] boxes = boundingBox.split();
		BernsteinPlotCell[] cells = new BernsteinPlotCell[4];
		cells[0] = new BernsteinPlotCell(boxes[0], polynomials[0][0]);
		cells[1] = new BernsteinPlotCell(boxes[1], polynomials[1][0]);
		cells[2] = new BernsteinPlotCell(boxes[2], polynomials[0][1]);
		cells[3] = new BernsteinPlotCell(boxes[3], polynomials[1][1]);
		return cells;
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
		return kind != BernsteinPlotCellKind.CELL2;
	}
}
