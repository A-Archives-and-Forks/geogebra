package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimationListener;
import org.geogebra.common.euclidian.CoordSystemInfo;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.implicit.ImplicitCurvePlotter;
import org.geogebra.common.kernel.geos.GeoElement;

public class ImplicitCurveController implements CoordSystemAnimationListener {

	private ImplicitCurvePlotter plotter;
	private final EuclidianController ec;
	private final GeoElement geo;
	private final EuclidianView view;

	public ImplicitCurveController(EuclidianView view, GeoElement geo) {
		this.view = view;
		this.geo = geo;
		ec = view.getEuclidianController();
		ec.addZoomerAnimationListener(this, geo);
		plotter = new ImplicitCurvePlotter(geo, new EuclidianViewBoundsImp(view),
				new GeneralPathClippedForCurvePlotter(view));
		updatePlotter();
	}

	private void updatePlotter() {
		plotter.initContext();
		plotter.update();
	}

	public void drawPlotter(GGraphics2D g2) {
		plotter.draw(g2);
	}

	@Override
	public void onZoomStop(CoordSystemInfo info) {
		updatePlotter();
	}

	@Override
	public void onMove(CoordSystemInfo info) {

	}

	@Override
	public void onMoveStop() {
		updatePlotter();
	}
}
