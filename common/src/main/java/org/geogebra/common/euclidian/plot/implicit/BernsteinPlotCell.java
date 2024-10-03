package org.geogebra.common.euclidian.plot.implicit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.Splittable;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.implicit.EdgeConfig;

/**
 * BernsteinPlotCell is the basic building block of the algorithm.
 * A cell consist a bounding box, a Bernstein polynomial limited to that bounding box.
 * The algo splits the screen into these cells, decides if there might be a solution in
 * that cell, and if there might be, splits it further until a given box size. If that minimal size
 * is reached, we declare that there is a solution in that cell.
 *
 */
public class BernsteinPlotCell implements Splittable<BernsteinPlotCell> {
	final BernsteinBoundingBox boundingBox;
	final BernsteinPolynomial polynomial;
	private final BernsteinPlotCellKind kind;
	private final Map<EdgeKind, BernsteinPlotCellEdge> edges = new HashMap<>();
	private EdgeConfig edgeConfig;

	/**
	 *
	 * @return kind: top, left, bottom, right,
	 */
	BernsteinPlotCellKind getKind() {
		return kind;
	}

	/**
	 *
	 * @return the edges of the cell.
	 */
	public Collection<BernsteinPlotCellEdge> getEdges() {
		return edges.values();
	}

	/**
	 *
	 * @return the center point of the cell.
	 */
	public GPoint2D center() {
		return new GPoint2D(boundingBox.getXHalf(),
				boundingBox.getYHalf());
	}

	enum BernsteinPlotCellKind {
		CELL0,
		CELL1,
		CELL2
	}

	/**
	 *
	 * @param box bounding box of the cell
	 * @param polynomial restricted to the cell.
	 */
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
		edges.put(EdgeKind.TOP, BernsteinPlotCellEdge.create(this, polynomial,
				boundingBox.x1(), boundingBox.x2(),
				boundingBox.y1(), EdgeKind.TOP));

		edges.put(EdgeKind.LEFT, BernsteinPlotCellEdge.create(this, polynomial,
				boundingBox.y1(), boundingBox.y2(),
				boundingBox.x1(), EdgeKind.LEFT));

		edges.put(EdgeKind.BOTTOM, BernsteinPlotCellEdge.create(this, polynomial,
				boundingBox.x1(), boundingBox.x2(),
				boundingBox.y2(), EdgeKind.BOTTOM));

		edges.put(EdgeKind.RIGHT, BernsteinPlotCellEdge.create(this, polynomial,
				boundingBox.y1(), boundingBox.y2(),
				boundingBox.x2(), EdgeKind.RIGHT));
	}

	@Override
	public String toString() {
		return "CurvePlotContext{"
				+ "boundingBox=" + boundingBox
				+ ", polynomial=" + polynomial
				+ ", contextCass=" + kind
				+ '}';
	}

	public boolean mightHaveSolution() {
		return kind != BernsteinPlotCellKind.CELL2;
	}

	public EdgeConfig getEdgeConfig() {
		return edgeConfig;
	}

	public void setEdgeConfig(EdgeConfig edgeConfig) {
		this.edgeConfig = edgeConfig;
	}
}
