package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class BaseSimplifyTest extends BaseUnitTest {

	protected void shouldSimplify(String actualDef, String expectedDef, SimplifyNode simplifier) {
		GeoNumeric expected = newSymbolicNumeric(expectedDef);
		GeoNumeric actual = newSymbolicNumeric(actualDef);
		assertEquals(expected.getDefinition().toString(StringTemplate.defaultTemplate),
				simplifier.apply(actual.getDefinition())
						.toString(StringTemplate.defaultTemplate));
	}

	private GeoNumeric newSymbolicNumeric(String actualDef) {
		GeoNumeric actual = add(actualDef);
		actual.setSymbolicMode(true, true);
		return actual;
	}
}
