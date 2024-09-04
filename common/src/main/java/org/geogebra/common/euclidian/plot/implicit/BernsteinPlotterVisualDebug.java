package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

final class BernsteinPlotterVisualDebug {
	private final EuclidianViewBounds bounds;
	private List<BernsteinPlotCell> cells;

	BernsteinPlotterVisualDebug(EuclidianViewBounds bounds) {
		this.bounds = bounds;
	}


	void draw(GGraphics2D g2) {
		if (cells == null) {
			return;
		}

		for (BernsteinPlotCell cell : cells) {
			drawCell(g2, cell);
		}

	}

	private void drawCell(GGraphics2D g2, BernsteinPlotCell cell) {
		GColor color;
		switch (cell.getKind()) {
		case CELL0:
			color = GColor.GREEN;
			break;
		case CELL1:
			color = GColor.BLUE;
			break;
		case CELL2:
			color = GColor.GRAY;
			break;
		default:
			color = GColor.BLACK;
		}

		int x = (int) (bounds.toScreenCoordXd(cell.boundingBox.getX1()));
		int y = (int) (bounds.toScreenCoordYd(cell.boundingBox.getY1()));
		int width = (int) (bounds.toScreenCoordXd(cell.boundingBox.getX2()) - x);
		int height = (int) (bounds.toScreenCoordYd(cell.boundingBox.getY2()) - y);
		g2.setColor(color);

		g2.setColor(GColor.BLACK.deriveWithAlpha(25));
		g2.drawRect(x, y, width, height);
	}

	public void drawEdges(GGraphics2D g2, List<BoxEdge> edges) {
		for (BoxEdge edge : edges) {
			edge.draw(g2, bounds);
		}

	}

	public void setCells(List<BernsteinPlotCell> cells) {
		this.cells = cells;

	}
}
