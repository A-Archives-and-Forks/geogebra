package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.EdgeConfig;
import org.geogebra.common.kernel.implicit.EdgeConfigProvider;
import org.geogebra.common.kernel.implicit.PlotRect;

public class BernsteinEdgeConfigProvider implements EdgeConfigProvider {
	private final BernsteinPlotCell cell;
	private MyPoint[] pts;

	public BernsteinEdgeConfigProvider(BernsteinPlotCell cell) {
		this.cell = cell;
	}

	@Override
	public EdgeConfig create(PlotRect r) {
		EdgeConfig gridType = EdgeConfig.fromFlag(config(r));
		if (gridType == EdgeConfig.T0101 || gridType == EdgeConfig.T_INV) {
			return gridType;
		}

		double q1 = 0.0, q2 = 0.0;

		pts = gridType.getPoints(r);
		if (pts == null) {
			return EdgeConfig.EMPTY;
		}

		q1 = gridType.getQ1(r);
		q2 = gridType.getQ2(r);

		// check continuity of the function between P1 and P2
		double p = Math.abs(cell.polynomial.evaluate(pts[0].x, pts[0].y));
		double q = Math.abs(cell.polynomial.evaluate(pts[1].x, pts[1].y));
		if (p <= q1 && q <= q2) {
			return EdgeConfig.VALID;
		}
		return EdgeConfig.EMPTY;
	}

	@Override
	public MyPoint[] getPoints() {
		return pts;
	}

	private int config(PlotRect r) {
		int config = 0;
		for (int i = 0; i < 4; i++) {
			config = (config << 1) | sign(r.cornerAt(i));
		}
		return config >= 8 ? (~config) & 0xf : config;
	}


	/**
	 *
	 * @param val
	 *            value to check
	 * @return the sign depending on the value. if value is infinity or NaN it
	 *         returns T_INV, otherwise it returns 1 for +ve value 0 otherwise
	 */
	private int sign(double val) {
		if (Double.isInfinite(val) || Double.isNaN(val)) {
			return EdgeConfig.T_INV.flag();
		} else if (val > 0.0) {
			return 1;
		} else {
			return 0;
		}
	}

}
