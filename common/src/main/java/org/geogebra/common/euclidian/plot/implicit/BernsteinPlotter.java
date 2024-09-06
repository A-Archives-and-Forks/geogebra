package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoElement;

public class BernsteinPlotter extends CoordSystemAnimatedPlotter {
	public static final boolean VISUAL_DEBUG_ENABLED = true;
	public static final int SMALLEST_BOX_IN_PIXELS = 10;
	public static final double SMALLEST_EDGE_IN_PIXELS = 2;
	private final EuclidianViewBounds bounds;
	private final GeneralPathClippedForCurvePlotter gp;

	private final VisualDebug<BernsteinPlotCell> visualDebug;
	private final PlotterAlgo algo;
	private final CellGrid<BernsteinPlotCell> grid;

	/**
	 *
	 * @param curve to draw
	 * @param bounds {@link EuclidianViewBounds}
	 * @param gp {@link GeneralPathClippedForCurvePlotter}
	 */
	public BernsteinPlotter(GeoElement curve, EuclidianViewBounds bounds,
			GeneralPathClippedForCurvePlotter gp) {
		this.bounds = bounds;
		this.gp = gp;
		grid = new BernsteinCellGrid();
		algo = new BernsteinImplicitAlgo(grid, bounds, curve);

		if (VISUAL_DEBUG_ENABLED) {
			visualDebug = new BernsteinPlotterVisualDebug(bounds);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.draw(g2);
		}

		updateOnDemand();
		drawResults(g2);
	}

	@Override
	public void update() {
		algo.compute();

		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.setData(grid.toList());
		}
	}

	private void drawResults(GGraphics2D g2) {
		gp.reset();

		g2.setColor(GColor.BLUE);

		for (BernsteinPlotCell cell : grid.toList()) {
			drawPointToScreen(cell.center());
		}

		g2.draw(gp);
		gp.reset();

	}

	private void drawPointToScreen(GPoint2D p) {
		gp.moveTo((int) bounds.toScreenCoordXd(p.x), (int) bounds.toScreenCoordYd(p.y));
		gp.lineTo((int) bounds.toScreenCoordXd(p.x),
				(int) bounds.toScreenCoordYd(p.y));
	}

	public int plotCellCount() {
		return grid.toList().size();
	}
}
