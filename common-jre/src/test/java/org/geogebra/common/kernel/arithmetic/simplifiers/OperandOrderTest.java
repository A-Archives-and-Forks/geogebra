package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.OperandOrder;
import org.junit.Test;

public class OperandOrderTest extends BaseSimplifyTest {
	@Override
	protected SimplifyNode getSimplifier() {
		return new OperandOrder(utils);
	}

	@Test
	public void name() {
		shouldSimplify("-1 + sqrt(2)", "sqrt(2) - 1");
		shouldSimplify("(-1 + sqrt(2)) / 2", "(sqrt(2) - 1) / 2");
	}
}
