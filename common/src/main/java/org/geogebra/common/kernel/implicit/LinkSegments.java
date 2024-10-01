package org.geogebra.common.kernel.implicit;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.DoubleUtil;
public class LinkSegments {
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

	public LinkSegments(List<MyPoint> locusPoints) {
		this.locusPoints = locusPoints;
	}

	public int add(PlotRect r, EdgeConfigProvider provider) {
		EdgeConfig config = provider.create(r);
		pts = provider.getPoints();
		if (config == EdgeConfig.VALID) {
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
		return config.flag();
	}


	private static boolean equal(MyPoint q1, MyPoint q2) {
		return DoubleUtil.isEqual(q1.x, q2.x, 1e-10)
				&& DoubleUtil.isEqual(q1.y, q2.y, 1e-10);
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

	public void setListThreshold(int threshold) {
		listThreshold = threshold;
	}
}
