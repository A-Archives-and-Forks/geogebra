package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;

public class ImplicitCurvePlotter {
	public static final int MAX_SPLIT_RECURSION = 4;
	public static final boolean VISUAL_DEBUG_ENABLED = true;
	private final List<CurvePlotContext> subContexts = new ArrayList<>();
	private final GeoElement curve;
	private final EuclidianViewBounds bounds;
	private final BernsteinPolynomialConverter converter;
	private GPoint2D pixelInRW;
	private ImplicitCurvePlotterVisualDebug visualDebug;
	private final List<GPoint2D> output = new ArrayList<>();
	private final List<BoxEdge> edges = new ArrayList<>();

	public ImplicitCurvePlotter(GeoElement curve, EuclidianViewBounds bounds) {
		this.curve = curve;
		this.bounds = bounds;
		converter = new BernsteinPolynomialConverter();
		initContext();
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug = new ImplicitCurvePlotterVisualDebug(bounds, subContexts);
		}
	}

	public void initContext() {
		BoundsRectangle limits = new BoundsRectangle(bounds);
		BernsteinPolynomial polynomial = converter.from(curve, limits);
		CurvePlotBoundingBox box = new CurvePlotBoundingBox(limits);
		CurvePlotContext rootContext = new CurvePlotContext(box, polynomial);
		subContexts.clear();
		subContexts.add(rootContext);
		pixelInRW = new GPoint2D(bounds.toRealWorldCoordX(1) - bounds.toRealWorldCoordX(0),
				bounds.toRealWorldCoordY(0) - bounds.toRealWorldCoordY(1));
		Log.debug("PixelInRW: " + pixelInRW);
	}

	public void draw(GGraphics2D g2) {
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.draw(g2, output);
			visualDebug.drawEdges(g2, edges);
		}
		drawResults(g2);

	}

	private void drawResults(GGraphics2D g2) {

	}

	public void update() {
		for (int i = 0; i < MAX_SPLIT_RECURSION; i++) {
			split();
		}
		output.clear();
		edges.clear();
		subContexts.forEach(this::process);

	}

	private void process(CurvePlotContext context) {
		findSolutionsInEdges(context.createEdges());
	}

	private void findSolutionsInEdges(List<BoxEdge> edges) {
		for (BoxEdge edge: edges) {
			if (edge.mightHaveSolutions()) {
				findSolutionsInOneEdge(edge);
				edges.add(edge);
			}
		}
	}

	private void findSolutionsInOneEdge(BoxEdge edge) {
		if (edge.isDerivativeSignDiffer()) {
			BoxEdge[] split = edge.split();
			findSolutionsInOneEdge(split[0]);
			findSolutionsInOneEdge(split[1]);
		} else {
			findSignChangeInEdge(edge);
		}

	}

	private void findSignChangeInEdge(BoxEdge edge) {
		if (edge.mightHaveSolutions() && edge.isUnderSize(pixelInRW)) {
			output.add(edge.startPoint());
			return;
		}

		BoxEdge[] split = edge.split();
		if (split[0].mightHaveSolutions()) {
			findSignChangeInEdge(split[0]);
		} else if (split[1].mightHaveSolutions()){
			findSignChangeInEdge(split[1]);
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
