package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.PlotRect;
import org.geogebra.common.kernel.implicit.PlotRectConfig;
import org.geogebra.common.kernel.implicit.PlotRectConfigProvider;

public class BernsteinPlotRectConfigProvider extends PlotRectConfigProvider {
	private final BernsteinPlotCell cell;

	public BernsteinPlotRectConfigProvider(BernsteinPlotCell cell) {
		this.cell = cell;
	}

	@Override
	public BernsteinRectConfig getConfigFromPlotRect(PlotRect r) {
		return BernsteinRectConfig.fromFlag(config(r));
	}

	@Override
	protected PlotRectConfig empty() {
		return BernsteinRectConfig.EMPTY;
	}

	public static int config(PlotRect r) {
		int config = 0;

		for (int i = 0; i < 4; i++) {
			config = (config << 1) | sign(r.cornerAt(i));
		}

		return config;
	}

	/**
	 *
	 * @param val
	 *            value to check
	 * @return the sign depending on the value. if value is infinity or NaN it
	 *         returns T_INV, otherwise it returns 1 for +ve value 0 otherwise
	 */
	private static int sign(double val) {
		if (Double.isInfinite(val) || Double.isNaN(val)) {
			return BernsteinRectConfig.T_INV.flag();
		} else if (val > 0.0) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int listThreshold() {
		return 1;
	}

	@Override
	public boolean canChangePointOrder() {
		return false;
	}

	@Override
	protected boolean isConfigFinal(PlotRectConfig config) {
		return config.isInvalid();
	}

	@Override
	protected PlotRectConfig checkContinouty(PlotRectConfig config, PlotRect plotRect, MyPoint[] points) {
		return BernsteinRectConfig.VALID;
	}
}
