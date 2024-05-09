package org.geogebra.common.kernel.implicit;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.junit.Before;
import org.junit.Test;

public class ImplicitPolyToBernsteinConverterTest extends BaseUnitTest {
	private ImplicitPolyToBernsteinConverter converter;

	@Before
	public void setUp() {
		converter = new ImplicitPolyToBernsteinConverter();
	}

	@Test
	public void testMain() {
		GeoImplicitCurve curve = add(" 3 * x^3 + 2 * y^2 + 1*y + 1x= 5");
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		BernsteinPolynomial bernstein =
				new BernsteinPolynomial(functionNVar.getPolynomial(),
						curve.getKernel(),
						AwtFactory.getPrototype().newRectangle(100, 100),
						curve.getDegX(), curve.getDegY(), functionNVar.getFunctionVariables());

	}
}
