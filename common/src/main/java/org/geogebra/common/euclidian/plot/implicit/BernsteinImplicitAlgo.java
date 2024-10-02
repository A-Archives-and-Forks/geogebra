package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.euclidian.plot.implicit.BernsteinPlotter.SMALLEST_BOX_IN_PIXELS;
import static org.geogebra.common.euclidian.plot.implicit.BernsteinPlotter.SMALLEST_EDGE_IN_PIXELS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.LinkSegments;
import org.geogebra.common.util.debug.Log;

public class BernsteinImplicitAlgo implements PlotterAlgo {

	private final EuclidianViewBounds bounds;
	private final GeoElement curve;
	private final List<MyPoint> points;
	private final List<BernsteinPlotCell> cells;
	private final BernsteinPolynomialConverter converter;
	private final LinkSegments segments;
	private BernsteinPolynomial polynomial;

	/**
	 * @param grid {@link CellGrid}
	 * @param bounds {@link EuclidianViewBounds}
	 * @param curve the curve geo.
	 * @param cells
	 */
	public BernsteinImplicitAlgo(EuclidianViewBounds bounds, GeoElement curve, List<MyPoint> points,
			List<BernsteinPlotCell> cells) {
		this.bounds = bounds;
		this.curve = curve;
		this.points = points;
		this.cells = cells;
		converter = new BernsteinPolynomialConverter();
		segments = new LinkSegments(points);
	}

	@Override
	public void compute() {
		List<BernsteinPlotCell> cells = initialSplit(createRootCell());
		cells.forEach(this::findSolutions);
		Log.debug("done");
	}

	private List<BernsteinPlotCell> initialSplit(BernsteinPlotCell rootCell) {
		List<BernsteinPlotCell> list = new ArrayList<>();
		Collections.addAll(list, rootCell.split());
		return cellsWithPossibleSolution(list);
	}

	private BernsteinPlotCell createRootCell() {
		BoundsRectangle limits = new BoundsRectangle(bounds);
		polynomial = converter.from(curve, limits);
		BernsteinBoundingBox box = new BernsteinBoundingBox(limits);
		return new BernsteinPlotCell(box, polynomial);
	}

	private void findSolutions(BernsteinPlotCell cell) {
		findSolutionsInFaces(cell);
	}

	private static List<BernsteinPlotCell> cellsWithPossibleSolution(List<BernsteinPlotCell> list) {
		return list.stream().filter(BernsteinPlotCell::mightHaveSolution)
				.collect(Collectors.toList());
	}

	private void findSolutionsInFaces(BernsteinPlotCell cell) {
		// Stack to replace recursion
		Stack<BernsteinPlotCell> stack = new Stack<>();
		stack.push(cell);

		while (!stack.isEmpty()) {
			BernsteinPlotCell currentCell = stack.pop();

			if (!currentCell.mightHaveSolution()) {
				return;
			}

			if (isBoxSmallEnough(currentCell.boundingBox)) {
				addToOutput(currentCell);
			} else {
				for (BernsteinPlotCell c : currentCell.split()) {
					if (c.mightHaveSolution()) {
						stack.push(c);
					}
				}
			}
		}
	}

	private void addToOutput(BernsteinPlotCell currentCell) {
		cells.add(currentCell);
		BernsteinEdgeConfigProvider provider =
				new BernsteinEdgeConfigProvider(currentCell);
		BernsteinPlotRect rect =
				new BernsteinPlotRect(currentCell.boundingBox, polynomial);
		segments.add(rect, provider);
	}

	private boolean isBoxSmallEnough(BernsteinBoundingBox box) {
		double width = bounds.toScreenCoordXd(box.x2()) - bounds.toScreenCoordXd(box.x1());
		double height = bounds.toScreenCoordYd(box.y1()) - bounds.toScreenCoordYd(box.y2());
		return width < SMALLEST_BOX_IN_PIXELS
				|| height < SMALLEST_BOX_IN_PIXELS;
	}

	@SuppressWarnings("unused")
	private void findSolutionsInEdges(BernsteinPlotCell context) {
		context.createEdges();
		for (BernsteinPlotCellEdge edge : context.getEdges()) {
			if (edge.mightHaveSolutions()) {
				findSolutionsInOneEdge(edge);
			}
		}
	}

	private void findSolutionsInOneEdge(BernsteinPlotCellEdge startEdge) {
		Stack<BernsteinPlotCellEdge> stack = new Stack<>();
		stack.push(startEdge);

		while (!stack.isEmpty()) {
			BernsteinPlotCellEdge edge = stack.pop();
			if (edge.isDerivativeSignDiffer()) {
				BernsteinPlotCellEdge[] split = edge.split();
				stack.push(split[0]);
				stack.push(split[1]);
			} else {
				findSignChangeInEdge(edge);
			}
		}

	}

	private void findSignChangeInEdge(BernsteinPlotCellEdge startEdge) {
		Stack<BernsteinPlotCellEdge> stack = new Stack<>();
		stack.push(startEdge);
		while (!stack.isEmpty()) {
			BernsteinPlotCellEdge edge = stack.pop();
			if (edge.mightHaveSolutions() && isEdgeSmallEnough(edge)) {
				if (edge.hasIntersect()) {
					addIntersect(edge);
				}
				return;
			}

			BernsteinPlotCellEdge[] split = edge.split();
			if (split[0].mightHaveSolutions()) {
				stack.push(split[0]);
			}
			if (split[1].mightHaveSolutions()) {
				stack.push(split[1]);
			}
		}
	}

	@SuppressWarnings("unused")
	private void addIntersect(BernsteinPlotCellEdge edge) {
		// TODO: implement
	}

	private boolean isEdgeSmallEnough(BernsteinPlotCellEdge edge) {
		double x1 = edge.startPoint().x;
		double x2 = x1 + edge.length();
		double width = edge.isHorizontal()
				? bounds.toScreenCoordXd(x1) - bounds.toScreenCoordXd(x2)
				: bounds.toScreenCoordYd(x1) - bounds.toScreenCoordYd(x2);
		return width < SMALLEST_EDGE_IN_PIXELS;
	}
}
