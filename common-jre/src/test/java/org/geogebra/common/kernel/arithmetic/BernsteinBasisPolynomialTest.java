package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class BernsteinBasisPolynomialTest extends BaseUnitTest {

	private FunctionVariable fv;

	@Before
	public void setUp() throws Exception {
		fv = new FunctionVariable(getKernel());
	}

	@Test
	public void testBasis0() {
		basisShouldBe("1", 0, 0);
		basisShouldBe("1 - x", 0, 1);
		basisShouldBe("(1 - x)\u00b2", 0, 2);
		basisShouldBe("(1 - x)\u00b3", 0, 3);
		basisShouldBe("(1 - x)\u2074", 0, 4);
		basisShouldBe("(1 - x)\u2075", 0, 5);
	}

	private void basisShouldBe(String value, int index, int degree) {
		BernsteinBasisPolynomial b00 = new BernsteinBasisPolynomial(index, degree, fv);
		assertEquals(value, b00.toString());
	}
}
