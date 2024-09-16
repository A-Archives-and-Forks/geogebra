package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Test;

public class RationalizableFractionTest extends BaseUnitTest {
	@Test
	public void testSupported() {
		shouldBeSupported("1 / sqrt(2)");
		shouldBeSupported("1 / (sqrt(2) + 1)");
		shouldBeSupported("1 / (1 - sqrt(2))");
		shouldBeSupported("sqrt(3) / sqrt(2)");
		shouldBeSupported("(sqrt(3) + 1) / sqrt(2)");
		shouldBeSupported("(sqrt(3) + 1) / (sqrt(2) - 1)");
	}

	private void shouldBeSupported(String definition) {
		GeoElementND geo = add(definition);
		assertTrue(RationalizableFraction.isSupported(geo));
	}

	@Test
	public void testUnsupported() {
		shouldBeUnsupported("1 / 2");
		shouldBeUnsupported("1 / (sqrt(2) + sqrt(3))");
		shouldBeUnsupported("(sqrt(3) + sqrt(2) + 1) / sqrt(2)");
		shouldBeUnsupported("(sqrt(3) + sqrt(2)) / sqrt(2)");
	}

	private void shouldBeUnsupported(String definition) {
		GeoElementND geo = add(definition);
		assertFalse(RationalizableFraction.isSupported(geo));
	}

	@Test
	public void decimalValueShouldBeOK() {
		GeoNumeric num = add("1/sqrt(2)");
		num.setSymbolicMode(false, true);
		assertEquals("0.71", num.getFormulaString(StringTemplate.defaultTemplate, true));
		num.setSymbolicMode(true, true);
	}

	@Test
	public void testRationalizationLeafNumerator() {
		rationalizationShouldBe("1 / sqrt(2)", "sqrt(2) / 2");
		rationalizationShouldBe("1 / (sqrt(2) + 1)", "sqrt(2) - 1");
		rationalizationShouldBe("1 / (sqrt(2) - 1)", "sqrt(2) + 1");
		rationalizationShouldBe("1 / (1 + sqrt(2))", "-1 + sqrt(2)");
		rationalizationShouldBe("1 / (1 - sqrt(2))", "-1 - sqrt(2)");
		rationalizationShouldBe("1 / (sqrt(2) + 3)", "(sqrt(2) - 3) / -7");
		rationalizationShouldBe("1 / (sqrt(2) - 3)", "(sqrt(2) + 3) / -7");
	}

	@Test
	public void name() {
		rationalizationShouldBe("1 / (sqrt(2) - 1)", "sqrt(2) + 1");
		rationalizationShouldBe("(sqrt(2) + 1) / sqrt(2)", "(2 + sqrt(2)) / 2");
		rationalizationShouldBe("(1 + sqrt(2)) / sqrt(2)", "(sqrt(2) + 2) / 2");

	}

	private void rationalizationShouldBe(String definition, String expected) {
		GeoNumeric num = add(definition);
		num.setSymbolicMode(true, true);
		assertEquals(expected, num.getFormulaString(StringTemplate.defaultTemplate, true));
	}
}
