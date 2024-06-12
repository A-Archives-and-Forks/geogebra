package org.geogebra.common.kernel.arithmetic;

public interface BernsteinPolynomial {
	double evaluate(double value);
	String coeffsToString();

	ExpressionNode output();

	BernsteinPolynomial derivative();
}
