package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.junit.Before;
import org.junit.Test;

public class BernsteinPolynomialTest extends BaseUnitTest {

	private BernsteinPolynomial bernstein;
	private GeoImplicitCurve curve;
	private EuclidianView view;

	@Before
	public void setUp() {
		add("ZoomIn(0,0,1,1)");
		view = getApp().getEuclidianView1();
//		newBernsteinPolynomialFrom("x^3+x^2 +2x-1=0");

	}

	private void newCreateBernsteinPolynomialPolynomialFrom(String definition) {
		curve = add(definition);
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		Polynomial polynomial = functionNVar.getPolynomial();
		bernstein = new BernsteinPolynomial(polynomial,
						curve.getKernel(),
						view.getXmin(), view.getXmax(),
						curve.getDegX(), curve.getDegY(),
						functionNVar.getFunctionVariables());
	}

	@Test
	public void xminShouldGiveZero() {
		newCreateBernsteinPolynomialPolynomialFrom("4 x^3+ 3 x^2 + 2x - 1=0");
		assertEquals(0, bernstein.evaluate(view.getXmin()), 0);
	}

	@Test
	public void xmaxShouldGiveOne() {
		newCreateBernsteinPolynomialPolynomialFrom("4 x^3+ 3 x^2 + 2x + 1=0");
		assertEquals(curve.evaluate(view.getXmin(), 0), bernstein.evaluate(view.getXmax()), 0);
	}

	@Test
	public void testOriginalCoefficents() {
		newCreateBernsteinPolynomialPolynomialFrom("4 x^3 + 3 x^2 + 2x - 1=0");
		double[] expected = {-1, 2, 3, 4};
		assertArrayEquals(expected, bernstein.getCoeffsX(), 0);
		assertEquals(4.0, bernstein.bernsteinCoefficient(0, 0), 0);
		assertEquals(3.0, bernstein.bernsteinCoefficient(1, 0), 0);
		assertEquals(7.0, bernstein.bernsteinCoefficient(1, 1), 0);
		assertEquals(2, bernstein.bernsteinCoefficient(2, 0), 0);
	}

	@Test
	public void testBis() {
		newCreateBernsteinPolynomialPolynomialFrom("4 x^3+ 3 x^2 + 2x + 1=0");
		capitalBiShoudBe("4", 0);
		capitalBiShoudBe("7x + 3 (1 - x)", 1);
//		capitalBiShoudBe("9x\u00b2 - 7x + 2(1 - x)\u00b2", 2);
		capitalBiShoudBe("10x\u00b3 + 10 x\u00b2 (1-x) + 5 x (1-x)\u00b2 + 1 (1-x)\u00b3", 3 );
	}

	private void capitalBiShoudBe(String number, int i) {
		assertEquals(number, bernstein.createB(i).toString(StringTemplate.defaultTemplate));
	}
}
