package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.junit.Before;
import org.junit.Test;

public class BernsteinPolynomialTest extends BaseUnitTest {

	private BernsteinPolynomial bernstein;
	private GeoImplicitCurve curve;

	@Before
	public void setUp() throws Exception {
		curve = add("1 + 5x + 2x^2 + 3x^3 + 4x^4 = 0");
		bernstein =
				new BernsteinPolynomial(curve.getFunctionDefinition().getPolynomial(),
						curve.getKernel(),
						AwtFactory.getPrototype().newRectangle(10, 10),
						curve.getDegX(), curve.getDegY());

	}

	@Test
	public void testMain() {
		assertEquals(4, bernstein.b(0, 0));
		assertEquals(3, bernstein.b(1, 0));
		assertEquals(2, bernstein.b(2, 0));
		assertEquals(5, bernstein.b(3, 0));
		assertEquals(5, bernstein.b(3, 1));
	}

	@Test
	public void testBad() {
		assertEquals(1, bernstein.b(2, 1));

	}

	@Test
	public void testConstruct() {
		bernstein.construct(curve.getDegX());
	}
}
