package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

final class RationalizeFractionAlgo {
	private final Kernel kernel;
	private final ExpressionNode numerator;
	private final ExpressionNode denominator;

	public RationalizeFractionAlgo(Kernel kernel, ExpressionNode numerator,
			ExpressionNode denominator) {
		this.kernel = kernel;
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public ExpressionNode compute() {
		double numeratorValue = numerator.evaluateDouble();
		double denominatorValue = denominator.evaluateDouble();
		if (Math.abs(numeratorValue) == Math.abs(denominatorValue)) {
			double value = numeratorValue == denominatorValue ? 1 : -1;
			return (new MyDouble(kernel, value)).wrap();
		}

		if (numerator.isLeaf()) {
			return rationalizeAsLeafNumerator();
		}

		if (bothHaveSquareRoot(numerator, denominator)) {
			return rationalizeAsSquareRootProduct();
		}

		if (numerator.isOperation(Operation.SQRT) || canBeFactorized()) {
			return factorize();
		}

		return rationalizeAsLeafSqrtDenominator();
	}

	private boolean canBeFactorized() {
		return hasTwoTags(numerator) && hasTwoTags(denominator);
	}

	private static boolean hasTwoTags(ExpressionNode node) {
		// isSupported() guarantees that exactly one of the leaves is SQRT by now,
		// so no check is needed here.
		return node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS);
	}

	private ExpressionNode rationalizeAsLeafNumerator() {
		if (denominator.isOperation(Operation.SQRT)) {
			return new ExpressionNode(kernel,
					numerator.multiply(denominator),
					Operation.DIVIDE, denominator.getLeft());
		}

		try {
			return checkFactorization(Operation.MINUS);
		} catch (NoFactorization e) {
			//
		}

		try {
			return checkFactorization(Operation.PLUS);
		} catch (NoFactorization e) {
			//
		}

		return null;
	}

	private ExpressionNode checkFactorization(Operation op) throws NoFactorization {
		if (denominator.isOperation(op)) {
			ExpressionNode mul =
					new ExpressionNode(kernel, denominator.getLeft(), Operation.inverse(op),
							denominator.getRight());
			double v = denominator.multiply(mul).evaluateDouble();
			if (DoubleUtil.isEqual(v, 1, Kernel.STANDARD_PRECISION)) {
				return new ExpressionNode(kernel,
						numerator.multiplyR(mul));
			}

			if (DoubleUtil.isEqual(v, -1, Kernel.STANDARD_PRECISION)) {
				ExpressionNode invMul =
						new ExpressionNode(kernel, denominator.getLeft().wrap().multiplyR(-1),
								Operation.inverse(op),
								denominator.getRight().wrap().multiplyR(-1));
				return new ExpressionNode(kernel,
						numerator.multiply(invMul));
			}

			if (DoubleUtil.isInteger(v)) {
				return new ExpressionNode(kernel,
						numerator.multiply(mul),
						Operation.DIVIDE, new MyDouble(kernel, v));
			}
		}
		throw new NoFactorization();
	}

	private ExpressionNode simplifiedMultiply(ExpressionValue rationalized,
			ExpressionNode node1) {
		return rationalized.equals(node1.getLeft())
				? rationalized.wrap()
				: doMultiply(node1, denominator);
	}

	private ExpressionNode doMultiply(ExpressionNode left, ExpressionNode right) {
		if (bothHaveSquareRoot(left, right)) {
			return multiplySquareRoots(left, right);
		}

		return left.multiply(right);
	}

	private ExpressionNode factorize() {
		try {
			return checkFactorization(Operation.MINUS);
		} catch (NoFactorization e) {
			//
		}

		try {
			return checkFactorization(Operation.PLUS);
		} catch (NoFactorization e) {
			//
		}

		return null;
	}

	private ExpressionNode rationalizeAsSquareRootProduct() {
		return new ExpressionNode(kernel,
				multiplySquareRoots(numerator, denominator),
				Operation.DIVIDE,
				denominator.getLeft());
	}

	private static boolean bothHaveSquareRoot(ExpressionNode numerator, ExpressionNode denominator) {
		return numerator.isOperation(Operation.SQRT) && denominator.isOperation(Operation.SQRT);
	}

	private ExpressionNode multiplySquareRoots(ExpressionNode left, ExpressionNode right) {
		double product = left.getLeftTree().multiply(right.getLeft())
				.wrap().evaluateDouble();
		
		return new ExpressionNode(kernel, new MyDouble(kernel, product),
						Operation.SQRT, null);
	}

	private ExpressionNode rationalizeAsLeafSqrtDenominator() {
		ExpressionValue rationalized = denominator.getLeft();
		ExpressionNode numeratorLeft =
				simplifiedMultiply(rationalized, numerator.getLeftTree());
		ExpressionNode numeratorRight =
				simplifiedMultiply(rationalized, numerator.getRightTree());
		ExpressionNode newNumerator =
				new ExpressionNode(kernel, numeratorLeft, numerator.getOperation(),
						numeratorRight);
		return new ExpressionNode(kernel,
				newNumerator,
				Operation.DIVIDE, rationalized);
	}
}