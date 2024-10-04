package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

public abstract class PlotRectConfigProvider {
	private MyPoint[] pts;

	public PlotRectConfig create(PlotRect r) {
		PlotRectConfig config = getConfigFromPlotRect(r);
		if (isConfigFinal(config)) {
			return config;
		}

		pts = config.getPoints(r);
		if (pts == null) {
			return empty();
		}

		return checkContinouty(config, r, pts);
	}

	protected abstract boolean isConfigFinal(PlotRectConfig gridType);

	protected abstract PlotRectConfig checkContinouty(PlotRectConfig config, PlotRect plotRect, MyPoint[] points);

	protected abstract PlotRectConfig getConfigFromPlotRect(PlotRect r);

	MyPoint[] getPoints() {
		return pts;
	}
	protected abstract PlotRectConfig empty();
	public abstract int listThreshold();
}
