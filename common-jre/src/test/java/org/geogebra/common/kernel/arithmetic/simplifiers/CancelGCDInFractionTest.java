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
		shouldSimplify("9(-8 - sqrt(10)) / 54", "((-8 - sqrt(10))) / 6");
		shouldSimplify("2 (-1 + sqrt(2)) / 4", "(-1 + sqrt(2)) / 2");
	}

	@Test
	public void name() {
		shouldSimplify("((sqrt(5) + 1) * 4) / 4", "sqrt(5) + 1");
		shouldSimplify("((-2 - sqrt(8)) (-6)) / -4", "3(-sqrt(2) - 1)");
	}

	@Test
	public void shouldNotChange() {
		shouldSimplify("(-(sqrt(2) - 3)) / 7", "(-(sqrt(2) - 3)) / 7");
	}
}
