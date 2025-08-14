package org.geogebra.common.properties.factory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.QuadraticEquationFormProperty;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoElementPropertiesFactoryTest extends BaseAppTestSetup {

	@BeforeEach
	public void setupApp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testPoint() {
		GeoPoint zeroPoint = evaluateGeoElement("(0,0)");
		GeoPoint onePoint = evaluateGeoElement("(1,1)");
		PropertiesArray propertiesArray = new GeoElementPropertiesFactory()
				.createGeoElementProperties(getKernel().getAlgebraProcessor(),
						getApp().getLocalization(), List.of(zeroPoint, onePoint));
		Property[] pointProperties = propertiesArray.getProperties();

		assertAll(
				() -> assertEquals("Name", pointProperties[0].getName()),
				() -> assertEquals("Show", pointProperties[1].getName()),
				() -> assertEquals("Set color", pointProperties[2].getName()),
				() -> assertEquals("Point Style", pointProperties[3].getName()),
				() -> assertEquals("Size", pointProperties[4].getName()),
				() -> assertEquals("Set caption style", pointProperties[5].getName()),
				() -> assertEquals("Show trace", pointProperties[6].getName()),
				() -> assertEquals("Fixed", pointProperties[7].getName()),
				() -> assertEquals("Show in Algebra View", pointProperties[8].getName())
		);
	}

	@Test
	public void testEquationFormProperty() {
		GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

		GeoLine line = evaluateGeoElement("Line((-1,-1),(1,2))");
		PropertiesArray lineProperties = propertiesFactory.createGeoElementProperties(
				getAlgebraProcessor(), getApp().getLocalization(), List.of(line));
		assertTrue(containsLinearEquationFormProperty(lineProperties));
		assertFalse(containsQuadraticEquationFormProperty(lineProperties));

		GeoConic circle = evaluateGeoElement("xx+yy=1");
		PropertiesArray circleProperties = propertiesFactory.createGeoElementProperties(
				getAlgebraProcessor(), getApp().getLocalization(), List.of(circle));
		assertFalse(containsLinearEquationFormProperty(circleProperties));
		assertTrue(containsQuadraticEquationFormProperty(circleProperties));
	}

	private boolean containsLinearEquationFormProperty(PropertiesArray array) {
		return Arrays.stream(array.getProperties())
				.anyMatch(property -> property instanceof NamedEnumeratedPropertyCollection<?, ?>
						&& ((NamedEnumeratedPropertyCollection<?, ?>) property)
						.getFirstProperty() instanceof LinearEquationFormProperty);
	}

	private boolean containsQuadraticEquationFormProperty(PropertiesArray array) {
		return Arrays.stream(array.getProperties())
				.anyMatch(property -> property instanceof NamedEnumeratedPropertyCollection<?, ?>
						&& ((NamedEnumeratedPropertyCollection<?, ?>) property)
						.getFirstProperty() instanceof QuadraticEquationFormProperty);
	}
}