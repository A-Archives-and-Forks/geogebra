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
	public void test1VarBasisNIndex0() {
		basisShouldBe("1", 0, 0);
		basisShouldBe("1 - x", 1, 0);
		basisShouldBe("(1 - x)\u00b2", 2, 0);
		basisShouldBe("(1 - x)\u00b3", 3, 0);
		basisShouldBe("(1 - x)\u2074", 4, 0);
		basisShouldBe("(1 - x)\u2075", 5, 0);
	}

	@Test
	public void test1VarBasisNIndex1() {
		basisShouldBe("x", 1, 1);
		basisShouldBe("x (1 - x)", 2, 1);
		basisShouldBe("x (1 - x)\u00b2", 3, 1);
		basisShouldBe("x (1 - x)\u00b3", 4, 1);
		basisShouldBe("x (1 - x)\u2074", 5, 1);
	}

	@Test
	public void test1VarBasisNIndex2() {
		basisShouldBe("x\u00b2", 2, 2);
		basisShouldBe("x\u00b2 (1 - x)", 3, 2);
		basisShouldBe("x\u00b2 (1 - x)\u00b2", 4, 2);
		basisShouldBe("x\u00b2 (1 - x)\u00b3", 5, 2);
	}

	@Test
	public void test1VarBasisNIndex3() {
		basisShouldBe("x\u00b3", 3, 3);
		basisShouldBe("x\u00b3 (1 - x)", 4, 3);
		basisShouldBe("x\u00b3 (1 - x)\u00b2", 5, 3);
	}

	@Test
	public void test1VarBasisNIndex4() {
		basisShouldBe("x\u2074", 4, 4);
		basisShouldBe("x\u2074 (1 - x)", 5, 4);
	}

	@Test
	public void test1VarBasisNIndex5() {
		basisShouldBe("x\u00b3", 3, 3);
		basisShouldBe("x\u00b3 (1 - x)", 4, 3);
		basisShouldBe("x\u00b3 (1 - x)\u00b2", 5, 3);
	}

	private void basisShouldBe(String value, int degree, int index) {
		BernsteinBasisPolynomial b00 = new BernsteinBasisPolynomial(fv, degree, index);
		assertEquals(value, b00.toString());
	}

}
