package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimationListener;
import org.geogebra.common.euclidian.CoordSystemInfo;

public abstract class CoordSystemAnimatedPlotter implements CoordSystemAnimationListener {

	private boolean updateEnabled;

	@Override
	public void onZoomStop(CoordSystemInfo info) {
		enableUpdate();
	}

	@Override
	public void onMove(CoordSystemInfo info) {
		disableUpdate();
	}

	@Override
	public void onMoveStop() {
		enableUpdate();
	}

	public void updateOnDemand() {
		if (updateEnabled) {
			update();
		}
	}

	public abstract void update();

	private void enableUpdate() {
		updateEnabled = true;
	}

	private void disableUpdate() {
		updateEnabled = false;
	}

	public abstract void draw(GGraphics2D g2);
}
