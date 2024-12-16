package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class CancelGCDInFractionTest extends BaseSimplifyTest{
	@Override
	protected SimplifyNode getSimplifier() {
		return new CancelGCDInFraction(utils);
	}

	@Test
	public void testCancelGCD() {
		shouldSimplify("2 / (2sqrt(3))", "1 / sqrt(3)");
		shouldSimplify("9(-8 - sqrt(10)) / 54" , "((-8 - sqrt(10))) / 6");
		shouldSimplify("2 (-1 + sqrt(2)) / 4", "(-1 + sqrt(2)) / 2");
	}

	@Test
	public void testAccept() {
		shouldAccept("(-8 - sqrt(10)) / 54");
		shouldAccept("(9(-8 - sqrt(10))) / 54");
		shouldAccept("9(-8 - sqrt(10)) / 54");
		shouldAccept("(12 (1 + sqrt(2))) / 4");
		shouldAccept("(-(2 (1 - sqrt(2)))) / 4");
	}

	@Test
	public void testCancel2() {
		shouldSimplify("(12 (1 + sqrt(2))) / 4" , "3 (1 + sqrt(2))");
		shouldSimplify("(-(2 (1 - sqrt(2)))) / 4", "(-1 + sqrt(2)) / 2");
		shouldSimplify("(-9 (8 + sqrt(10))) / 54", "(-8 - sqrt(10)) / 6");
	}

	@Test
	public void name() {
		shouldSimplify("(3 (1 - sqrt(6))) / -5", "(-(3 (1 - sqrt(6)))) / 5");
		shouldSimplify("(4 (sqrt(5) + 1)) / 4", "sqrt(5) + 1");
		shouldSimplify("((sqrt(5) + 1) * 4) / 4", "sqrt(5) + 1");
	}

	@Test
	public void wip() {
	}

	@Test
	public void shouldNotChange() {
		shouldSimplify("(3 (1 - sqrt(6))) / -5", "(3 (1 - sqrt(6))) / -5");
		shouldSimplify("(-(sqrt(2) - 3)) / 7", "(-(sqrt(2) - 3)) / 7");
		shouldSimplify("(-4 (5 - 2sqrt(2))) / 17", "(-4 (5 - 2sqrt(2))) / 17");
	}

	@Test
	public void testSimplifyConstantFractions() {
		shouldSimplify("12 / 8", " 3 / 2");
		shouldSimplify("-1 / 3", " -1 / 3");
		shouldSimplify("1 / -3", "1 / -3");
		shouldSimplify("12 / 3", "4");
	}

}
