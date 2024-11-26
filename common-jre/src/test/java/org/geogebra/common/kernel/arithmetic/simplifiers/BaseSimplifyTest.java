package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;

public abstract class BaseSimplifyTest extends BaseUnitTest {
	protected abstract SimplifyNode getSimplifier();

	protected final void shouldSimplify(String from, String to) {
		shouldSimplify(from, to, getSimplifier());
	}

	protected final void shouldSimplify(String actualDef, String expectedDef, SimplifyNode simplifier) {
		GeoNumeric expected = newSymbolicNumeric(expectedDef);
		GeoNumeric actual = newSymbolicNumeric(actualDef);
		assertTrue(actualDef + " is not accepted by " + simplifier.name(),
				simplifier.isAccepted(actual.getDefinition()));
		assertEquals(expected.getDefinition().toString(StringTemplate.defaultTemplate),
				simplifier.apply(actual.getDefinition())
						.toString(StringTemplate.defaultTemplate));
	}

	private GeoNumeric newSymbolicNumeric(String actualDef) {
		GeoNumeric actual = add(actualDef);
		actual.setSymbolicMode(true, true);
		return actual;
	}


	protected final void shouldAccept(String def) {
		assertTrue(isAccepted(def));
	}

	protected final void shouldNotAccept(String def) {
		assertFalse(isAccepted(def));
	}

	private boolean isAccepted(String def) {
		return getSimplifier().isAccepted(add(def).getDefinition());
	}
}
