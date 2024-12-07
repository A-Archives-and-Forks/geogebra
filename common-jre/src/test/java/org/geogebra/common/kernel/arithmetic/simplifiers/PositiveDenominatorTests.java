package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class PositiveDenominatorTests extends BaseSimplifyTest{
	@Override
	protected SimplifyNode getSimplifier() {
		return new PositiveDenominator(utils);
	}

	@Test
	public void testApply() {
		shouldSimplify("(3+sqrt(2)) / -5", "-(3+sqrt(2)) / 5");
		shouldSimplify("-(3+sqrt(2)) / -5", "(3+sqrt(2)) / 5");
		shouldSimplify("7 (3+sqrt(2)) / -5", "-7 (3+sqrt(2)) / 5");
	}
}
