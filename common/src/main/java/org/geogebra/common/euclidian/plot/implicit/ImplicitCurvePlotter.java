package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;

public class ImplicitCurvePlotter {
	public static final int MAX_SPLIT_RECURSION = 4;
	public static final boolean VISUAL_DEBUG_ENABLED = true;
	private final List<CurvePlotContext> subContexts = new ArrayList<>();
	private final GeoElement curve;
	private final EuclidianViewBounds bounds;
	private final BernsteinPolynomialConverter converter;
	private final ImplicitCurvePlotterVisualDebug visualDebug;

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
	}

	public void draw(GGraphics2D g2) {
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.draw(g2);
		}
		drawResults(g2);

	}

	private void drawResults(GGraphics2D g2) {
	}

	public void update() {
		for (int i = 0; i < MAX_SPLIT_RECURSION; i++) {
			split();
		}
//		subContexts.forEach(CurvePlotContext::process);

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
