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

	@Before
	public void setUp() {
		add("ZoomIn(0,0,1,1)");
		view = getApp().getEuclidianView1();

	}

	private void newBernsteinPolynomialPolynomialFrom(String definition) {
		curve = add(definition);
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		Polynomial polynomial = functionNVar.getPolynomial();

		bernstein = curve.getDegY() == 0 ?
				new BernsteinPolynomial1Var(polynomial, 'x', view.getXmin(), view.getXmax(),
						curve.getDegX())
		: new BernsteinPolynomial2Var(curve.getKernel(), polynomial,
				functionNVar.getFunctionVariables(), view.getXmin(), view.getXmax(),
				curve.getDegX(), curve.getDegY());
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
		BernsteinPolynomial1Var bernsteinPolynomial = new BernsteinPolynomial1Var(polynomial,
				'y',	0, 1, 2);
		assertEquals("y\u00B2 + y (1 - y)", bernsteinPolynomial.toString());
	}


	@Test
	public void testDerivative() {
		newBernsteinPolynomialPolynomialFrom("3x^3 + 2x^2 + x - 1=0");
		assertEquals("5x³ + x² (1 - x) - 2x (1 - x)² - (1 - x)³", bernstein.toString());
	}

	@Test
	public void testDerivatives() {
		derivativeShouldBe("4x", 0, 0, 2);
	}

	private void derivativeShouldBe(String expected, double... coeffs) {
		BernsteinPolynomial1Var bernsteinPolynomial1Var =
				new BernsteinPolynomial1Var(coeffs, 'x', view.getXmin(), view.getXmax(),
						coeffs.length - 1);
		assertEquals(expected,
				bernsteinPolynomial1Var.derivative().toString());
	}
}
