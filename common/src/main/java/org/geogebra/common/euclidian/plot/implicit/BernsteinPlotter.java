package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.CurvePlotterUtils;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.CoordSys;

public class BernsteinPlotter extends CoordSystemAnimatedPlotter {
	public static final boolean VISUAL_DEBUG_ENABLED = true;
	public static final int SMALLEST_BOX_IN_PIXELS = 10;
	public static final double SMALLEST_EDGE_IN_PIXELS = 2;
	private final GeoElement geo;
	private final EuclidianViewBounds bounds;
	private final GeneralPathClippedForCurvePlotter gp;
	private final CoordSys transformedCoordSys;

	private final VisualDebug<BernsteinPlotCell> visualDebug;
	private final PlotterAlgo algo;
	private final List<MyPoint> points = new ArrayList<>();
	private final List<BernsteinPlotCell> cells = new ArrayList<>();

	/**
	 * @param geo to draw
	 * @param bounds {@link EuclidianViewBounds}
	 * @param gp {@link GeneralPathClippedForCurvePlotter}
	 * @param transformedCoordSys
	 */
	public BernsteinPlotter(GeoElement geo, EuclidianViewBounds bounds,
			GeneralPathClippedForCurvePlotter gp, CoordSys transformedCoordSys) {
		this.geo = geo;
		this.bounds = bounds;
		this.gp = gp;
		this.transformedCoordSys = transformedCoordSys;
		algo = new BernsteinImplicitAlgo(bounds, geo, points, cells);
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug = new BernsteinPlotterVisualDebug(bounds);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (VISUAL_DEBUG_ENABLED) {
//			visualDebug.draw(g2);
		}

		updateOnDemand();
		drawResults(g2);
	}

	@Override
	public void update() {
		cells.clear();
		points.clear();
		algo.compute();

		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.setData(cells);
		}
	}

	private void drawResults(GGraphics2D g2) {
//		gp.resetWithThickness(geo.getLineThickness());
//		g2.setColor(geo.getObjectColor());
		CurvePlotterUtils.draw(gp, points, transformedCoordSys);
//		g2.draw(gp);
	}

	private void drawPointToScreen(GPoint2D p) {
		gp.moveTo((int) bounds.toScreenCoordXd(p.x), (int) bounds.toScreenCoordYd(p.y));
		gp.lineTo((int) bounds.toScreenCoordXd(p.x),
				(int) bounds.toScreenCoordYd(p.y));
	}

	public int plotCellCount() {
		return points.size();
	}
}
