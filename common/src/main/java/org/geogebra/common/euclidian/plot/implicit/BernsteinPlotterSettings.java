package org.geogebra.common.euclidian.plot.implicit;

public interface BernsteinPlotterSettings {
	boolean visualDebug();
	int minBoxWidthInPixels();
	int minBoxHeightInPixels();
	int minEdgeWidth();

	boolean isUpdateEnabled();
}
