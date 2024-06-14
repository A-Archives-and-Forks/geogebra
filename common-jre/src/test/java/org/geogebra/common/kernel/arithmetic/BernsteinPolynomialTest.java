package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.junit.Before;
import org.junit.Test;

public class BernsteinPolynomialTest extends BaseUnitTest {

	private BernsteinPolynomial bernstein;
	private GeoImplicitCurve curve;
	private EuclidianView view;
	private BernsteinPolynomialConverter converter;

	@Before
	public void setUp() {
		add("ZoomIn(0,0,1,1)");
		view = getApp().getEuclidianView1();
		converter =
				new BernsteinPolynomialConverter();

	}

	private void newBernsteinPolynomialPolynomialFrom(String definition) {
		curve = add(definition);
		bernstein = converter.fromImplicitCurve(curve, view.getXmin(), view.getXmax());
	}

	@Test
	public void testEvaluatingBernsteinForm() {
		shouldEvaluateTheSame("3x^3 + 2x^2 + x - 1=0");
		shouldEvaluateTheSame("3x^3 + 2x^2 + 6x + 11=0");
		shouldEvaluateTheSame("2x^4 + 5x^3 + x^2 + x - 1=0");
		shouldEvaluateTheSame("x^3+x=0");
		shouldEvaluateTheSame("x^3+1=0");
		shouldEvaluateTheSame("x^4+5x^3=0");
	}

	private void shouldEvaluateTheSame(String definition) {
		newBernsteinPolynomialPolynomialFrom(definition);
		for (double v = -10.0; v < 10.0; v += 0.01) {
			assertEquals(curve.evaluate(v, 0), bernstein.evaluate(v), 1E-8);
		}
	}

	@Test
	public void testTwoVars() {
		newBernsteinPolynomialPolynomialFrom("x^3 + 2x*y^2 + 2x + y=0");
		assertEquals("(6y\u00B2 + 7y (1 - y) + 3(1 - y)\u00B2) x\u00B3 + (11y\u00B2 + "
						+ "11y (1 - y) + 4(1 - y)\u00B2) x\u00B2 (1 - x) + (7y\u00B2 + 7y (1 - y)"
						+ " + 2(1 - y)\u00B2) x (1 - x)\u00B2 + (y\u00B2"
						+ " + y (1 - y)) (1 - x)\u00B3",
				bernstein.toString());
	}

	@Test
	public void testOneVariableToBernsteinPolynomial() {
		Polynomial polynomial = new Polynomial(getKernel(), "y");
		BernsteinPolynomial bernsteinPolynomial = converter.fromPolynomial(polynomial,
				'y',	0, 1, 2);
		assertEquals("y\u00B2 + y (1 - y)", bernsteinPolynomial.toString());
	}


	@Test
	public void testToString() {
		newBernsteinPolynomialPolynomialFrom("3x^3 + 2x^2 + x - 1=0");
		assertEquals("5x³ + x² (1 - x) - 2x (1 - x)² - (1 - x)³", bernstein.toString());
	}

	@Test
	public void testBernsteinFromCoefficients() {
		bernsteinShouldBe("2x²", 0, 0, 2);
		bernsteinShouldBe("4x² + 2x (1 - x)", 0, 2, 2);
		bernsteinShouldBe("6x² + 6x (1 - x)+ 2(1 - x)²", 2, 2, 2);
		bernsteinShouldBe("14x³ + 22x² (1 - x)+ 17x (1 - x)²+ 5(1 - x)³", 5, 2, 3, 4);
		bernsteinShouldBe("6x + 2(1 - x)",  2, 4);
		bernsteinShouldBe("20x² + 10x (1 - x)+ 2(1 - x)²", 2, 6, 12);
	}

	@Test
	public void testDerivatives() {
		derivativeShouldBe("4", 2, 4);
		derivativeShouldBe("4x", 0, 0, 2);
		derivativeShouldBe("6x + 2(1 - x)", 0, 2, 2);
		derivativeShouldBe("6x + 2(1 - x)", 2, 2, 2);
		derivativeShouldBe("20x² + 10x (1 - x)+ 2(1 - x)²", 5, 2, 3, 4);
	}


	private void derivativeShouldBe(String expected, double... coeffs) {
		bernstein =	converter.fromPowerBasisCoefficients(coeffs, coeffs.length - 1, 0,
						view.getXmin(), view.getXmax()
				);
		assertEquals(expected, bernstein.derivative().toString());
	}

	private void bernsteinShouldBe(String expected, double... coeffs) {
		bernstein =
				converter.fromPowerBasisCoefficients(coeffs, coeffs.length - 1, 0,
						view.getXmin(), view.getXmax()
				);

		assertEquals(expected, bernstein.toString());
	}

	@Test
	public void secondDerivative() {
		derivativeShouldBe("6x + 2(1 - x)", 0, 2, 2);
		assertEquals("4", bernstein.derivative().derivative().toString());
	}
}
