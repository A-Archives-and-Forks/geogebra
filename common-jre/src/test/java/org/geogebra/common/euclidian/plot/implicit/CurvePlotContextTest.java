package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.junit.Test;

public class CurvePlotContextTest extends BaseUnitTest {
	private CurvePlotContext context;
	private BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();

	@Test
	public void testSpitContext() {
		BernsteinPolynomial bernstein = converter.from(add("x^4 + x^3y + y^2"), 0, 1);
		context = new CurvePlotContext(getDefaultBoundingBox(),
				bernstein);
		CurvePlotContext[] contexts = context.split();

		for (CurvePlotContext ctx: contexts)  {
			checkEvalOnContext(bernstein, ctx);
		}
	}

	private void checkEvalOnContext(BernsteinPolynomial bernstein, CurvePlotContext context) {
		double offsetX = context.boundingBox.getXmin();
		double offsetY = context.boundingBox.getYmin();
		assertSameValue(bernstein, offsetX + 0, offsetY + 0, context, 0, 0);
		assertSameValue(bernstein, offsetX + 0.25, offsetY + 0, context, 0.5, 0);
		assertSameValue(bernstein, offsetX + 0.5, offsetY + 0, context, 1, 0);
		assertSameValue(bernstein, offsetX + 0, offsetY + 0.25, context, 0, 0.5);
		assertSameValue(bernstein, offsetX + 0.25, offsetY + 0.25, context, 0.5, 0.5);
		assertSameValue(bernstein, offsetX + 0.25, offsetY + 0.5, context, 0.5, 1);
		assertSameValue(bernstein, offsetX + 0.5, offsetY + 0.5, context, 1, 1);
	}

	private static void assertSameValue(BernsteinPolynomial bernstein, double x0, double y0,
			CurvePlotContext context, double x, double y) {
		assertEquals(bernstein.evaluate(x0, y0), context.polynomial.evaluate(x, y),
				0);
	}

	private CurvePlotBoundingBox getDefaultBoundingBox() {
		return new CurvePlotBoundingBox(0, 1, 0, 1);
	}

}
