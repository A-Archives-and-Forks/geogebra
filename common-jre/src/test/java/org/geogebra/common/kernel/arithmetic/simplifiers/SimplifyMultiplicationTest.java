package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class SimplifyMultiplicationTest extends BaseSimplifyTest {
	@Override
	protected SimplifyNode getSimplifier() {
		return new SimplifyMultiplication(utils);
	}

	@Test
	public void testSimplify() {
		shouldSimplify("(1 + sqrt(2))(1 + sqrt(3))", "1 + sqrt(6) + sqrt(3) + sqrt(2)");
		shouldSimplify("(1 - sqrt(2))(1 + sqrt(3))", "1 + sqrt(3) - sqrt(2) - sqrt(6)");
		shouldSimplify("(1 - sqrt(2))(1 - sqrt(3))", "1 + sqrt(6) - sqrt(2) - sqrt(3)");
		shouldSimplify("(1 + sqrt(2))(1 - sqrt(3))", "1 + sqrt(2) - sqrt(3) - sqrt(6)");
	}

//	@Ignore
	@Test
	public void name() {
//		shouldSimplify("(2 + 3sqrt(2))(4 - 5sqrt(3))", "8 + 4 * 3sqrt(2) - 2 * 5sqrt(3) "
//				+ "- (5sqrt(3) * 3sqrt(2))");
		shouldSimplify("(-8 - sqrt(10))(sqrt(4) + 7)", "-72 - (2sqrt(10)) - 7sqrt(10)");
		shouldSimplify("(-8 - sqrt(10))(sqrt(4) + 7)", "9(-sqrt(10) - 8)");
	}
}
