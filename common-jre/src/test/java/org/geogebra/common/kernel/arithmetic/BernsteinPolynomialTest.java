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

	private void newCreateBernsteinPolynomialPolynomialFrom(String definition) {
		curve = add(definition);
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		Polynomial polynomial = functionNVar.getPolynomial();
		bernstein = new BernsteinPolynomial(curve.getKernel(), polynomial,
				functionNVar.getFunctionVariables()[0], view.getXmin(), view.getXmax(),
						curve.getDegX()
		);
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
		newCreateBernsteinPolynomialPolynomialFrom(definition);
		for (double v = -10.0; v < 10.0; v += 0.01) {
			assertEquals(curve.evaluate(v, 0), bernstein.evaluate(v), 1E-8);
		}
	}

	@Test
	public void testTwoVars() {
		newCreateBernsteinPolynomialPolynomialFrom("x⁶ - 4y³ + 3x⁴ y=0");
	}
}
