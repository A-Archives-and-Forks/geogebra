package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

public class BoundsRectangle {

	private final double xmin;
	private final double xmax;
	private final double ymin;
	private final double ymax;

	public BoundsRectangle(EuclidianViewBounds bounds) {
		xmin = bounds.getXmin();
		xmax = bounds.getXmax();
		ymin = bounds.getYmin();
		ymax = bounds.getYmax();
	}

	public double getXmin() {
		return xmin;
	}

	public double getXmax() {
		return xmax;
	}

	public double getYmin() {
		return ymin;
	}

	public double getYmax() {
		return ymax;
	}

	@Override
	public String toString() {
		return "BoundsRectangle{" +
				"xmin=" + xmin +
				", xmax=" + xmax +
				", ymin=" + ymin +
				", ymax=" + ymax +
				'}';
	}
}
