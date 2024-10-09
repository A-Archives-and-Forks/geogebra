package org.geogebra.common.euclidian.plot.implicit;

public class BernsteinPlotterDebugSettings implements BernsteinPlotterSettings {

	public static final int MIN_CELL_SIZE = 10;

	@Override
	public boolean visualDebug() {
		return true;
	}

	@Override
	public int minBoxWidthInPixels() {
		return MIN_CELL_SIZE;
	}

	@Override
	public int minBoxHeightInPixels() {
		return MIN_CELL_SIZE;
	}

	@Override
	public int minEdgeWidth() {
		return 2;
	}
}