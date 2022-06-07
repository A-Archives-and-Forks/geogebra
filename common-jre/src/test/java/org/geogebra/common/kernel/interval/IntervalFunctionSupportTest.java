package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalFunction.isSupported;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class IntervalFunctionSupportTest extends BaseUnitTest {

	@Test
	public void testSupportOneVariableOnly() {
		assertFalse(isSupported(add("x + x")));
		assertFalse(isSupported(add("x^2 + x")));
		assertFalse(isSupported(add("abs(x)/x")));
		assertFalse(isSupported(add("(1/x)sin(x)")));
		assertFalse(isSupported(add("sin(x^4) + x")));
		assertFalse(isSupported(add("tan(x)/x")));
		assertFalse(isSupported(add("x+3x")));
	}

	@Test
	public void powerShouldBeNumber() {
		add("v = (1, 2)");
		assertFalse(isSupported(add("x^v")));
		assertFalse(isSupported(add("abs(x^v)")));
		add("A = (1, 2)");
		assertFalse(isSupported(add("x^A")));
	}

	@Test
	public void testSupportedOperations() {
		assertTrue(isSupported(add("x + 1")));
		assertTrue(isSupported(add("x - 1")));
		assertTrue(isSupported(add("x * 5")));
		assertTrue(isSupported(add("x / 5")));
		assertTrue(isSupported(add("x^3")));
		assertTrue(isSupported(add("nroot(x, 4)")));
		assertTrue(isSupported(add("sin(x)")));
		assertTrue(isSupported(add("cos(x)")));
		assertTrue(isSupported(add("sqrt(x)")));
		assertTrue(isSupported(add("tan(x)")));
		assertTrue(isSupported(add("exp(x)")));
		assertTrue(isSupported(add("log(x)")));
		assertTrue(isSupported(add("arccos(x)")));
		assertTrue(isSupported(add("arcsin(x)")));
		assertTrue(isSupported(add("arctan(x)")));
		assertTrue(isSupported(add("abs(x)")));
		assertTrue(isSupported(add("cosh(x)")));
		assertTrue(isSupported(add("sinh(x)")));
		assertTrue(isSupported(add("tanh(x)")));
		assertTrue(isSupported(add("log10(x)")));
		assertTrue(isSupported(add("log(x)")));
		assertTrue(isSupported(add("sin(x)^3")));
		assertTrue(isSupported(add("x * ((1, 1) * (1, 1))")));
		assertTrue(isSupported(add("sin(x)^(2/3)")));
		assertTrue(isSupported(add("sin(x)^2.141")));
		assertTrue(isSupported(add("x^(-2)")));
		assertTrue(isSupported(add("sin(e^x)")));
		assertTrue(isSupported(add("2^sin(x)")));
		assertTrue(isSupported(add("2^x")));
		assertTrue(isSupported(add("2^(1/x)")));
	}

	@Test
	public void testUnsupportedOperations() {
		assertFalse(isSupported(add("x!")));
		assertFalse(isSupported(add("gamma(x)")));
		assertFalse(isSupported(add("x^2x")));
		assertFalse(isSupported(add("(x * (1, 1)) * (1, 1)")));
		assertFalse(isSupported(add("acosh(x)")));
		assertFalse(isSupported(add("sin(x)^(ln(x))")));
	}

	@Test
	public void supportIf() {
		assertTrue(isSupported(add("If[x < 1, 0]")));
		assertTrue(isSupported(add("If[x < 1, 2x]")));
		assertTrue(isSupported(add("If[x < 1, x + 1]")));
		assertTrue(isSupported(add("If[sin(x) < 0, x + 1]")));
		assertFalse(isSupported(add("If[x < 1, 2x + x^3")));
	}

	@Test
	public void supportIfElse() {
		assertTrue(isSupported(add("If[x < 1, x, x + 1]")));
		assertTrue(isSupported(add("If[sin(x) < 0, x, x^2 + 1]")));
		assertFalse(isSupported(add("If[x < 1, x * sin(x), x + 1]")));
		assertFalse(isSupported(add("If[x < 1, x, x * sin(x)]")));
		assertFalse(isSupported(add("If[x < 1, x/tan(x), x * sin(x)]")));
		assertFalse(isSupported(add("If[x < 1, x * sin(x) + 1]")));
	}

	@Test
	public void supportIfList() {
		assertTrue(isSupported(add("if(x < -2, -2, x > 0, 4)")));
		assertTrue(isSupported(add("if(x < -2, x + 1, x > 0, x^4)")));
		assertTrue(isSupported(add("if(-4 < x < -2, 3x, 2 < x < 4, 4x)")));
		assertFalse(isSupported(add("if(x < -2, x * (ln(x)), x > 0, x^4)")));
	}
}
