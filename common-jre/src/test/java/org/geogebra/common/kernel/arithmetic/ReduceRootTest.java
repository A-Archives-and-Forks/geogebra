package org.geogebra.common.kernel.arithmetic;

import org.junit.Test;

public class ReduceRootTest extends BaseSimplifyTest {
	@Test
	public void testReducedRoots() {
		shouldReduce("sqrt(3 + 4)", "sqrt(7)");
		shouldReduce("sqrt(72)", "6 sqrt(2)");
		shouldReduce("sqrt(40 + 4*8)", "6 sqrt(2)");
		shouldReduce("2 + sqrt(3 + 4)", "2 + sqrt(7)");
		shouldReduce("2 * sqrt(3 + 4)", "2 * sqrt(7)");
		shouldReduce("14 + 2 * sqrt(3 + 4)", "14 + 2 * sqrt(7)");
	}

	private void shouldReduce(String actualDef, String expectedDef) {
		shouldSimplify(actualDef, expectedDef, new ReduceRoot(getKernel()));
	}

}
