package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.RationalizeFractionAlgo.checkDecimals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.debug.Log;
import org.junit.Test;

public class RationalizableFractionTest extends BaseUnitTest {
	@Test
	public void testSupported() {
		shouldBeSupported("1 / sqrt(2)");
		shouldBeSupported("1 / (sqrt(2) + 1)");
		shouldBeSupported("1 / (1 - sqrt(2))");
		shouldBeSupported("sqrt(3) / sqrt(2)");
		shouldBeSupported("(sqrt(3) + 1) / sqrt(2)");
		shouldBeSupported("(3 (sqrt(3) + 1)) / sqrt(2)");
		shouldBeSupported("(sqrt(3) + 1) / (sqrt(2) - 1)");
	}

	private void shouldBeSupported(String definition) {
		GeoElementND geo = add(definition);
		ExpressionNode resolution = RationalizableFraction.getResolution(geo.getDefinition());
		assertNotNull(resolution);
	}

	@Test
	public void testUnsupported() {
		shouldBeUnsupported("1 / 2");
		shouldBeUnsupported("1 / (sqrt(2) + sqrt(3))");
		shouldBeUnsupported("(sqrt(3) + sqrt(2) + 1) / sqrt(2)");
		shouldBeUnsupported("(sqrt(3) + sqrt(2)) / sqrt(2)");
		shouldBeUnsupported("1 / sqrt(2.5)");
		shouldBeUnsupported("sqrt(2.5) / sqrt(2.5)");
		shouldBeUnsupported("sqrt(1 / 4)");
		shouldBeUnsupported("sqrt(1 / 4) / sqrt(2)");
		shouldBeUnsupported("(sqrt(2.5) + 1) / sqrt(2.5)");
		shouldBeUnsupported("sqrt(2.5) / (sqrt(2.5) + 1)");
		shouldBeUnsupported("1 / (sqrt(4) + 1.5)");
		shouldBeUnsupported("1 / (1.5 + sqrt(4))");
		shouldBeUnsupported("1 / sqrt(-2)");
		shouldBeUnsupported("2.3 / sqrt(2)");
		shouldBeUnsupported("((1 / 2) (sqrt(3) + 1)) / sqrt(2)");
		shouldBeUnsupported("(3.2 (sqrt(3) + 1)) / sqrt(2)");
		shouldBeUnsupported("((4+sqrt(10+0.0001))/(-2+sqrt(0.0001+10)))");
	}


	private void shouldBeUnsupported(String definition) {
		GeoElementND geo = add(definition);
		ExpressionNode resolution = RationalizableFraction.getResolution(geo.getDefinition());
		assertNull(resolution);
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
		rationalizationShouldBe("1 / (1 + sqrt(2))", "sqrt(2) - 1");
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
	public void testFractionIsInteger() {
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
		rationalizationShouldBe("-sqrt(3) / sqrt(3)", "-1");
		rationalizationShouldBe("(3 * sqrt(3)) / sqrt(3)", "3");
		rationalizationShouldBe("(-3 * sqrt(3)) / sqrt(3)", "-3");
		rationalizationShouldBe("(3 * sqrt(3)) / -sqrt(3)", "-3");
		rationalizationShouldBe("(-3 * sqrt(3)) / -sqrt(3)", "3");
		rationalizationShouldBe("(-3 (sqrt(3) + 2)) / (sqrt(3) + 2)", "-3");
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
		ExpressionNode resolution = RationalizableFraction.getResolution(num.getDefinition());
		assertNotNull("resolution is null, " + definition + " is not supported", resolution);
		assertEquals(expected, resolution.toString(tpl));
	}

	@Test
	public void testSimplifySquareRoots() {
		rationalizationShouldBe("sqrt(3) / sqrt(4)", "sqrt(3) / 2");
		rationalizationShouldBe("sqrt(3) / sqrt(1)", "sqrt(3)");
		rationalizationShouldBe("sqrt(6) / sqrt(2)", "sqrt(3)");
		rationalizationShouldBe("3 / sqrt(8)", "(3sqrt(2)) / 4");
		rationalizationShouldBe("sqrt(2 + 3) / (1 + sqrt(2))",
				"(-1 + sqrt(2)) sqrt(5)");
	}

	@Test
	public void testDecimalsShouldDiscardResults() {
		shouldBeUnsupported("sqrt(2 + 3) / (1 + sqrt(2 + 5))");
	}

	@Test
	public void testCancelGCDs() {
		rationalizationShouldBe("2 / sqrt(2)", "sqrt(2)");
		rationalizationShouldBe("4 / (sqrt(5) - 1)", "sqrt(5) + 1");
		rationalizationShouldBe("8 / (sqrt(5) - 1)", "2 (sqrt(5) + 1)");
	}

	@Test
	public void testProductInDenominator() {
		rationalizationShouldBe("1 / (2 * (1 + sqrt(2)))", "(sqrt(2) - 1) / 2");
		rationalizationShouldBe("1 / (2 * (1 - sqrt(2)))", "(sqrt(2) + 1) / 2");
		rationalizationShouldBe("1 / (2 * sqrt(2))", "sqrt(2) / 4");
	}

	@Test
	public void testEvaluateUnderSquareRoot() {
		rationalizationShouldBe("1 / sqrt(3 + 4)", "sqrt(7) / 7");
	}

	@Test
	public void testCheckDecimals() {
		shouldPassToDecimalTest("sqrt(2) / 4");
		shouldPassToDecimalTest("1 + sqrt(2)");
		shouldPassToDecimalTest("(2 (sqrt(5) + 1))");
		shouldNotPassToDecimalTest("1 + sqrt(2.5)");
		shouldNotPassToDecimalTest("16.5 * 2 sqrt(5)");
	}

	private void shouldNotPassToDecimalTest(String command) {
		assertTrue(isPassDecimal(command));
	}

	private void shouldPassToDecimalTest(String command) {
		assertFalse(isPassDecimal(command));
	}

	private boolean isPassDecimal(String command) {
		GeoNumeric numeric = add(command);
		numeric.setSymbolicMode(true, true);
		return checkDecimals(numeric.getDefinition());
	}

	@Test
	public void test() {
		rationalizationShouldBe("(-2 + sqrt(7)) / (-9 + sqrt(4))", "(2 - sqrt(7)) / 7");
		rationalizationShouldBe("(-2 + sqrt(3+4)) / (-9 + sqrt(4))",
				"(2 - sqrt(7)) / 7");
		rationalizationShouldBe("(-10 + sqrt(6)) / (5 + sqrt(1))",
				"(-10 + sqrt(6)) / 6");
//		rationalizationShouldBe("(-10 + sqrt(5)) / (-2 + sqrt(5))",
//				"(-8sqrt(5) - 15");
////		rationalizationShouldBe("(7 + sqrt(8)) / (4 + sqrt(8))",
//				"(-3 sqrt(2)) / 4");
//		rationalizationShouldBe(genericSqrtFraction(7, 4, -8, 10),
//				"");
//		rationalizationShouldBe(genericSqrtFraction(-8, 4, -2, 8),
//				"");
	}

	@Test
	public void name() {
//		rationalizationShouldBe(genericSqrtFraction(-10, 5, -2, 5),
//				"-8sqrt(5) - 15");
//		rationalizationShouldBe("(-8 + sqrt(4)) / (-2 + sqrt(8))",
//				"-3(sqrt(2) + 1)");

	}

	private String genericSqrtFraction(int a, int b, int c, int d) {
		String s = "(" + a + " + sqrt(" + b + ")) / " + "(" + c + " + sqrt(" + d + "))";
		Log.debug(s);
		return s;
	}
}