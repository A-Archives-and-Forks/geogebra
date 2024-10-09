package org.geogebra.common.euclidian.plot.implicit;

public class BernsteinPlotterDefaultSettings implements BernsteinPlotterSettings {
	@Override
	public boolean visualDebug() {
		return false;
	}

	@Override
	public int minBoxWidthInPixels() {
		return 10;
	}

	@Override
	public int minBoxHeightInPixels() {
		return 10;
	}

	@Override
	public int minEdgeWidth() {
		return 2;
	}
}
