package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.Kernel.MAX_PRECISION;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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
		BernsteinBasisPolynomial b00 = new BernsteinBasisPolynomial(degree, index, fv);
		assertEquals(value, b00.toString());
	}

	@Test
	public void testBasis1() {
		basisShouldBe("x", 1, 1);
		basisShouldBe("2x (1 - x)", 1, 2);
		basisShouldBe("3x (1 - x)\u00b2", 1, 3);
		basisShouldBe("4x (1 - x)\u00b3", 1, 4);
		basisShouldBe("5x (1 - x)\u2074", 1, 5);
	}

	@Test
	public void testBasis2() {
		basisShouldBe("x\u00b2", 2, 2);
		basisShouldBe("3x\u00b2 (1 - x)", 2, 3);
		basisShouldBe("6x\u00b2 (1 - x)\u00b2", 2, 4);
		basisShouldBe("10x\u00b2 (1 - x)\u00b3", 2, 5);
	}

	@Test
	public void testBasis3() {
		basisShouldBe("x\u00b3", 3, 3);
		basisShouldBe("4x\u00b3 (1 - x)", 3, 4);
		basisShouldBe("10x\u00b3 (1 - x)\u00b2", 3, 5);
	}

	@Test
	public void testBasis4() {
		basisShouldBe("x\u2074", 4, 4);
		basisShouldBe("5x\u2074 (1 - x)", 4, 5);
	}

	@Test
	public void testBasis5() {
		basisShouldBe("x\u2075", 5, 5);
	}

	@Test
	public void testSumOfBases() {
		sumOfBasesShouldBeOne(3);
		sumOfBasesShouldBeOne(4);
		sumOfBasesShouldBeOne(5);
		sumOfBasesShouldBeOne(6);
		sumOfBasesShouldBeOne(10);
		sumOfBasesShouldBeOne(99);
	}

	private void sumOfBasesShouldBeOne(int degree) {
		List<BernsteinBasisPolynomial> bases = makeBasis(degree);
		double step = 1.0 / (degree * 10);
		for (double value = 0; value < 1; value += step) {
			assertEquals(1.0, sumOf(bases, value), MAX_PRECISION);
		}
	}

	private double sumOf(List<BernsteinBasisPolynomial> bases, double value) {
		double result = 0;
		for (BernsteinBasisPolynomial basis : bases) {
			result += basis.evaluate(value);
		}
		return result;
	}

	private List<BernsteinBasisPolynomial> makeBasis(int degree) {
		List<BernsteinBasisPolynomial> list = new ArrayList<>();
		for (int i = 0; i <= degree; i++) {
			list.add(new BernsteinBasisPolynomial(degree, i, fv));
		}

		return list;
	}
}
