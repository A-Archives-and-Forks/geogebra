package org.geogebra.common.kernel.arithmetic;

import org.junit.Test;

public class SimplifyMultiplicationTest extends BaseSimplifyTest {
	@Test
	public void testSimplify() {
		shouldSimplify("(1 + sqrt(2))(1 + sqrt(3))", "1 + sqrt(3) + sqrt(6) + sqrt(2)");
		shouldSimplify("(1 + sqrt(2))(1 - sqrt(3))", "1 - sqrt(3) - sqrt(6) + sqrt(2)");
		shouldSimplify("(1 - sqrt(2))(1 + sqrt(3))", "1 + sqrt(3) - sqrt(6) - sqrt(2)");
		shouldSimplify("(1 - sqrt(2))(1 - sqrt(3))", "1 - sqrt(3) + sqrt(6) - sqrt(2)");
		shouldSimplify("(-1 - sqrt(2))(1 - sqrt(3))", "-1 + sqrt(3) + sqrt(6) - sqrt(2)");
		shouldSimplify("(-1 - sqrt(2))(-1 - sqrt(3))", "1 + sqrt(3) + sqrt(6) + sqrt(2)");
		shouldSimplify("(-1 + sqrt(2))(-1 - sqrt(3))",  "1 + sqrt(3) - sqrt(6) - sqrt(2)");
		shouldSimplify("(-1 + sqrt(2))(-1 + sqrt(3))",  "1 - sqrt(3) + sqrt(6) - sqrt(2)");
	}

	@Test
	public void name() {
		shouldSimplify("(-8 - sqrt(10))(7 + sqrt(4))", "9(-sqrt(10) - 8)");
	}

	private void shouldSimplify(String actual, String expected) {
		shouldSimplify(actual, expected, new SimplifyMultiplication(getKernel()));
	}
}
