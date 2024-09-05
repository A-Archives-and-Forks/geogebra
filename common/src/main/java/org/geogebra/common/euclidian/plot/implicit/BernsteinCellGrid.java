package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.euclidian.plot.implicit.BernsteinPlotter.SMALLEST_BOX_IN_PIXELS;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

public class BernsteinCellGrid implements CellGrid<BernsteinPlotCell> {
	private BernsteinPlotCell[][] cells;

	@Override
	public void resize(EuclidianViewBounds bounds) {
		int rows = (bounds.getHeight() / SMALLEST_BOX_IN_PIXELS) + 2;
		int columns = (bounds.getWidth() / SMALLEST_BOX_IN_PIXELS) + 2;
		cells = new BernsteinPlotCell[rows][columns];
	}

	/**
	 *
	 * @return All cells of the grid as a flattened list
	 */
	 @Override
	 public final List<BernsteinPlotCell> toList() {
		List<BernsteinPlotCell> list = new ArrayList<>();
		for (int row = 0; row < cells.length; row++) {
			BernsteinPlotCell[] arow = cells[row];
			if (arow == null) {
				continue;
			}
			for (int col = 0; col < arow.length ; col++) {
				BernsteinPlotCell cell = arow[col];
				if (cell != null) {
					list.add(cell);
				}
			}
		}
		return list;
	}

	/**
	 *
	 * @param cell the content
	 * @param row index to put
	 * @param column index to put
	 */
	@Override
	public void put(BernsteinPlotCell cell, int row, int column) {
		cells[row][column] = cell;
	}
}
