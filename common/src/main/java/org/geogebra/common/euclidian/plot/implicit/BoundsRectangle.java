package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

public class BoundsRectangle {

	private final double xmin;
	private final double xmax;
	private final double ymin;
	private final double ymax;

	public BoundsRectangle(double xmin, double xmax, double ymin, double ymax) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	public BoundsRectangle(EuclidianViewBounds bounds) {
		this(bounds.getXmin(), bounds.getYmin(), bounds.getXmax(), bounds.getYmax());
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
