package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.util.DoubleUtil;
public class LinkSegments {
	private final GeoImplicitCurve geoImplicitCurve;
	private List<MyPoint> locusPoints;
	private MyPoint[] pts = new MyPoint[2];
	private PointList p1;
	private PointList p2;
	private MyPoint temp;
	private ListIterator<PointList> itr1;
	private ListIterator<PointList> itr2;
	private LinkedList<PointList> openList = new LinkedList<>();

	/**
	 * it would be better to adjust LIST_THRESHOLD based on platform
	 */
	public int listThreshold = 48;

	public LinkSegments(GeoImplicitCurve geoImplicitCurve, ArrayList<MyPoint> locusPoints) {

		this.geoImplicitCurve = geoImplicitCurve;
		this.locusPoints = locusPoints;
	}

	public int add(Rect r, int factor) {
		EdgeConfig status = create(r, factor);
		if (status == EdgeConfig.VALID) {
			if (pts[0].x > pts[1].x) {
				temp = pts[0];
				pts[0] = pts[1];
				pts[1] = temp;
			}
			itr1 = openList.listIterator();
			itr2 = openList.listIterator();
			boolean flag1 = false, flag2 = false;
			while (itr1.hasNext()) {
				p1 = itr1.next();
				if (equal(pts[1], p1.start)) {
					flag1 = true;
					break;
				}
			}

			while (itr2.hasNext()) {
				p2 = itr2.next();
				if (equal(pts[0], p2.end)) {
					flag2 = true;
					break;
				}
			}

			if (flag1 && flag2) {
				itr1.remove();
				p2.mergeTo(p1);
			} else if (flag1) {
				p1.extendBack(pts[0]);
			} else if (flag2) {
				p2.extendFront(pts[1]);
			} else {
				openList.addFirst(new PointList(pts[0], pts[1]));
			}
			if (openList.size() > listThreshold) {
				abortList();
			}
		}
		return status.flag();
	}


	private static boolean equal(MyPoint q1, MyPoint q2) {
		return DoubleUtil.isEqual(q1.x, q2.x, 1e-10)
				&& DoubleUtil.isEqual(q1.y, q2.y, 1e-10);
	}


	public EdgeConfig create(PlotRect r, int factor) {
		EdgeConfig gridType = EdgeConfig.fromFlag(config(r));
		if (gridType == EdgeConfig.T0101 || gridType == EdgeConfig.T_INV) {
			return gridType;
		}

		double x1 = r.x1(), x2 = r.x2(), y1 = r.y1(), y2 = r.y2();
		double tl = r.topLeft(), tr = r.topRight(), br = r.bottomRight(),
				bl = r.bottomLeft();
		double q1 = 0.0, q2 = 0.0;

		switch (gridType) {
		// one or three corners are inside / outside
		case T0001:
		case T0010:
			pts = gridType.getPoints(r);
			q1 = gridType.getQ1(r);
			q2 = gridType.getQ2(r);
			break;

//			pts[0] = new MyPoint(x2,
//					GeoImplicitCurve.interpolate(br, tr, y2,
//							y1), SegmentType.MOVE_TO);
//			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(br, bl, x2, x1),
//					y2, SegmentType.LINE_TO);
//			q1 = minAbs(br, tr);
//			q2 = minAbs(br, bl);
//			break;

		case T0100:
			pts[0] = new MyPoint(x2, GeoImplicitCurve.interpolate(tr, br, y1,
					y2), SegmentType.MOVE_TO);
			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(tr, tl, x2, x1),
					y1, SegmentType.LINE_TO);
			q1 = minAbs(tr, br);
			q2 = minAbs(tr, tl);
			break;

		case T0111:
			pts[0] = new MyPoint(x1,
					GeoImplicitCurve.interpolate(tl, bl, y1, y2),
					SegmentType.MOVE_TO);
			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(tl, tr, x1, x2),
					y1, SegmentType.LINE_TO);
			q1 = minAbs(bl, tl);
			q2 = minAbs(tl, tr);
			break;

		// two consecutive corners are inside / outside
		case T0011:
			pts[0] = new MyPoint(x1, GeoImplicitCurve.interpolate(tl, bl, y1,
					y2), SegmentType.MOVE_TO);
			pts[1] = new MyPoint(x2,
					GeoImplicitCurve.interpolate(tr, br, y1, y2),
					SegmentType.LINE_TO);
			q1 = minAbs(tl, bl);
			q2 = minAbs(tr, br);
			break;

		case T0110:
			pts[0] = new MyPoint(GeoImplicitCurve.interpolate(tl, tr, x1, x2),
					y1, SegmentType.MOVE_TO);
			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(bl, br, x1, x2),
					y2, SegmentType.LINE_TO);
			q1 = minAbs(tl, tr);
			q2 = minAbs(bl, br);
			break;
		default:
			return EdgeConfig.EMPTY;
		}
		// check continuity of the function between P1 and P2
		double p = Math.abs(this.geoImplicitCurve
				.evaluateImplicitCurve(pts[0].x, pts[0].y, factor));
		double q = Math.abs(this.geoImplicitCurve
				.evaluateImplicitCurve(pts[1].x, pts[1].y, factor));
		if (p <= q1 && q <= q2) {
			return EdgeConfig.VALID;
		}
		return EdgeConfig.EMPTY;
	}

	private static double minAbs(double a, double b) {
		return Math.min(Math.abs(a), Math.abs(b));
	}


	public void abortList() {
		itr1 = openList.listIterator();
		while (itr1.hasNext()) {
			p1 = itr1.next();
			locusPoints.add(p1.start);
			locusPoints.addAll(p1.pts);
			locusPoints.add(p1.end);
		}
		openList.clear();
	}

	public void updatePoints(List<MyPoint> locusPoints) {
		this.locusPoints = locusPoints;
	}

	public void abort() {
		abortList();
	}

	public int config(PlotRect r) {
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
	public int sign(double val) {
		if (Double.isInfinite(val) || Double.isNaN(val)) {
			return CornerConfig.T_INV;
		} else if (val > 0.0) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setListThreshold(int threshold) {
		listThreshold = threshold;
	}
}
