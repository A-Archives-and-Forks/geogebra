package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Polynomial;

public class ImplicitPolyToBernsteinConverter {

	private Polynomial polynomial;

	public void convert(GeoImplicitCurve curve) {
		FunctionNVar function = curve.getFunctionDefinition();
		polynomial = function.getPolynomial();
		int a=1;
	}
}
