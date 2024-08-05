package org.geogebra.common.euclidian.plot.implicit;

import java.util.Objects;

import org.geogebra.common.kernel.arithmetic.Splittable;

public class CurvePlotBoundingBox implements Splittable<CurvePlotBoundingBox> {
	private final double xmin;
	private final double ymin;
	private final double xmax;
	private final double ymax;
	private final double xHalf;
	private final double yHalf;

	public CurvePlotBoundingBox(double xmin, double xmax, double ymin, double ymax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		xHalf = xmin + (xmax - xmin) / 2;
		yHalf = ymin + (ymax - ymin) / 2;
	}

	@Override
	public CurvePlotBoundingBox[] split() {
		CurvePlotBoundingBox[] boxes = new CurvePlotBoundingBox[4];
		boxes[0] = new CurvePlotBoundingBox(xmin, xHalf, ymin, yHalf);
		boxes[1] = new CurvePlotBoundingBox(xHalf, xmax, ymin, yHalf);
		boxes[2] = new CurvePlotBoundingBox(xmin, xHalf, yHalf, ymax);
		boxes[3] = new CurvePlotBoundingBox(xHalf, xmax, yHalf, ymax);
		return boxes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CurvePlotBoundingBox)) return false;
		CurvePlotBoundingBox that = (CurvePlotBoundingBox) o;
		return Double.compare(xmin, that.xmin) == 0
				&& Double.compare(ymin, that.ymin) == 0
				&& Double.compare(xmax, that.xmax) == 0
				&& Double.compare(ymax, that.ymax) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(xmin, ymin, xmax, ymax);
	}

	@Override
	public String toString() {
		return "CurvePlotBoundingBox{" +
				"xmin=" + xmin +
				", ymin=" + ymin +
				", xmax=" + xmax +
				", ymax=" + ymax +
				'}';
	}
}
