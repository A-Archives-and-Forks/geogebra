package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.junit.Test;

public class CurvePlotContextTest extends BaseUnitTest {
	private CurvePlotContext context;
	private BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();

	@Test
	public void testSpitContext() {
		context = new CurvePlotContext(getDefaultBoundingBox(),
				converter.from(add("x^4 + x^3y + y^2"), 0, 1));
		CurvePlotContext[] contexts = context.split();
		assertEquals(0.5, contexts[0].evaluatePolynomAt(0, 0.5), 0);
	}

	private CurvePlotBoundingBox getDefaultBoundingBox() {
		return new CurvePlotBoundingBox(0, 1, 0, 1);
	}
}
