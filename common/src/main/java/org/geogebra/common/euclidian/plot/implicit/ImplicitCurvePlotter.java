package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;

public class ImplicitCurvePlotter {
	public static final int MAX_SPLIT_RECURSION = 4;
	private final List<CurvePlotContext> subContexts = new ArrayList<>();
	private final EuclidianViewBounds bounds;
	private final CurvePlotContext intitialContext;

	public ImplicitCurvePlotter(GeoElement curve, EuclidianViewBounds bounds) {
		this.bounds = bounds;
		BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();
		BernsteinPolynomial polynomial = converter.from(curve, bounds.getXmin(), bounds.getXmax());
		intitialContext = new CurvePlotContext(new CurvePlotBoundingBox(
				bounds.getXmin(), bounds.getXmax(),
				bounds.getYmin(), bounds.getYmax()),
				polynomial);
		subContexts.add(intitialContext);
	}

	public void draw(GGraphics2D g2) {
		drawDebug(g2);
		drawResults(g2);

	}

	private void drawDebug(GGraphics2D g2) {
		drawDebug(intitialContext, g2);
		for (CurvePlotContext ctx : subContexts) {
			drawDebug(ctx, g2);
		}

	}

	private void drawDebug(CurvePlotContext ctx, GGraphics2D g2) {
		GColor color;
		switch (ctx.getContextCass()) {
		case CELL0:
			color = GColor.GREEN;
			break;
		case CELL1:
			color = GColor.BLUE;
			break;
		case CELL2:
			color = GColor.RED;
			break;
		case NONE:
			color = GColor.GRAY;
			break;
		default:
			color = GColor.BLACK;
		}

		int x = (int) (bounds.toScreenCoordXd(ctx.boundingBox.getXmin()));
		int y = (int) (bounds.toScreenCoordYd(ctx.boundingBox.getYmin()));
		int width = (int) (bounds.toScreenCoordXd(ctx.boundingBox.getXmax()) - x);
		int height = (int) (bounds.toScreenCoordYd(ctx.boundingBox.getYmax()) - y);
		g2.setColor(color.deriveWithAlpha(40));

		g2.fillRect(x, y, width, height);
		g2.setColor(GColor.BLACK.deriveWithAlpha(60));
		g2.drawRect(x, y, width, height);
	}

	private void drawResults(GGraphics2D g2) {
	}

	public void update() {
		for (int i = 0; i < MAX_SPLIT_RECURSION; i++) {
			split();
		}
		for (CurvePlotContext subContext : subContexts) {
			subContext.process();
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
