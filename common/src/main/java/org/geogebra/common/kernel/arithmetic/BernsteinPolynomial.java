package org.geogebra.common.kernel.arithmetic;

/**
 * Interface representing Bernstein polynomials and its operations.
 *
 */
public interface BernsteinPolynomial extends Splittable<BernsteinPolynomial> {

	/**
	 * Evaluates the polynomial.
	 *
	 * @param value to evaluate at.
	 * @return the value at the given parameter.
	 */
	double evaluate(double value);

	/**
	 * Evaluates the polynomial in two variables.
	 *
	 * @param x to evaluate at.
	 * @param y to evaluate at.
	 * @return the value at (x, y)
	 */
	double evaluate(double x, double y);

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
	@Override
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
	 * Divide the Bernstein polynomial with a given value.
	 * @param value to divide with.
	 * @return the new, divided polynomial.
	 */
	BernsteinPolynomial divide(double value);

	/**
	 * Adds a Bernstein polynomial to this one.
	 * @param bernsteinPolynomial to add.
	 * @return the result polynomial.
	 */
	BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial);

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

	/**
	 *
	 * @return true if the polynomial has no solution.
	 */
	boolean hasNoSolution();

	/**
	 *
	 * @return sign of the coefficients {+, - or mixed}.
	 */
	BinomialCoefficientsSign getSign();

	BernsteinPolynomial substitute(String variable, double value);

//	BernsteinPolynomial linearCombination(BernsteinPolynomial coeffs, )
}
