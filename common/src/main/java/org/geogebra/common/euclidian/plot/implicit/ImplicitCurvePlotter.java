package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

public class ImplicitCurvePlotter {
	public static final int MAX_RECURSION = 3;
	private final BernsteinPolynomial polynomial;
	private final CurvePlotContext context;
	private BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();

	public ImplicitCurvePlotter(GeoImplicitCurve curve, EuclidianViewBounds bounds) {
		polynomial = converter.fromImplicitCurve(curve, bounds.getXmin(), bounds.getXmax());
		context = new CurvePlotContext(new CurvePlotBoundingBox(
				bounds.getXmin(), bounds.getYmin(),
				bounds.getXmax(), bounds.getYmax()),
				polynomial);
	}

	public void plot() {
		for (int i = 0; i < MAX_RECURSION; i++) {
			update();
		}
	}

	private void update() {
		for (CurvePlotContext ctx: context.split()) {
			ctx.classifyCells();
			ctx.findSolutionsInEdges();
			ctx.findSolutionsInBox();
			ctx.linkSolutions();
		}
	}
}
