package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

public class BernsteinPolynomialConverter {

	private final BernsteinBuilder1Var builder1Var = new BernsteinBuilder1Var();
	private final BernsteinBuilder2Var builder2Var;

	public BernsteinPolynomialConverter() {
		builder2Var = new BernsteinBuilder2Var(builder1Var);
	}

	BernsteinPolynomial fromImplicitCurve(GeoImplicitCurve curve, double min, double max) {
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		return fromFunctionNVar(functionNVar, min, max);
	}

	BernsteinPolynomial fromFunctionNVar(FunctionNVar functionNVar,
			double min, double max) {
		Polynomial polynomial = functionNVar.getPolynomial();
		return fromPolynomial(polynomial, polynomial.degree('x'),
				polynomial.degree('y'), min, max);
	}

	BernsteinPolynomial fromPolynomial(Polynomial polynomial, int degreeX, int degreeY, double min,
			double max) {
		if (degreeX !=0 && degreeY != 0) {
			return builder2Var.build(polynomial, degreeX, degreeY, min, max);
		}

		if (degreeY == 0) {
			return builder1Var.build(coeffsFromPolynomial(polynomial, degreeX, 'x'),
					degreeX, 'x', min, max);
		}

		return builder1Var.build(coeffsFromPolynomial(polynomial, degreeY, 'y'),
				degreeY, 'y', min, max);
	}

	private double[] coeffsFromPolynomial(Polynomial polynomial, int degree, char variableName) {
		double[] coeffs = new double[degree + 1];
		for (int i = 0; i <= degree; i++) {
			Term term = i < polynomial.length() ? polynomial.getTerm(i) : null;
			if (term != null) {
				int power = term.degree(variableName);
				coeffs[power] = term.coefficient.evaluateDouble();
			}
		}
		return coeffs;
	}
}