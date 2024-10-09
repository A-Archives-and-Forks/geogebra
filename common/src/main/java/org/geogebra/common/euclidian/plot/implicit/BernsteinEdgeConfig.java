package org.geogebra.common.euclidian.plot.implicit;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
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
			return new MyPoint[] { new MyPoint(r.x1(),
					GeoImplicitCurve.interpolate(r.bottomLeft(), r.topLeft(), r.y2(),
							r.y1()), SegmentType.MOVE_TO),
					new MyPoint(GeoImplicitCurve.interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()),
					r.y2(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * bottom right corner is inside / outside
	 */
	T0010(2) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {new MyPoint(r.x2(),
					GeoImplicitCurve.interpolate(r.bottomRight(), r.topRight(), r.y2(),
							r.y1()), SegmentType.MOVE_TO),
					new MyPoint(GeoImplicitCurve.interpolate(r.bottomRight(), r.bottomLeft(), r.x2(), r.x1()),
					r.y2(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * both corners at the bottom are inside / outside
	 */
	T0011(3) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {new MyPoint(r.x1(), GeoImplicitCurve.interpolate(r.topLeft(), r.bottomLeft(), r.y1(),
					r.y2()), SegmentType.MOVE_TO),
					new MyPoint(r.x2(),
					GeoImplicitCurve.interpolate(r.topRight(), r.bottomRight(), r.y1(), r.y2()),
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
					new MyPoint(r.x2(), GeoImplicitCurve.interpolate(r.topRight(), r.bottomRight(), r.y1(),
							r.y2()), SegmentType.MOVE_TO),
					new MyPoint(GeoImplicitCurve.interpolate(r.topRight(), r.topLeft(), r.x2(), r.x1()),
							r.y1(), SegmentType.LINE_TO)
			};
		}
	},

	/**
	 * opposite corners are inside / outside. NOTE: This configuration is
	 * regarded as invalid
	 */
	T0101(5),

	/**
	 * both the corners at the left are inside / outside
	 */
	T0110(6) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {new MyPoint(GeoImplicitCurve.interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
					r.y1(), SegmentType.MOVE_TO),
					new MyPoint(GeoImplicitCurve.interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()),
					r.y2(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * only top left corner is inside / outside
	 */
	T0111(7) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			return new MyPoint[] {new MyPoint(r.x1(),
					GeoImplicitCurve.interpolate(r.topLeft(), r.bottomLeft(), r.y1(), r.y2()),
					SegmentType.MOVE_TO),
					new MyPoint(GeoImplicitCurve.interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
					r.y1(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * invalid configuration. expression value is undefined / infinity for at
	 * least one of the corner
	 */
	T_INV(-1),

	EMPTY(0),

	VALID(10),
	TOPBOTTOM(11) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			BernsteinPlotRect rect = BernsteinPlotRect.as(r);
			logRect(rect);
			GPoint2D p1 = rect.getSolution(EdgeKind.TOP);
			GPoint2D p2 = rect.getSolution(EdgeKind.BOTTOM);
			return new MyPoint[] {new MyPoint(p1.x, p1.y, SegmentType.MOVE_TO),
					new MyPoint(p2.x, p2.y, SegmentType.LINE_TO)};
		}

	}, LEFTRIGHT(12) {
		@Override
		public MyPoint[] getPoints(PlotRect r) {
		BernsteinPlotRect rect = BernsteinPlotRect.as(r);
		GPoint2D p1 = rect.getSolution(EdgeKind.LEFT);
		GPoint2D p2 = rect.getSolution(EdgeKind.RIGHT);
			return new MyPoint[] {new MyPoint(p1.x, p1.y, SegmentType.MOVE_TO),
				new MyPoint(p2.x, p2.y, SegmentType.LINE_TO)};
		}

	},
	X(13){
		@Override
		public MyPoint[] getPoints(PlotRect r) {
			BernsteinPlotRect rect = BernsteinPlotRect.as(r);
			GPoint2D top = rect.getSolution(EdgeKind.TOP);
			GPoint2D left = rect.getSolution(EdgeKind.LEFT);
			GPoint2D bottom = rect.getSolution(EdgeKind.BOTTOM);
			GPoint2D right = rect.getSolution(EdgeKind.RIGHT);
			return new MyPoint[] {new MyPoint(left.x, top.y, SegmentType.MOVE_TO),
					new MyPoint(right.x, bottom.y, SegmentType.LINE_TO),
			new MyPoint(right.x, top.y, SegmentType.MOVE_TO),
			new MyPoint(left.x, bottom.y, SegmentType.LINE_TO)};
		}

	}, CENTER(14) {
		@Override
		public MyPoint[] getPoints(PlotRect rect) {
			BernsteinPlotRect r = BernsteinPlotRect.as(rect);
			double xMiddle = r.x1() + Math.abs(r.x2() - r.x1()) / 2;
			double yMiddle = r.y1() + Math.abs(r.y2() - r.y1()) / 2;
			return new MyPoint[] {new MyPoint(r.x1(), r.y1(), SegmentType.MOVE_TO),
					new MyPoint(xMiddle, yMiddle, SegmentType.LINE_TO)};
		}

	};

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