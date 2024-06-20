package org.geogebra.common.kernel.arithmetic;

public interface BernsteinPolynomial {
	double evaluate(double value);
	BernsteinPolynomial derivative();

	BernsteinPolynomial multiply(double value);
	BernsteinPolynomial plus(double value);
	BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial);

	BernsteinPolynomial derivative(String variable);

	BernsteinPolynomial minus(BernsteinPolynomial bernsteinPolynomial);
}
