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

		double q1 = config.getQ1(r);
		double q2 = config.getQ2(r);

		return checkContinouty(q1, q2) ? valid() : empty();
//		return valid();
	}

	protected abstract boolean isConfigFinal(PlotRectConfig gridType);

	protected boolean checkContinouty(double q1, double q2) {
		return limitOf(pts[0]) <= q1 && limitOf(pts[1]) <= q2;
	}

	protected abstract double limitOf(MyPoint point);

	protected abstract PlotRectConfig getConfigFromPlotRect(PlotRect r);

	MyPoint[] getPoints() {
		return pts;
	}
	
	protected abstract PlotRectConfig valid();
	protected abstract PlotRectConfig empty();

	public abstract int listThreshold();
}
