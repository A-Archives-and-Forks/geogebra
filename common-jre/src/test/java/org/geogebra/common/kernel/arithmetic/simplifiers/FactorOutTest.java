package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Ignore;
import org.junit.Test;

public class FactorOutTest extends BaseSimplifyTest {

	@Override
	protected SimplifyNode getSimplifier() {
		return new FactorOut(utils);
	}

	@Test
	public void testFactorAdditionOfPositives() {
		shouldSimplify("2 + 2sqrt(2)", "2 (1 + sqrt(2))");
		shouldSimplify("2sqrt(2) + 2", "2 (sqrt(2) + 1)");

		shouldSimplify("2 + 6sqrt(2)", "2 (1 + 3sqrt(2))");
		shouldSimplify("6sqrt(2) + 2", "2 (3sqrt(2) + 1)");
	}

	@Test
	public void testFactorSubtractionOfPositives() {
		shouldSimplify("2 - 2sqrt(2)", "2 (1 - sqrt(2))");
		shouldSimplify("2sqrt(2) - 2", "2 (sqrt(2) - 1)");

		shouldSimplify("12 - 4sqrt(2)", "4 (3 - sqrt(2))");
		shouldSimplify("4sqrt(2) - 12", "4 (sqrt(2) - 3)");
	}

	@Test
	public void testFactorSubtractionOfNegatives() {
		shouldSimplify("(-3 - sqrt(2))", "-(3 + sqrt(2))");
		shouldSimplify("(-sqrt(2) - 3)", "-(sqrt(2) + 3)");

		shouldSimplify("(-2 - 2sqrt(2))", "-2 (1 + sqrt(2))");
		shouldSimplify("(-2sqrt(2) - 2)", "-2 (sqrt(2) + 1)");
	}

	@Ignore
	@Test
	public void testNoChange() {
		shouldNotChange("(3 - sqrt(2)) / 7");
		shouldNotChange("(-sqrt(2) + 3) / 7");
		shouldNotChange("3 + sqrt(2)");
		shouldNotChange("sqrt(2) + 2");
		shouldNotChange("2sqrt(2) + 2");
	}

	@Test
	public void testFactorOutNominator() {
		shouldSimplify("(-2 - 2sqrt(2)) / 4", "(-2 (1 + sqrt(2))) / 4");
		shouldSimplify("(2 - 2sqrt(2)) / 4", "(2 (1 - sqrt(2))) / 4");
	}
}
