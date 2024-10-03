package org.geogebra.common.kernel.implicit;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.DoubleUtil;
public class LinkSegments {
	private List<MyPoint> locusPoints;
	private PointList points1;
	private PointList points2;
	private LinkedList<PointList> openPointLists = new LinkedList<>();

	/**
	 * it would be better to adjust LIST_THRESHOLD based on platform
	 */
	public int listThreshold = 480;

	public LinkSegments(List<MyPoint> locusPoints) {
		this.locusPoints = locusPoints;
	}

	public int add(PlotRect r, PlotRectConfigProvider provider) {
		PlotRectConfig config = provider.create(r);
		if (!config.isValid()) {
			return config.flag();
		}
		MyPoint[] pts = provider.getPoints();
		if (pts[0].x > pts[1].x) {
			MyPoint temp = pts[0];
			pts[0] = pts[1];
			pts[1] = temp;
		}

		ListIterator<PointList> itr1 = openPointLists.listIterator();
		ListIterator<PointList> itr2 = openPointLists.listIterator();
		boolean pt1Start = false, pt0End = false;
		while (itr1.hasNext()) {
			points1 = itr1.next();
			if (equal(pts[1], points1.start)) {
				pt1Start = true;
				break;
			}
		}

		while (itr2.hasNext()) {
			points2 = itr2.next();
			if (equal(pts[0], points2.end)) {
				pt0End = true;
				break;
			}
		}

		if (pt1Start && pt0End) {
			itr1.remove();
			points2.mergeTo(points1);
		} else if (pt1Start) {
			points1.extendBack(pts[0]);
		} else if (pt0End) {
			points2.extendFront(pts[1]);
		} else {
			openPointLists.addFirst(new PointList(pts[0], pts[1]));
		}
		if (openPointLists.size() > provider.listThreshold()) {
			flushPoints();
		}

		return config.flag();
	}


	private static boolean equal(MyPoint q1, MyPoint q2) {
		return DoubleUtil.isEqual(q1.x, q2.x, 1e-10)
				&& DoubleUtil.isEqual(q1.y, q2.y, 1e-10);
	}

	public void flushPoints() {
		ListIterator<PointList> itr1 = openPointLists.listIterator();
		while (itr1.hasNext()) {
			points1 = itr1.next();
			locusPoints.add(points1.start);
			locusPoints.addAll(points1.pts);
			locusPoints.add(points1.end);
		}
		openPointLists.clear();
	}

	public void updatePoints(List<MyPoint> locusPoints) {
		this.locusPoints = locusPoints;
	}

	public void flush() {
		flushPoints();
	}

	public void setListThreshold(int threshold) {
		listThreshold = threshold;
	}
}
