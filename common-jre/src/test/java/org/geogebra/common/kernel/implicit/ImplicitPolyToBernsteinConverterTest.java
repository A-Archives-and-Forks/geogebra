package org.geogebra.common.kernel.implicit;

import org.geogebra.common.BaseUnitTest;
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
		GeoImplicitCurve curve = add("x^3+y^2+3x=4");
		converter.convert(curve);
	}
}
