package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Polynomial;

public class ImplicitPolyToBernsteinConverter {

	private FunctionNVar function;

	public void convert(GeoImplicitCurve curve) {
		function = curve.getFunctionDefinition();
		Polynomial polynomial = function.getPolynomial();
	}
}
