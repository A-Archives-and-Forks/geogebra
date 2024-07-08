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
	 * @return the value at the given parameter.
	 */
	double evaluate(double value);

	/**
	 * @return the derivative of the polynomial.
	 */
	BernsteinPolynomial derivative();

	/**
	 *
	 * @param variable to partial derivative with respect to.
	 * @return the partial derivative of the polynomial with respect to the given variable.
	 */
	BernsteinPolynomial derivative(String variable);

	/**
	 * Splits the polynomial into two.
	 * Todo describe more
	 *
	 * @return the two polynomials in array.
	 */
	BernsteinPolynomial[] split();
/**
	 * Splits the 2 variable polynomial into four, two by each variable
	 * Todo describe more
	 *
	 * @return the two polynomials in array.
	 */
	BernsteinPolynomial[][] split2D();

	/**
	 * Multiply the Bernstein polynomial with a given value.
	 * @param value to multiply with.
	 * @return the new, multiplied polynomial.
	 */
	BernsteinPolynomial multiply(double value);

	/**
	 * Adds a given value to the Bernstein polynomial
	 * @param value to add.
	 * @return the result polynomial.
	 */
	BernsteinPolynomial plus(double value);

	/**
	 * Adds a Bernstein polynomial to this one.
	 * @param bernsteinPolynomial to add.
	 * @return the result polynomial.
	 */
	BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial);

	/**
	 * Subtracts a Bernstein polynomial from this one.
	 * @param bernsteinPolynomial to subtract.
	 * @return the result polynomial.
	 */
	BernsteinPolynomial minus(BernsteinPolynomial bernsteinPolynomial);

	/**
	 * Multiplies the Bernstein polynomial with (1 - x)
	 * Note: x stands for any name that was previously given to the variable.
	 *
	 * @return the result polynomial.
	 */
	BernsteinPolynomial multiplyByOneMinusX();

	/**
	 * Multiplies the Bernstein polynomial with x
	 * Note: x stands for any name that was previously given to the variable.
	 *
	 * @return the result polynomial.
	 */
	BernsteinPolynomial multiplyByX();

	/**
	 *
	 * @return if the polynomial is a simple constant number.
	 */
	boolean isConstant();

	/**
	 *
	 * @return the number of variables of Bernstein polynomial;
	 */
	int numberOfVariables();
}
