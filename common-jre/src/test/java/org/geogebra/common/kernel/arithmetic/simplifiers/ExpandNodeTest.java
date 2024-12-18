package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class ExpandNodeTest extends BaseSimplifyTest {
	@Override
	protected SimplifyNode getSimplifier() {
		return new ExpandNode(utils);
	}

	@Test
	public void testSimplify() {
		shouldSimplify("(1 + sqrt(2))(1 + sqrt(3))", "1 + sqrt(6) + sqrt(3) + sqrt(2)");
		shouldSimplify("(1 - sqrt(2))(1 + sqrt(3))", "1 + sqrt(3) - sqrt(2) - sqrt(6)");
		shouldSimplify("(1 - sqrt(2))(1 - sqrt(3))", "1 + sqrt(6) - sqrt(2) - sqrt(3)");
		shouldSimplify("(1 + sqrt(2))(1 - sqrt(3))", "1 + sqrt(2) - sqrt(3) - sqrt(6)");
	}

	@Test
	public void withCompleteSquare() {
		shouldSimplify("(-8 - sqrt(10))(sqrt(4) + 7)", "-72 - (2sqrt(10)) - 7sqrt(10)");
	}
}
