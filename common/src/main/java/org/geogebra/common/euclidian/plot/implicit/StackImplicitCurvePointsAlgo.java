package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;
import java.util.Stack;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

public class StackImplicitCurvePointsAlgo implements ImplicitCurvePointsAlgo {

	private final ImplicitCurveData data;
	private final EuclidianViewBounds bounds;

	public StackImplicitCurvePointsAlgo(ImplicitCurveData data, EuclidianViewBounds bounds) {
		this.data = data;
		this.bounds = bounds;
	}

	@Override
	public void compute(CurvePlotContext context, List<CurvePlotContext> list) {
		findSolutionsInEdges(context.createEdges());
		findSolutionsInFaces(context, list);
	}

	private void findSolutionsInFaces(CurvePlotContext context, List<CurvePlotContext> list) {
		// Stack to replace recursion
		Stack<CurvePlotContext> stack = new Stack<>();
		stack.push(context);

		while (!stack.isEmpty()) {
			// Get the current context from the top of the stack
			CurvePlotContext currentContext = stack.pop();

			list.add(currentContext);
			// If the current context doesn't have a solution, skip to the next
			if (!currentContext.mightHaveSolution()) {
				continue;
			}

			CurvePlotBoundingBox box = currentContext.boundingBox;

			// If the bounding box is small enough, add the center point to the output7
			if (isBoxSmallEnough(box)) {
				data.addOutput(new GPoint2D(box.getX1() + box.getWidth() / 2,
						box.getY1() + box.getHeight() / 2));
			} else {
				// Otherwise, split the context and push each part onto the stack
				for (CurvePlotContext c : currentContext.split()) {
					stack.push(c);
				}
			}
		}
	}

	private boolean isBoxSmallEnough(CurvePlotBoundingBox box) {
		double width = bounds.toScreenCoordXd(box.getX2()) - bounds.toScreenCoordXd(box.getX1());
		double height = bounds.toScreenCoordYd(box.getY1()) - bounds.toScreenCoordYd(box.getY2());
		return width < ImplicitCurvePlotter.SMALLEST_BOX_IN_PIXELS
				|| height < ImplicitCurvePlotter.SMALLEST_BOX_IN_PIXELS;
	}

	private void findSolutionsInEdges(List<BoxEdge> edges) {
		for (BoxEdge edge : edges) {
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
			if (edge.mightHaveSolutions() && edge.isUnderSize(data.pixelInRW())) {
				data.addIntersect(edge);
				continue;
			}

			BoxEdge[] split = edge.split();
			if (split[0].mightHaveSolutions()) {
				data.addEdge(edge);
				stack.push(split[0]);
			}
			if (split[1].mightHaveSolutions()) {
				data.addEdge(edge);
				stack.push(split[1]);
			}
		}
	}
}
