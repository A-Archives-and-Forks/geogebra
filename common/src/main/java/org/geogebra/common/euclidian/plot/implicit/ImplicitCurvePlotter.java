package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;

public class ImplicitCurvePlotter {
	public static final int MAX_SPLIT_RECURSION = 3;
	public static final boolean VISUAL_DEBUG_ENABLED = true;
	public static final int SMALLEST_BOX_IN_PIXELS = 35;
	private final List<CurvePlotContext> subContexts = new ArrayList<>();
	private final GeoElement curve;
	private final EuclidianViewBounds bounds;
	private final GeneralPathClippedForCurvePlotter gp;
	private final BernsteinPolynomialConverter converter;
	private GPoint2D pixelInRW;
	private final ImplicitCurvePlotterVisualDebug visualDebug;
	private final List<GPoint2D> output = new ArrayList<>();
	private final List<BoxEdge> edges = new ArrayList<>();
	private final List<BoxEdge> intersects = new ArrayList<>();

	public ImplicitCurvePlotter(GeoElement curve, EuclidianViewBounds bounds, GeneralPathClippedForCurvePlotter gp) {
		this.curve = curve;
		this.bounds = bounds;
		this.gp = gp;
		converter = new BernsteinPolynomialConverter();
		initContext();
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug = new ImplicitCurvePlotterVisualDebug(bounds, subContexts);
		}
	}

	public void initContext() {
		Log.debug("initContext()");
		BoundsRectangle limits = new BoundsRectangle(bounds);
		BernsteinPolynomial polynomial = converter.from(curve, limits);
		CurvePlotBoundingBox box = new CurvePlotBoundingBox(limits);
		CurvePlotContext rootContext = new CurvePlotContext(box, polynomial);
		subContexts.clear();
		subContexts.add(rootContext);
		pixelInRW = new GPoint2D(bounds.toRealWorldCoordX(1) - bounds.toRealWorldCoordX(0),
				bounds.toRealWorldCoordY(0) - bounds.toRealWorldCoordY(1));
	}

	public void draw(GGraphics2D g2) {
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.draw(g2, intersects);
//			visualDebug.drawEdges(g2, edges);
		}
		drawResults(g2);

	}

	private void drawResults(GGraphics2D g2) {
		gp.reset();

		g2.setColor(GColor.DARK_RED);
		for (GPoint2D p : output) {
			gp.moveTo((int) bounds.toScreenCoordXd(p.x), (int) bounds.toScreenCoordYd(p.y));
			gp.lineTo((int) bounds.toScreenCoordXd(p.x),
					(int) bounds.toScreenCoordYd(p.y));
		}

		gp.endPlot();
		g2.draw(gp);

	}

	public void update() {
		for (int i = 0; i < MAX_SPLIT_RECURSION; i++) {
			split();
		}
		output.clear();
		edges.clear();
		intersects.clear();
		List<CurvePlotContext> list = new ArrayList<>();
		subContexts.forEach(c -> process(c, list));
		subContexts.clear();
		subContexts.addAll(list);

	}

	private void process(CurvePlotContext context, List<CurvePlotContext> list) {
		findSolutionsInEdges(context.createEdges());
		findSolutionsInFaces(context, list);
	}

	//	private void findSolutionsInFaces(CurvePlotContext context, List<CurvePlotContext> list) {
//		list.add(context);
//		if (!context.mightHaveSolution()) {
//			return;
//		}
//
//		CurvePlotBoundingBox box = context.boundingBox;
//		if (isBoxSmallEnough(box)) {
//			output.add(new GPoint2D(box.getX1() + box.getWidth() / 2,
//					box.getY1() + box.getHeight() / 2));
//			return;
//		}
//
//		for (CurvePlotContext c : context.split()) {
//			findSolutionsInFaces(c, list);
//		}
//	}
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

			// If the bounding box is small enough, add the center point to the output
			if (isBoxSmallEnough(box)) {
				output.add(new GPoint2D(box.getX1() + box.getWidth() / 2,
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
		return width < SMALLEST_BOX_IN_PIXELS || height < SMALLEST_BOX_IN_PIXELS;
	}

	private void findSolutionsInEdges(List<BoxEdge> edges) {
		for (BoxEdge edge: edges) {
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
		while (!stack.isEmpty())   {
			BoxEdge edge = stack.pop();
			if (edge.mightHaveSolutions() && edge.isUnderSize(pixelInRW)) {
				intersects.add(edge);
				continue;
			}

			BoxEdge[] split = edge.split();
			if (split[0].mightHaveSolutions()) {
				this.edges.add(edge);
				stack.push(split[0]);
			}
			if (split[1].mightHaveSolutions()){
				this.edges.add(edge);
				stack.push(split[1]);
			}
		}
	}


	private void split() {
		List<CurvePlotContext> list = new ArrayList<>();
		for (CurvePlotContext ctx: subContexts) {
			Collections.addAll(list, ctx.split());
		}
		subContexts.clear();
		subContexts.addAll(list);
//		subContexts.addAll(filterByCell(list));
	}

	private static List<CurvePlotContext> filterByCell(List<CurvePlotContext> list) {
		return list.stream().filter(CurvePlotContext::mightHaveSolution)
				.collect(Collectors.toList());
	}

	public int subContentCount() {
		return subContexts.size();
	}
}
