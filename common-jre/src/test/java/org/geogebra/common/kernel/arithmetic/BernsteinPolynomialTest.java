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
	public void setUp() {
//		curve = add("1 + 5x + 2x^2 + 3x^3 + 4x^4 = 0");
		curve = add("x^3+2x^2+3x=0");
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		Polynomial polynomial = functionNVar.getPolynomial();
		bernstein = new BernsteinPolynomial(polynomial,
						curve.getKernel(),
						AwtFactory.getPrototype().newRectangle(1, 1),
						curve.getDegX(), curve.getDegY(),
						functionNVar.getFunctionVariables());

	}

	@Test
	public void testConstruct() {
		bernstein.construct(curve.getDegX());
		assertEquals("", bernstein.toString());
	}
}
