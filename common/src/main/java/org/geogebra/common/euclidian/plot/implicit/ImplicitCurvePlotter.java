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
	public static final int SMALLEST_BOX_IN_PIXELS = 10;
	public static final double SMALLEST_EDGE_IN_PIXELS = 2;
	private final List<BernsteinPlotCell> subContexts = new ArrayList<>();
	private final GeoElement curve;
	private final EuclidianViewBounds bounds;
	private final GeneralPathClippedForCurvePlotter gp;
	private final BernsteinPolynomialConverter converter;
	private final ImplicitCurvePlotterVisualDebug visualDebug;
	private final ImplicitCurvePointsAlgo algo;
	private final BernsteinCellGrid grid;

	public ImplicitCurvePlotter(GeoElement curve, EuclidianViewBounds bounds, GeneralPathClippedForCurvePlotter gp) {
		this.curve = curve;
		this.bounds = bounds;
		this.gp = gp;
		grid = new BernsteinCellGrid();
		algo = new StackImplicitCurvePointsAlgo(grid, bounds);
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
		BernsteinBoundingBox box = new BernsteinBoundingBox(limits);
		BernsteinPlotCell rootContext = new BernsteinPlotCell(box, polynomial);
		subContexts.clear();
		subContexts.add(rootContext);
	}

	public void draw(GGraphics2D g2) {
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.draw(g2);
//			visualDebug.drawEdges(g2, edges);
		}
		drawResults(g2);

	}

	private void drawResults(GGraphics2D g2) {
		gp.reset();

		g2.setColor(GColor.BLUE);

		for (GPoint2D p : grid.toPointList()) {
			drawPointToScreen(p);
		}

		g2.draw(gp);
		gp.reset();

	}

	private void drawPointToScreen(GPoint2D p) {
		gp.moveTo((int) bounds.toScreenCoordXd(p.x), (int) bounds.toScreenCoordYd(p.y));
		gp.lineTo((int) bounds.toScreenCoordXd(p.x),
				(int) bounds.toScreenCoordYd(p.y));
	}

	public void update() {
		split();
		List<BernsteinPlotCell> list = new ArrayList<>();
		grid.resize(bounds);
		subContexts.forEach(c -> algo.compute(c, list));
		subContexts.clear();
		subContexts.addAll(filterByCell(list));
//		subContexts.addAll(list);
	}


	private void split() {
		List<BernsteinPlotCell> list = new ArrayList<>();
		for (BernsteinPlotCell ctx: subContexts) {
			Collections.addAll(list, ctx.split());
		}
		subContexts.clear();
		subContexts.addAll(filterByCell(list));
	}

	private static List<BernsteinPlotCell> filterByCell(List<BernsteinPlotCell> list) {
		return list.stream().filter(BernsteinPlotCell::mightHaveSolution)
				.collect(Collectors.toList());
	}

	public int subContentCount() {
		return subContexts.size();
	}
}
