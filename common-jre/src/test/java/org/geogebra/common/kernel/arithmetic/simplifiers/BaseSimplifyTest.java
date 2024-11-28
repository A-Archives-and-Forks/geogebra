package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.SimplifyUtils;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Before;

public abstract class BaseSimplifyTest extends BaseUnitTest {

	SimplifyUtils utils;

	protected abstract SimplifyNode getSimplifier();

	@Before
	public void setUp() throws Exception {
		utils = new SimplifyUtils(getKernel());
	}

	protected final void shouldSimplify(String from, String to) {
		shouldSimplify(from, to, getSimplifier());
	}

	protected final void shouldSimplify(String actualDef, String expectedDef, SimplifyNode simplifier) {
		GeoNumeric expected = newSymbolicNumeric(expectedDef);
		GeoNumeric actual = newSymbolicNumeric(actualDef);
		assertTrue(actualDef + " is not accepted by " + simplifier.name(),
				simplifier.isAccepted(actual.getDefinition()));
		ExpressionNode applied = simplifier.apply(actual.getDefinition());
//		assertEquals(expected.getDefinition().evaluateDouble(), applied.evaluateDouble(),
//				Kernel.MAX_PRECISION);
		shouldSerialize(expected.getDefinition(), applied);
	}

	protected static void shouldSerialize(GeoElement expected, GeoElement actual) {
		shouldSerialize(expected.getDefinition(), actual.getDefinition());
	}

	protected static void shouldSerialize(ExpressionNode expected, ExpressionNode actual) {
		assertEquals(expected.toString(StringTemplate.defaultTemplate),
				actual.toString(StringTemplate.defaultTemplate));
	}

	protected GeoNumeric newSymbolicNumeric(String actualDef) {
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
