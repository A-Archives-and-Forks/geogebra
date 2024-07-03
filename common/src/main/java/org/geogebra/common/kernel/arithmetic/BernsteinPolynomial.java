package org.geogebra.common.kernel.arithmetic;

public interface BernsteinPolynomial {
	double evaluate(double value);
	BernsteinPolynomial derivative();
	BernsteinPolynomial derivative(String variable);
	BernsteinPolynomial[] split();

	BernsteinPolynomial multiply(double value);
	BernsteinPolynomial plus(double value);

	BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial);

	BernsteinPolynomial minus(BernsteinPolynomial bernsteinPolynomial);
	BernsteinPolynomial shiftLeft();
	BernsteinPolynomial shiftRight();
	boolean isConstant();
}
