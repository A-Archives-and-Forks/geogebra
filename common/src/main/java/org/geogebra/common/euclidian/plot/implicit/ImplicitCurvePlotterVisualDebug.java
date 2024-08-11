package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

final class ImplicitCurvePlotterVisualDebug {
	private final EuclidianViewBounds bounds;
	private final List<CurvePlotContext> subContexts;

	ImplicitCurvePlotterVisualDebug(EuclidianViewBounds bounds,
			List<CurvePlotContext> subContexts) {
		this.bounds = bounds;
		this.subContexts = subContexts;
	}


	void draw(GGraphics2D g2) {
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
		int y = (int) (bounds.toScreenCoordYd(ctx.boundingBox.getYmax()));
		int width = (int) (bounds.toScreenCoordXd(ctx.boundingBox.getXmax()) - x);
		int height = (int) (bounds.toScreenCoordYd(ctx.boundingBox.getYmin()) - y);
		g2.setColor(color.deriveWithAlpha(40));

		g2.fillRect(x, y, width, height);
		g2.setColor(GColor.BLACK.deriveWithAlpha(60));
		g2.drawRect(x, y, width, height);
	}

}
