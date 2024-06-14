package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

public class BernsteinPolynomialConverter {

	BernsteinPolynomial fromImplicitCurve(GeoImplicitCurve curve, double min, double max) {
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		Polynomial polynomial = functionNVar.getPolynomial();
		return curve.getDegY() == 0
				? new BernsteinPolynomial1Var(polynomial, 'x', min, max,
						curve.getDegX())
				: new BernsteinPolynomial2Var(curve.getKernel(), polynomial,
				functionNVar.getFunctionVariables(), min, max,
				curve.getDegX(), curve.getDegY());
	}

}
