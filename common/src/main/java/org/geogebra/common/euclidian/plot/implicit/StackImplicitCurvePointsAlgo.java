package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.euclidian.plot.implicit.ImplicitCurvePlotter.SMALLEST_BOX_IN_PIXELS;

import java.util.List;
import java.util.Stack;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

public class StackImplicitCurvePointsAlgo implements ImplicitCurvePointsAlgo {

	private final BernsteinCellGrid grid;
	private final EuclidianViewBounds bounds;

	public StackImplicitCurvePointsAlgo(BernsteinCellGrid grid, EuclidianViewBounds bounds) {
		this.grid = grid;
		this.bounds = bounds;
	}

	@Override
 	public void compute(BernsteinPlotCell cell, List<BernsteinPlotCell> list) {
		findSolutionsInFaces(cell, list);
	}

	private void findSolutionsInFaces(BernsteinPlotCell cell, List<BernsteinPlotCell> list) {
		// Stack to replace recursion
		Stack<BernsteinPlotCell> stack = new Stack<>();
		stack.push(cell);

		while (!stack.isEmpty()) {
			BernsteinPlotCell currentCell = stack.pop();

			list.add(currentCell);
			if (!currentCell.mightHaveSolution()) {
				return;
			}

			if (isBoxSmallEnough(currentCell.boundingBox)) {
				addCell(currentCell);
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
		return width < ImplicitCurvePlotter.SMALLEST_BOX_IN_PIXELS
				|| height < ImplicitCurvePlotter.SMALLEST_BOX_IN_PIXELS;
	}

	private void findSolutionsInEdges(BernsteinPlotCell context) {
		context.createEdges();
		for (BoxEdge edge : context.getEdges()) {
			if (edge.mightHaveSolutions()) {
				findSolutionsInOneEdge(edge);
			}
		}
	}

	private void findSolutionsInOneEdge(BoxEdge startEdge) {
		Stack<BoxEdge> stack = new Stack<>();
		stack.push(startEdge);

		while (!stack.isEmpty()) {
			BoxEdge edge = stack.pop();
			if (edge.isDerivativeSignDiffer()) {
				BoxEdge[] split = edge.split();
				stack.push(split[0]);
				stack.push(split[1]);
			} else {
				findSignChangeInEdge(edge);
			}
		}

	}

	private void findSignChangeInEdge(BoxEdge startEdge) {
		Stack<BoxEdge> stack = new Stack<>();
		stack.push(startEdge);
		while (!stack.isEmpty()) {
			BoxEdge edge = stack.pop();
			if (edge.mightHaveSolutions() && isEdgeSmallEnough(edge)) {
				if (edge.hasIntersect()) {
//					data.addIntersect(edge);
				}
				return;
			}

			BoxEdge[] split = edge.split();
			if (split[0].mightHaveSolutions()) {
				stack.push(split[0]);
			}
			if (split[1].mightHaveSolutions()) {
				stack.push(split[1]);
			}
		}
	}

	private void addCell(BernsteinPlotCell cell) {
		int column = (int) Math.round(bounds.toScreenCoordXd(cell.boundingBox.getX1())
				/ SMALLEST_BOX_IN_PIXELS);
		int row =  (int) Math.round(bounds.toScreenCoordYd(cell.boundingBox.getY1())
				/ SMALLEST_BOX_IN_PIXELS);
		grid.put(cell, row, column);

	}

	private boolean isEdgeSmallEnough(BoxEdge edge) {
		double x1 = edge.startPoint().x;
		double x2 = x1 + edge.length();
		double width = edge.isHorizontal()
				? bounds.toScreenCoordXd(x1) - bounds.toScreenCoordXd(x2)
				: bounds.toScreenCoordYd(x1) - bounds.toScreenCoordYd(x2);
		return width < ImplicitCurvePlotter.SMALLEST_EDGE_IN_PIXELS;
	}

}
