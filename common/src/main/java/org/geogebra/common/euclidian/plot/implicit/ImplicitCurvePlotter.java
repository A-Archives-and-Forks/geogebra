package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	public static final int MAX_SPLIT_RECURSION = 1;
	public static final boolean VISUAL_DEBUG_ENABLED = true;
	public static final int SMALLEST_BOX_IN_PIXELS = 5;
	private final List<CurvePlotContext> subContexts = new ArrayList<>();
	private final GeoElement curve;
	private final EuclidianViewBounds bounds;
	private final GeneralPathClippedForCurvePlotter gp;
	private final BernsteinPolynomialConverter converter;
	private final ImplicitCurvePlotterVisualDebug visualDebug;
	private final ImplicitCurvePointsAlgo algo;
	private final ImplicitCurveData data = new ImplicitCurveData();

	public ImplicitCurvePlotter(GeoElement curve, EuclidianViewBounds bounds, GeneralPathClippedForCurvePlotter gp) {
		this.curve = curve;
		this.bounds = bounds;
		this.gp = gp;
		algo = new StackImplicitCurvePointsAlgo(data, bounds);
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
		data.setPixelInRW(bounds.toRealWorldCoordX(1) - bounds.toRealWorldCoordX(0),
				bounds.toRealWorldCoordY(0) - bounds.toRealWorldCoordY(1));
	}

	public void draw(GGraphics2D g2) {
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.draw(g2, data.intersects());
//			visualDebug.drawEdges(g2, edges);
		}
		drawResults(g2);

	}

	private void drawResults(GGraphics2D g2) {
		gp.reset();

		g2.setColor(GColor.DARK_RED);

		for (GPoint2D p : data.output()) {
			gp.moveTo((int) bounds.toScreenCoordXd(p.x), (int) bounds.toScreenCoordYd(p.y));
			gp.lineTo((int) bounds.toScreenCoordXd(p.x),
					(int) bounds.toScreenCoordYd(p.y));
		}

		gp.endPlot();
		g2.draw(gp);

	}

	public void update() {
		split();
		data.clear();
		List<CurvePlotContext> list = new ArrayList<>();
		subContexts.forEach(c -> process(c, list));
		subContexts.clear();
		subContexts.addAll(list);

	}

	private void process(CurvePlotContext context, List<CurvePlotContext> list) {
		algo.compute(context, list);
	}


	private void split() {
		List<CurvePlotContext> list = new ArrayList<>();
		for (CurvePlotContext ctx: subContexts) {
			Collections.addAll(list, ctx.split());
		}
		subContexts.clear();
		subContexts.addAll(filterByCell(list));
	}

	private static List<CurvePlotContext> filterByCell(List<CurvePlotContext> list) {
		return list.stream().filter(CurvePlotContext::mightHaveSolution)
				.collect(Collectors.toList());
	}

	public int subContentCount() {
		return subContexts.size();
	}
}
