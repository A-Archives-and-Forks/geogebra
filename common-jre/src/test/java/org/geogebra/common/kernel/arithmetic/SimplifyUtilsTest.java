package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.arithmetic.simplifiers.BaseSimplifyTest;
import org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyNode;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Before;
import org.junit.Test;

public class SimplifyUtilsTest extends BaseSimplifyTest {

	private SimplifyUtils utils;

	@Override
	protected SimplifyNode getSimplifier() {
		return null;
	}

	@Before
	public void setUp() throws Exception {
		utils = new SimplifyUtils(getKernel());
	}

	@Test
	public void testExpand() {
		shouldExpand("2 (1 + sqrt(4))", "6");
		shouldExpand("(1 + sqrt(4) - 1) 2", "4");
		shouldExpand("2 (1 + sqrt(2))", "2 + 2sqrt(2)");
		shouldExpand("(1 + sqrt(2)) 2", "2 + 2sqrt(2)");
	}

	private void shouldExpand(String from, String to) {
		GeoNumeric original = newSymbolicNumeric(from);
		GeoNumeric expected = newSymbolicNumeric(to);
		ExpressionNode actual = utils.expand(original.getDefinition());
		shouldSerialize(expected.getDefinition(), actual);
	}

	@Test
	public void testMultiplyByMinusOne() {
		mulShouldBe("sqrt(2) + 1", "-sqrt(2) - 1");
		mulShouldBe("sqrt(2) - 1", "-sqrt(2) + 1");
		mulShouldBe("1 + sqrt(2)", "-1 - sqrt(2)");
		mulShouldBe("1 - sqrt(2)", "-1 + sqrt(2)");

	}

	private void mulShouldBe(String from, String to) {
		GeoNumeric original = newSymbolicNumeric(from);
		GeoNumeric expected = newSymbolicNumeric(to);
		ExpressionValue actual = utils.mulByMinusOne(original.getDefinition());
		shouldSerialize(expected.getDefinition(), actual.wrap());
	}

	@Test
	public void testSqrtPositiveInteger() {
		sqrtShouldNotBeValid("sqrt(0.0001+10)");
		sqrtShouldNotBeValid("sqrt(1 / 4)");
		sqrtShouldNotBeValid("sqrt(2.5)");
		sqrtShouldBeValid("sqrt(1+2+3+4)");
		sqrtShouldBeValid("sqrt(4*0.25)");
	}

	private void sqrtShouldBeValid(String def) {
		GeoNumeric original = newSymbolicNumeric(def);
		assertTrue(def + " is not square root of a positive integer.",
				SimplifyUtils.isSquareRootValidInteger(original.getDefinition()));
	}

	private void sqrtShouldNotBeValid(String def) {
		GeoNumeric original = newSymbolicNumeric(def);
		assertFalse(def + " is square root of a positive integer.",
				SimplifyUtils.isSquareRootValidInteger(original.getDefinition()));
	}

	@Test
	public void testNodeSupported() {
		shouldSupportNode("1");
		shouldSupportNode("sqrt(2)");
		shouldSupportNode("sqrt(2) + 1");
		shouldSupportNode("1 + sqrt(2)");
		shouldSupportNode("sqrt(2) - 1");
		shouldSupportNode("1 - sqrt(2)");
		shouldNotSupportNode("sqrt(-2)");
	}

	@Test
	public void name() {
		shouldNotSupportNode("sqrt(-2)");
	}

	private void shouldSupportNode(String def) {
		GeoNumeric numeric = newSymbolicNumeric(def);
		assertTrue(def + " is not supported",
				SimplifyUtils.isNodeSupported(numeric.getDefinition()));
	}

	private void shouldNotSupportNode(String def) {
		GeoNumeric numeric = newSymbolicNumeric(def);
		assertFalse(def + " should not be supported",
				SimplifyUtils.isNodeSupported(numeric.getDefinition()));
	}

	@Test
	public void testNegative() {
		negativeShouldBe("-3 - sqrt(2)", "-(3 + sqrt(2))");
		negativeShouldBe("-sqrt(2) - 3", "-(sqrt(2) + 3)");
	}

	@Test
	public void fixMe() {
		mulShouldBe("1 + sqrt(2)", "-1 - sqrt(2)");

	}

	private void negativeShouldBe(String from, String to) {
		GeoNumeric original = newSymbolicNumeric(from);
		GeoNumeric expected = newSymbolicNumeric(to);
		ExpressionValue actual = utils.negative(original.getDefinition());
		shouldSerialize(expected.getDefinition(), actual.wrap());
	}
}
