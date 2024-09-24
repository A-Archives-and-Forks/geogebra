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
	public void testRationalizationNumeratorIsConstant() {
		rationalizationShouldBe("1 / sqrt(2)", "sqrt(2) / 2");
		rationalizationShouldBe("2 / sqrt(2)", "sqrt(2)");
		rationalizationShouldBe("1 / (sqrt(2) + 1)", "sqrt(2) - 1");
		rationalizationShouldBe("1 / (sqrt(2) - 1)", "sqrt(2) + 1");
		rationalizationShouldBe("1 / (1 + sqrt(2))", "-1 + sqrt(2)");
		rationalizationShouldBe("1 / (1 - sqrt(2))", "-1 - sqrt(2)");
		rationalizationShouldBe("1 / (sqrt(2) + 3)", "(sqrt(2) - 3) / -7");
		rationalizationShouldBe("1 / (sqrt(2) - 3)", "(sqrt(2) + 3) / -7");
	}

	@Test
	public void testRationalizationNumeratorIsSquareRoot() {
		rationalizationShouldBe("sqrt(3) / sqrt(2)", "sqrt(6) / 2");
		rationalizationShouldBe("(sqrt(3) + 1) / sqrt(2)", "(sqrt(6) + sqrt(2)) / 2");
		rationalizationShouldBe("(1 + sqrt(3)) / sqrt(2)", "(sqrt(2) + sqrt(6)) / 2");
		rationalizationShouldBe("sqrt(3) / (sqrt(2) - 1)", "sqrt(3) (sqrt(2) + 1)");
		rationalizationShouldBe("sqrt(3) / (sqrt(2) + 1)", "sqrt(3) (sqrt(2) - 1)");
		rationalizationShouldBe("(sqrt(3) + 1) / (sqrt(2) + 1)",
				"(sqrt(3) + 1) (sqrt(2) - 1)");
		rationalizationShouldBe("(sqrt(3) + 1) / (sqrt(2) - 1)",
				"(sqrt(3) + 1) (sqrt(2) + 1)");
	}

	@Test
	public void testFractionIsOne() {
		rationalizationShouldBe("sqrt(3) / sqrt(3)", "1");
		rationalizationShouldBe("(2 + sqrt(3)) / (2 + sqrt(3))", "1");
		rationalizationShouldBe("(sqrt(3) + 2) / (sqrt(3) + 2)", "1");
		rationalizationShouldBe("-sqrt(3) / sqrt(3)", "-1");
		rationalizationShouldBe("sqrt(3) / -sqrt(3)", "-1");
		rationalizationShouldBe("-sqrt(3) / -sqrt(3)", "1");
		rationalizationShouldBe("-(sqrt(3) + 2) / (sqrt(3) + 2)", "-1");
		rationalizationShouldBe("(sqrt(3) + 2) / -(sqrt(3) + 2)", "-1");
		rationalizationShouldBe("-(sqrt(3) + 2) / -(sqrt(3) + 2)", "1");
		rationalizationShouldBe("(3 * sqrt(2)) / sqrt(18)", "1");
	}

	@Test
	public void name() {
		rationalizationShouldBe("(3 * sqrt(3)) / sqrt(3)", "3");
		rationalizationShouldBe("(-3 * sqrt(3)) / sqrt(3)", "-3");
		rationalizationShouldBe("(3 * sqrt(3)) / -sqrt(3)", "-3");
		rationalizationShouldBe("(-3 * sqrt(3)) / -sqrt(3)", "3");

	}

	@Test
	public void testOutputAsLatex() {
		rationalizationShouldBe("2 / sqrt(2)",
				"\\sqrt{2}", StringTemplate.latexTemplate);

		rationalizationShouldBe("sqrt(3) / sqrt(2)",
				"\\frac{\\sqrt{6}}{2}", StringTemplate.latexTemplate);

	}

	private void rationalizationShouldBe(String definition, String expected) {
		rationalizationShouldBe(definition, expected, StringTemplate.defaultTemplate);
	}

	private void rationalizationShouldBe(String definition, String expected, StringTemplate tpl) {
		GeoNumeric num = add(definition);
		num.setSymbolicMode(true, true);
		assertEquals(expected, num.getFormulaString(tpl, true));
	}

	@Test
	public void testSimplifySquareRoots() {
		rationalizationShouldBe("sqrt(3) / sqrt(4)", "sqrt(3) / 2");
		rationalizationShouldBe("sqrt(3) / sqrt(1)", "sqrt(3)");
	}

	@Test
	public void testCancelGCDs() {
		rationalizationShouldBe("2 / sqrt(2)", "sqrt(2)");
		rationalizationShouldBe("4 / (sqrt(5) - 1)", "sqrt(5) + 1");
//		rationalizationShouldBe("8 / (sqrt(5) - 1)", "2 * sqrt(5) + 1");
	}
}
