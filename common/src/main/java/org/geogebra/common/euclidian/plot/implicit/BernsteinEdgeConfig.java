package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.kernel.implicit.GeoImplicitCurve.interpolate;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.implicit.PlotRect;
import org.geogebra.common.kernel.implicit.PlotRectConfig;
import org.geogebra.common.util.debug.Log;

public enum BernsteinEdgeConfig implements PlotRectConfig {
	/**
	 * All corners are inside / outside
	 */
	T0000(0),

	/**
	 * only bottom left corner is inside / outside
	 */
	T0001(1) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {
					moveTo(r.x1(), interpolate(r.bottomLeft(), r.topLeft(), r.y2(),
							r.y1())),
					new MyPoint(interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()),
					r.y2(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * bottom right corner is inside / outside
	 */
	T0010(2) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {
					moveTo(r.x2(), interpolate(r.bottomRight(), r.topRight(), r.y2(),
							r.y1())),
					new MyPoint(interpolate(r.bottomRight(), r.bottomLeft(), r.x2(), r.x1()),
					r.y2(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * both corners at the bottom are inside / outside
	 */
	T0011(3) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {
					moveTo(r.x1(), interpolate(r.topLeft(), r.bottomLeft(), r.y1(),
							r.y2())),
					new MyPoint(r.x2(),
					interpolate(r.topRight(), r.bottomRight(), r.y1(), r.y2()),
					SegmentType.LINE_TO)};
		}
	},

	/**
	 * top left corner is inside / outside
	 */
	T0100(4) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {
					moveTo(r.x2(), interpolate(r.topRight(), r.bottomRight(), r.y1(),
							r.y2())),
					new MyPoint(interpolate(r.topRight(), r.topLeft(), r.x2(), r.x1()),
							r.y1(), SegmentType.LINE_TO)
			};
		}
	},

	/**
	 * opposite corners are inside / outside. NOTE: This configuration is
	 * regarded as invalid
	 */
	T0101(5) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {
					moveTo(r.x1(), interpolate(r.topLeft(), r.bottomLeft(),r.y1(), r.y2())),
					lineTo(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()), r.y1()),
					moveTo(r.x2(), (r.y1() + r.y2()) / 2),
					lineTo((r.x1() + r.x2()) / 2, r.y2())
			};
		}
	},

	/**
	 * both the corners at the left are inside / outside
	 */
	T0110(6) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {
					moveTo(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
							r.y1()),
					new MyPoint(interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()),
					r.y2(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * only top left corner is inside / outside
	 */
	T0111(7) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {moveTo(r.x1(),
					interpolate(r.topLeft(), r.bottomLeft(), r.y1(), r.y2())),
					new MyPoint(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
					r.y1(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * invalid configuration. expression value is undefined / infinity for at
	 * least one of the corner
	 */
	T_INV(-1),

	EMPTY(0),

	VALID(10);

	private static MyPoint moveTo(double x, double y) {
		return new MyPoint(x, y, SegmentType.MOVE_TO);
	}

	private static MyPoint lineTo(double x, double y) {
		return new MyPoint(x, y, SegmentType.LINE_TO);
	}

	public void logRect(BernsteinPlotRect r) {
		Log.debug(this + " " + r.debugString());
	}

	private final int flag;

	private static Map<Integer, BernsteinEdgeConfig> map = new HashMap<>();

	static {
		for (BernsteinEdgeConfig config: BernsteinEdgeConfig.values()) {
			map.put(config.flag, config);
		}
	}

	BernsteinEdgeConfig(int flag) {
		this.flag = flag;
	}

	@Override
	public int flag() {
		return flag;
	}

	@Override
	public MyPoint[] getPoints(PlotRect r) {
		return null;
	}

	@Override
	public boolean isValid() {
		return this == VALID;
	}

	@Override
	public boolean isInvalid() {
		return this == T_INV;
	}

	@Override
	public boolean isEmpty() {
		return this == EMPTY;
	}


	public static BernsteinEdgeConfig fromFlag(int config) {
		return map.getOrDefault(config, T_INV);
	}

	private static double minAbs(double a, double b) {
		return Math.min(Math.abs(a), Math.abs(b));
	}

}