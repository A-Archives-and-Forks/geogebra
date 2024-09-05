package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.junit.Test;

public class BernsteinPlotCellEdgeTest extends BaseUnitTest {
	@Test
	public void testSplit() {
		BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();
		GeoImplicitCurve curve 	= add("x^3 - y^3 = 0");
		BernsteinPolynomial polynomial =
				converter.from(curve, new BoundsRectangle(0, 1, 0, 1));
		BernsteinPlotCellEdge
				edge = BernsteinPlotCellEdge.create(null, polynomial, 0, 1, 0, EdgeKind.BOTTOM);
		startPointShouldBe(edge, 0, 0);
		BernsteinPlotCellEdge[] split = edge.split();
		startPointShouldBe(split[0], 0, 0);
		startPointShouldBe(split[1], 0.5, 0);
		assertEquals(0.5, split[0].length(), 0);
		assertFalse(polynomial.hasNoSolution());
		assertTrue(split[0].mightHaveSolutions());
		assertFalse(split[1].mightHaveSolutions());
	}

	private void startPointShouldBe(BernsteinPlotCellEdge edge, double x, double y) {
		GPoint2D p = edge.startPoint();
		assertTrue("Expected: (" + x + ", " + y +")  Actual: (" + p.x + ", " + p.y +")"
				,p.x == x && p.y == y);

	}
}
