package org.geogebra.common.kernel.arithmetic;

/**
 * Interface representing Bernstein polynomials and its operations.
 *
 */
public interface BernsteinPolynomial {
	/**
	 * Evaluates the polynomial.
	 *
	 * @param value to evaluate at.
	 * @return the value at given point.
	 */
	double evaluate(double value);

	/**
	 * @return the derivative of the polynomial.
	 */
	BernsteinPolynomial derivative();

	/**
	 *
	 * @param variable to partial derivative in.
	 * @return the partial derivative of the polynomial in .
	 */
	BernsteinPolynomial derivative(String variable);
	BernsteinPolynomial[] split();

	BernsteinPolynomial multiply(double value);
	BernsteinPolynomial plus(double value);

	BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial);

	BernsteinPolynomial minus(BernsteinPolynomial bernsteinPolynomial);
	BernsteinPolynomial multiplyByOneMinusX();
	BernsteinPolynomial multiplyByX();
	boolean isConstant();
}
