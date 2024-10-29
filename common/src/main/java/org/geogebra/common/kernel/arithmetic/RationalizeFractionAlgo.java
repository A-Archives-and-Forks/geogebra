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
		ExpressionNode result = getReducedRoot(doRationalize(), kernel);
		return result.isOperation(Operation.DIVIDE) ? cancelGCDs(result, kernel) : result;
	}

	private static ExpressionNode cancelGCDs(ExpressionNode node, Kernel kernel) {
		ExpressionNode numerator = node.getLeftTree();
		ExpressionNode denominator = node.getRightTree();
		boolean numeratorLeaf = numerator.isLeaf();
		boolean denominatorLeaf = denominator.isLeaf();
		if (!numeratorLeaf && denominatorLeaf) {
			ExpressionValue canceled = denominator.getLeft();
			if (numerator.isOperation(Operation.MULTIPLY))  {
				double evalCanceled = canceled.evaluateDouble();
				double evalLeft = numerator.getLeft().evaluateDouble();
				double evalRight = numerator.getRight().evaluateDouble();
				long gcdLeft = Kernel.gcd((long) evalCanceled, (long) evalLeft);
				long gcdRight = Kernel.gcd((long) evalCanceled, (long) evalRight);
				if (DoubleUtil.isEqual(gcdLeft, evalCanceled)) {
					return numerator.getRightTree();
				} else if (gcdLeft != 1) {
					double v = evalLeft / gcdLeft;
					double canceledDenumerator = denominator.divide(gcdLeft).evaluateDouble();
					return new ExpressionNode(kernel, new MyDouble(kernel, v).wrap()
							.multiplyR(numerator.getRightTree()),
							Operation.DIVIDE, new MyDouble(kernel, canceledDenumerator)
					);
				} else if (DoubleUtil.isEqual(gcdRight, evalCanceled)) {
					if (DoubleUtil.isEqual(evalCanceled, evalRight)) {
						return numerator.getLeftTree();
					} else {
						double v = evalRight / evalCanceled;
						return new ExpressionNode(kernel, new MyDouble(kernel, v),
								numerator.getOperation(), node.getLeftTree().getLeftTree()
						);
					}
				}
			}
		}
		return node;
	}

	private ExpressionNode doRationalize() {
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

	private ExpressionNode factorize() {
		for (Operation op: new Operation[]{Operation.MINUS, Operation.PLUS}) {
			if (denominator.isOperation(op)) {
				return checkFactorization(op);
			}
		}
		return null;
	}

	private static boolean hasTwoTags(ExpressionNode node) {
		// isSupported() guarantees that exactly one of the leaves is SQRT by now,
		// so no check is needed here.
		return node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS);
	}

	private ExpressionNode rationalizeAsLeafNumerator() {
		if (denominator.isOperation(Operation.SQRT)) {
			return new ExpressionNode(kernel,
					numerator.multiplyR(denominator),
					Operation.DIVIDE, denominator.getLeft());
		}
		return factorize();
	}

	private ExpressionNode checkFactorization(Operation op) {
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
						Operation.DIVIDE, newNumber(v));
			}
		}
		return null;
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

	private ExpressionNode rationalizeAsSquareRootProduct() {
		ExpressionNode product = multiplySquareRoots(numerator, denominator);
		return new ExpressionNode(kernel,
				product,
				Operation.DIVIDE,
				denominator.getLeft());
	}

	private static boolean bothHaveSquareRoot(ExpressionNode numerator,
			ExpressionNode denominator) {
		return numerator.isOperation(Operation.SQRT) && denominator.isOperation(Operation.SQRT);
	}

	private ExpressionNode multiplySquareRoots(ExpressionNode left, ExpressionNode right) {
		double product = left.getLeftTree().multiply(right.getLeft())
				.wrap().evaluateDouble();

		return new ExpressionNode(kernel, newNumber(product),
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

	static ExpressionNode getReducedRoot(ExpressionNode node, Kernel kernel) {
		if (node.isOperation(Operation.DIVIDE)) {
			ExpressionNode nominator = node.getLeftTree();
			if (nominator.isOperation(Operation.SQRT)) {
				ExpressionValue reducedSqrt = Surds.getResolution(nominator, kernel);
				if (reducedSqrt != null) {
					return new ExpressionNode(kernel, reducedSqrt, Operation.DIVIDE,
							node.getRightTree());
				}
			} else if (nominator.isOperation(Operation.MULTIPLY)) {
				ExpressionNode rightTree = nominator.getRightTree();
				ExpressionValue reducedSqrt = Surds.getResolution(rightTree, kernel);
				if (reducedSqrt != null) {
					ExpressionNode constantProduct =
							nominator.getLeftTree().multiplyR(reducedSqrt.wrap().getLeftTree());
					return new ExpressionNode(kernel,
							reducedSqrt.wrap().getRightTree().multiplyR(
									constantProduct.unwrap().evaluateDouble()),
							Operation.DIVIDE,
							node.getRightTree());
				}
			}
		}
		return node;
	}

	private MyDouble newNumber(double outer) {
		return new MyDouble(kernel, outer);
	}
}