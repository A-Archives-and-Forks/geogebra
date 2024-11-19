package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Test;

public class ReduceRootTest extends BaseUnitTest {
	@Test
	public void testReducedRoots() {
		shouldReduce("sqrt(3 + 4)", "sqrt(7)");
		shouldReduce("sqrt(72)", "6 sqrt(2)");
		shouldReduce("sqrt(40 + 4*8)", "6 sqrt(2)");
		shouldReduce("2 + sqrt(3 + 4)", "2 + sqrt(7)");
		shouldReduce("2 * sqrt(3 + 4)", "2 * sqrt(7)");
		shouldReduce("14 + 2 * sqrt(3 + 4)", "14 + 2 * sqrt(7)");
	}

	private void shouldReduce(String actualDef, String expectedDef) {
		GeoNumeric actual = newSymbolicNumeric(actualDef);
		GeoNumeric expected = newSymbolicNumeric(expectedDef);
		ReduceRoot reduceRoot = new ReduceRoot(actual.getDefinition(), getKernel());
		assertEquals(expected.getDefinition().toString(StringTemplate.defaultTemplate),
				reduceRoot.simplify().toString(StringTemplate.defaultTemplate));
	}

	private GeoNumeric newSymbolicNumeric(String actualDef) {
		GeoNumeric actual = add(actualDef);
		actual.setSymbolicMode(true, true);
		return actual;
	}
}
