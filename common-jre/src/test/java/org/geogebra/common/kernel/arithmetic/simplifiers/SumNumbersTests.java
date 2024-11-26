package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class SumNumbersTests extends BaseSimplifyTest{
	@Override
	protected SimplifyNode getSimplifier() {
		return new SumNumbers(getKernel());
	}

	@Test
	public void testSingleSums() {
		shouldSimplify("3+2", "5");
		shouldSimplify("3+sqrt(4)", "5");
		shouldSimplify("3-2", "1");
		shouldSimplify("3-2+6-4", "3");
		shouldSimplify("3-2+6-4", "3");
	}

	@Test
	public void testMixedSums() {
		shouldSimplify("3+2+2 +2sqrt(6)",  "7 + 2sqrt(6) ");
		shouldSimplify("3+2 + 2sqrt(6) + 2sqrt(2)",  "5 + 2sqrt(6) + 2sqrt(2)");

	}
}
