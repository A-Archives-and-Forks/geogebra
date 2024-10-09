package org.geogebra.common.euclidian.plot.implicit;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.implicit.PlotRect;

public class BernsteinPlotRect implements PlotRect {
	private final BernsteinBoundingBox box;
	private final double[] corners = new double[4];
	private final double[] evals = new double[4];
	private final Map<EdgeKind, GPoint2D> edgeSolutions;

	public BernsteinPlotRect(BernsteinPlotCell cell, BernsteinPolynomial originalPolynomial) {
		this.box = cell.boundingBox;
		corners[0] = originalPolynomial.evaluate(x1(), y1());
		corners[1] = originalPolynomial.evaluate(x2(), y1());
		corners[2] = originalPolynomial.evaluate(x2(), y2());
		corners[3] = originalPolynomial.evaluate(x1(), y2());
		edgeSolutions = cell.getEdgeSolutions();
		evals[0] = cell.polynomial.evaluate(x1(), y1());
		evals[1] = cell.polynomial.evaluate(x2(), y1());
		evals[2] = cell.polynomial.evaluate(x2(), y2());
		evals[3] = cell.polynomial.evaluate(x1(), y2());
	}

	public static BernsteinPlotRect as(PlotRect r) {
		return (BernsteinPlotRect) r;
	}

	@Override
	public double x1() {
		return box.x1();
	}

	@Override
	public double y1() {
		return box.y1();
	}

	@Override
	public double x2() {
		return box.x2();
	}

	@Override
	public double y2() {
		return box.y2();
	}

	@Override
	public double topLeft() {
		return corners[0];
	}

	@Override
	public double topRight() {
		return corners[1];
	}

	@Override
	public double bottomLeft() {
		return corners[3];
	}

	@Override
	public double bottomRight() {
		return corners[2];
	}

	@Override
	public double cornerAt(int i) {
		return corners[i];
	}

	public boolean haveSolutions(EdgeKind... kinds) {
		Set<EdgeKind> keys = edgeSolutions.keySet();
		if (keys.size() != kinds.length) {
			return false;
		}
		for (EdgeKind kind: kinds) {
			if (!keys.contains(kind)) {
				return false;
			}
		}
		return true;
	}

	public Map<EdgeKind, GPoint2D> getEdges() {
		return edgeSolutions;
	}

	public GPoint2D getSolution(EdgeKind kind) {
		return edgeSolutions.get(kind);
	}
	@Override
	public String toString() {
		return "BernsteinPlotRect{" +
				"box=" + box +
				", corners=" + Arrays.toString(corners) +
				", edgeSolutions=" + edgeSolutions.keySet() +
				'}';
	}

	public boolean hasNoSolution() {
		return edgeSolutions.isEmpty();
	}

	public String debugString() {
		return "corners=" + Arrays.toString(corners) + "\n evals: " + evals;
	}
}
