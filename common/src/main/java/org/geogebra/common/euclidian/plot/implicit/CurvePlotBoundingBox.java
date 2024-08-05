package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.arithmetic.Splittable;

public class CurvePlotBoundingBox implements Splittable<CurvePlotBoundingBox> {
	private final double xmin;
	private final double ymin;
	private final double xmax;
	private final double ymax;

	public CurvePlotBoundingBox(double xmin, double ymin, double xmax, double ymax) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	@Override
	public CurvePlotBoundingBox[] split() {
		return new CurvePlotBoundingBox[0];
	}
}
