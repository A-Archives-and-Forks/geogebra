package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.geogebra.common.kernel.MyPoint;
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

	public int add(PlotRect r, int factor) {
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

		double q1 = 0.0, q2 = 0.0;

		pts = gridType.getPoints(r);
		if (pts == null) {
			return EdgeConfig.EMPTY;
		}

		q1 = gridType.getQ1(r);
		q2 = gridType.getQ2(r);

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
