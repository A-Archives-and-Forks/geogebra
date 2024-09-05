package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.euclidian.plot.implicit.BernsteinPlotter.SMALLEST_BOX_IN_PIXELS;
import static org.geogebra.common.euclidian.plot.implicit.BernsteinPlotter.SMALLEST_EDGE_IN_PIXELS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;

public class BernsteinImplicitAlgo implements PlotterAlgo {

	private final CellGrid<BernsteinPlotCell>  grid;
	private final EuclidianViewBounds bounds;
	private final GeoElement curve;
	private final BernsteinPolynomialConverter converter;

	public BernsteinImplicitAlgo(CellGrid<BernsteinPlotCell> grid, EuclidianViewBounds bounds,
			GeoElement curve) {
		this.grid = grid;
		this.bounds = bounds;
		this.curve = curve;
		converter = new BernsteinPolynomialConverter();
	}

	@Override
	public void compute() {
		grid.resize(bounds);
		List<BernsteinPlotCell> cells = initialSplit(createRootCell());
		cells.forEach(this::findSolutions);
	}

	private List<BernsteinPlotCell> initialSplit(BernsteinPlotCell rootCell) {
		List<BernsteinPlotCell> list = new ArrayList<>();
		Collections.addAll(list, rootCell.split());
		return cellsWithPossibleSolution(list);
	}

	private BernsteinPlotCell createRootCell() {
		BoundsRectangle limits = new BoundsRectangle(bounds);
		BernsteinPolynomial polynomial = converter.from(curve, limits);
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
				putToGrid(currentCell);
			} else {
				for (BernsteinPlotCell c : currentCell.split()) {
					if (c.mightHaveSolution()) {
						stack.push(c);
					}
				}
			}
		}
	}

	private boolean isBoxSmallEnough(BernsteinBoundingBox box) {
		double width = bounds.toScreenCoordXd(box.getX2()) - bounds.toScreenCoordXd(box.getX1());
		double height = bounds.toScreenCoordYd(box.getY1()) - bounds.toScreenCoordYd(box.getY2());
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

	private void putToGrid(BernsteinPlotCell cell) {
		int column = (int) Math.round(bounds.toScreenCoordXd(cell.boundingBox.getX1())
				/ SMALLEST_BOX_IN_PIXELS);
		int row =  (int) Math.round(bounds.toScreenCoordYd(cell.boundingBox.getY1())
				/ SMALLEST_BOX_IN_PIXELS);
		grid.put(cell, row, column);

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
