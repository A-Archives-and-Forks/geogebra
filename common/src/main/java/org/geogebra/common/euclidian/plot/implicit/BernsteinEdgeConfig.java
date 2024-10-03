package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.PlotRect;
import org.geogebra.common.kernel.implicit.PlotRectConfig;

public enum BernsteinEdgeConfig implements PlotRectConfig {
	;

	@Override
	public int flag() {
		return 0;
	}

	@Override
	public MyPoint[] getPoints(PlotRect r) {
		return new MyPoint[0];
	}

	@Override
	public double getQ1(PlotRect r) {
		return 0;
	}

	@Override
	public double getQ2(PlotRect r) {
		return 0;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public boolean isInvalid() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
}
