package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

public interface ImplicitCurvePointsAlgo {
	//	private void findSolutionsInFaces(CurvePlotContext context, List<CurvePlotContext> list) {
//		list.add(context);
//		if (!context.mightHaveSolution()) {
//			return;
//		}
//
//		CurvePlotBoundingBox box = context.boundingBox;
//		if (isBoxSmallEnough(box)) {
//			output.add(new GPoint2D(box.getX1() + box.getWidth() / 2,
//					box.getY1() + box.getHeight() / 2));
//			return;
//		}
//
//		for (CurvePlotContext c : context.split()) {
//			findSolutionsInFaces(c, list);
//		}
//	}
	void compute(CurvePlotContext context, List<CurvePlotContext> list);
}
