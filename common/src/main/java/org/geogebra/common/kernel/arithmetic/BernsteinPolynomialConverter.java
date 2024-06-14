package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

public class BernsteinPolynomialConverter {

	BernsteinPolynomial fromImplicitCurve(GeoImplicitCurve curve, double min, double max) {
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		Polynomial polynomial = functionNVar.getPolynomial();
		return fromPolynomial(polynomial, curve.getDegX(), curve.getDegY(), min, max);
	}

	BernsteinPolynomial fromPolynomial(Polynomial polynomial, int degreeX, int degreeY, double min,
			double max) {
		return degreeY == 0
				? new BernsteinPolynomial1Var(polynomial, 'x', min, max,
				degreeX)
				: null;
//		new BernsteinPolynomial2Var(curve.getKernel(), polynomial,
//				functionNVar.getFunctionVariables(), min, max,
//				curve.getDegX(), curve.getDegY());
	}

}
