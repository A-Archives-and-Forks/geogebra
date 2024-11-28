package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.TidyNumbers;
import org.junit.Test;

public class TidyNumbersTest extends BaseSimplifyTest {
	@Override
	protected SimplifyNode getSimplifier() {
		return new TidyNumbers(getKernel());
	}

	@Test
	public void testTidyNumbers() {
		shouldSimplify("sqrt(4)sqrt(2)", "2sqrt(2)");
		shouldSimplify("(-(-2) + sqrt(5)) (-10 + sqrt(5))", "(2 + sqrt(5)) (-10 + sqrt(5))");
		shouldSimplify("((-2 - sqrt(8)) (-6)) / -4", "((-2 - sqrt(8)) (-6)) / -4");
	}

	@Test
	public void testShouldNotChange() {
		shouldSimplify("(-(2sqrt(2)) - 2) / 4", "(-(2sqrt(2)) - 2) / 4");
		shouldSimplify("(-1 + sqrt(2)) sqrt(5)", "(-1 + sqrt(2)) sqrt(5)");
	}
}
