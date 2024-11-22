package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class FactorizeTagsTest extends BaseSimplifyTest {

	@Override
	protected SimplifyNode getSimplifier() {
		return new FactorizeTags(getKernel());
	}

	@Test
	public void testIsAccepted() {
		shouldNotAccept("2 / 3");
	}

	@Test
	public void testFactorizeTags() {
	}
}
