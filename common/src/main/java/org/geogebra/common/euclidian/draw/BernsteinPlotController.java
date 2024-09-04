package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimationListener;
import org.geogebra.common.euclidian.CoordSystemInfo;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.implicit.BernsteinPlotter;
import org.geogebra.common.kernel.geos.GeoElement;

public class BernsteinPlotController implements CoordSystemAnimationListener {

	private final BernsteinPlotter plotter;
	private boolean updateEnabled=true;

	public BernsteinPlotController(EuclidianView view, GeoElement geo) {
		EuclidianController ec = view.getEuclidianController();
		ec.addZoomerAnimationListener(this, geo);
		plotter = new BernsteinPlotter(geo, new EuclidianViewBoundsImp(view),
				new GeneralPathClippedForCurvePlotter(view));
	}

	private void updatePlotter() {
		plotter.updateRootCell();
		plotter.update();
	}

	public void drawPlotter(GGraphics2D g2) {
		if (updateEnabled) {
			updatePlotter();
		}
		plotter.draw(g2);
	}

	@Override
	public void onZoomStop(CoordSystemInfo info) {
		updateEnabled = true;
		logUpdateEnabled();
	}

	@Override
	public void onMove(CoordSystemInfo info) {
		updateEnabled = false;
		logUpdateEnabled();
	}

	private void logUpdateEnabled() {
//		Log.debug("Update " + (updateEnabled ? "enabled" : "disabled"));
	}

	@Override
	public void onMoveStop() {
		updateEnabled = true;
		logUpdateEnabled();
	}
}
