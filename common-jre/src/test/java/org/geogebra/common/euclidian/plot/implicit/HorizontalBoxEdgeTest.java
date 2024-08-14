package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial1Var;
import org.junit.Test;

public class HorizontalBoxEdgeTest extends BaseUnitTest {
	@Test
	public void name() {
		double []coeffs= {1, 1, 1};
		BernsteinPolynomial polynomial = new BernsteinPolynomial1Var(coeffs, 'y',
				-10, 10);
		BoxEdgeImpl edge = new BoxEdgeImpl(polynomial, -10, 10, 1);
		BoxEdgeImpl[] split = edge.split();
	}
}
