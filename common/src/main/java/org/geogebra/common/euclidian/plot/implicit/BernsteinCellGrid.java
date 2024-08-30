package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.euclidian.plot.implicit.ImplicitCurvePlotter.SMALLEST_BOX_IN_PIXELS;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

public class BernsteinCellGrid {
	private BernsteinPlotCell[][] cells;

	public void resize(EuclidianViewBounds bounds) {
		int rows = (bounds.getHeight() / SMALLEST_BOX_IN_PIXELS) + 2;
		int columns = (bounds.getWidth() / SMALLEST_BOX_IN_PIXELS) + 2;
		cells = new BernsteinPlotCell[rows][columns];
	}


	public final List<GPoint2D> toPointList() {
		List<GPoint2D> list = new ArrayList<>();
		for (int row = 0; row < cells.length; row++) {
			BernsteinPlotCell[] arow = cells[row];
			if (arow == null) {
				continue;
			}
			for (int col = 0; col < arow.length ; col++) {
				BernsteinPlotCell cell = arow[col];
				if (cell != null) {
					GPoint2D center = cell.center();
					list.add(center);
				}
			}
		}
		return list;
	}

	public void put(BernsteinPlotCell cell, int row, int column) {
		cells[row][column] = cell;
	}
}
