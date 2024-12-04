package org.geogebra.common.exam.restrictions.cvte;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTests;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;
import org.junit.Before;
import org.junit.Test;

public class CvteValueConverterTests extends BaseExamTests {

	@Before
	public void setup() {
		setInitialApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testValueRestrictions() {
		ToStringConverter<GeoElement> converter = new CvteValueConverter(null);

		// For Lines, Rays, Conics, Implicit Equations and Functions created with command or tool:
		// When using the ANS button the "Definition" is inserted into the AV inputBar.
		GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
		assertEquals("Line((0, 0), (1, 2))", converter.convert(line));

		GeoElement ray = evaluateGeoElement("Ray((0, 0), (1, 2))");
		assertEquals("Ray((0, 0), (1, 2))", converter.convert(ray));

		GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
		assertEquals("Circle((0, 0), 1)", converter.convert(circle));
		
		// TODO are there any examples for implicit equation or function created with a command?
	}
}
