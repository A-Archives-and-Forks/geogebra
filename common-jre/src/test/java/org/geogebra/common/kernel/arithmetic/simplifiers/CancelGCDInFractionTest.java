package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class CancelGCDInFractionTest extends BaseSimplifyTest{
	@Override
	protected SimplifyNode getSimplifier() {
		return new CancelGCDInFraction(getKernel());
	}

	@Test
	public void testCancelGCD() {
		shouldSimplify("2 / (2sqrt(3))", "1 / sqrt(3)");
		shouldSimplify("9(-8 - sqrt(10)) / 54", "((-8 - sqrt(10))) / 6");	}
}
