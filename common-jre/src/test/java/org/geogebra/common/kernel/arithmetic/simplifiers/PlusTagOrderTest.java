package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.PlusTagOrder;
import org.junit.Test;

public class PlusTagOrderTest extends BaseSimplifyTest {

	@Override
	protected SimplifyNode getSimplifier() {
		return new PlusTagOrder(utils);
	}

	@Test
	public void testPlusOrder() {
		shouldSimplify("-10 + sqrt(2)", "sqrt(2) - 10");
	}
}
