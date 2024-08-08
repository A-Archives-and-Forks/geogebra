package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;

public class ImplicitCurvePlotter {
	public static final int MAX_SPLIT_RECURSION = 3;
	private final List<CurvePlotContext> subContexts = new ArrayList<>();

	public ImplicitCurvePlotter(GeoElement curve, EuclidianViewBounds bounds) {
		BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();
		BernsteinPolynomial polynomial = converter.from(curve, bounds.getXmin(), bounds.getXmax());
		CurvePlotContext intitialContext = new CurvePlotContext(new CurvePlotBoundingBox(
				bounds.getXmin(), bounds.getYmin(),
				bounds.getXmax(), bounds.getYmax()),
				polynomial);
		subContexts.add(intitialContext);
	}

	public void draw(GGraphics2D g2) {
		update();
		drawResults(g2);
	}

	private void drawResults(GGraphics2D g2) {
	}

	public void update() {
		for (int i = 0; i < MAX_SPLIT_RECURSION; i++) {
			split();
		}
	}

	private void split() {
		List<CurvePlotContext> list = new ArrayList<>();
		for (CurvePlotContext ctx: subContexts) {
			Collections.addAll(list, ctx.split());
		}
		subContexts.clear();
		subContexts.addAll(list);
	}

	public int subContentCount() {
		return subContexts.size();
	}
}
