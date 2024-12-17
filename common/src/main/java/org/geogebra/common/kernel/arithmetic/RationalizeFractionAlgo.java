
package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.simplifiers.ReduceRoot;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class RationalizeFractionAlgo {
	private final Kernel kernel;
	private final ExpressionNode numerator;
	private final ExpressionNode denominator;
	private final SimplifyUtils utils;

	public RationalizeFractionAlgo(@NonNull SimplifyUtils utils,
			@NonNull ExpressionNode numerator,
			@NonNull ExpressionNode denominator) {
		this.utils = utils;
		this.kernel = numerator.getKernel();
		this.numerator = numerator.deepCopy(kernel);
		this.denominator = denominator.deepCopy(kernel);


	}

	public ExpressionNode compute() {
		ExpressionNode node = doRationalize();
		if (node == null) {
			return null;
		}
		return checkDecimals(node) ? null : node;
	}


	/**
	 * Package private to be testable in isolation
	 *
	 * @param node to test
	 * @return if the expressx
	 */
	static boolean checkDecimals(ExpressionNode node) {
		return node.inspect(new Inspecting() {
			@Override
			public boolean check(ExpressionValue v) {
				return (v instanceof NumberValue
				|| v instanceof MyDouble) && !isIntegerValue(v.wrap());
			}
		});
}


	public static boolean isIntegerValue(ExpressionNode node) {
		double v = node.evaluateDouble();
		return DoubleUtil.isEqual(v, Math.rint(v), Kernel.MAX_PRECISION);
	}

	private ExpressionNode doRationalize() {
		if (numerator.isLeaf()) {
			return rationalizeAsLeafNumerator();
		}

		if (bothHaveSquareRoot(numerator, denominator)) {
			return rationalizeAsSquareRootProduct();
		}

		if (numerator.isOperation(Operation.SQRT) || canBeFactorized()) {
			return factorizeOrHandleProduct();
		}

		return rationalizeAsLeafSqrtDenominator();
	}

	private boolean canBeFactorized() {
		return hasTwoTags(numerator) && hasTwoTags(denominator);
	}

	/**
	 * If the fraction can be factorized, it is done here
	 * or if denominator is a product, it is handled here too.
	 *
	 * @return the altered expression described above.
	 */
	private ExpressionNode factorizeOrHandleProduct() {
		for (Operation op: new Operation[]{Operation.MINUS, Operation.PLUS}) {
			if (denominator.isOperation(op)) {
				return doFactorize(denominator);
			}
		}
		if (denominator.isOperation(Operation.MULTIPLY)) {
			return handleProductInDenominator();
		}
		return null;
	}

	private ExpressionNode handleProductInDenominator() {
		ExpressionNode expanded = utils.expand(denominator);
		ExpressionNode rightOperand = expanded;
		Operation rightOperandOperation = rightOperand.getOperation();
		if (hasTwoTags(rightOperand)) {
 			return doFactorize(expanded);
		} if (rightOperandOperation == Operation.SQRT) {
			ExpressionNode sqrt = denominator.getRightTree();
			return utils.newNode(this.numerator.multiplyR(sqrt), Operation.DIVIDE,
					denominator.multiplyR(sqrt));
		} else {
			ExpressionNode numerator = new ExpressionNode(kernel, expanded.getLeftTree(),
					rightOperandOperation, null);
			ExpressionNode denominator = expanded.getLeftTree().getLeftTree()
					.multiply(rightOperand.getLeft());
			return new ExpressionNode(kernel, numerator, Operation.DIVIDE,
					newNumber(denominator));
		}
	}

	private ExpressionValue newNumber(ExpressionNode node) {
		return new MyDouble(kernel, node.evaluateDouble());
	}

	private static boolean hasTwoTags(ExpressionNode node) {
		// isSupported() guarantees that exactly one of the leaves is SQRT by now,
		// so no check is needed here.
		return node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS);
	}

	private ExpressionNode rationalizeAsLeafNumerator() {
		if (denominator.isOperation(Operation.SQRT)) {
			ExpressionNode sqrtOf = simplifyUnderSqrt(denominator, kernel);
			return utils.div(numerator.multiplyR(sqrtOf), sqrtOf.getLeftTree());
		}
		return factorizeOrHandleProduct();
	}

	static ExpressionNode processUnderSqrts(final ExpressionNode node, Kernel kernel) {
		ReduceRoot reduceRoot = new ReduceRoot(new SimplifyUtils(kernel));
		return reduceRoot.apply(node);
	}

	private static ExpressionNode simplifyUnderSqrt(ExpressionNode node, Kernel kernel) {
		if (node.getLeft().isLeaf()) {
			return node;
		}
		double underSqrt = node.getLeft().evaluateDouble();
		MyDouble left = new MyDouble(kernel, underSqrt);
		return new ExpressionNode(kernel, left, Operation.SQRT,
				null);
	}

	private ExpressionNode doFactorize(ExpressionNode node) {
		ExpressionNode result = null;
		Operation op = node.getOperation();
		ExpressionNode conjugate = getConjugateFactor(node);
		double newDenominatorValue = node.multiply(conjugate).evaluateDouble();
		if (isOne(newDenominatorValue)) {
			result = new ExpressionNode(kernel, numerator.multiplyR(conjugate));
		} else if (isMinusOne(newDenominatorValue)) {
			ExpressionNode minusConjugate = getMinusConjugate(node, op);
			result = utils.multiply(numerator, minusConjugate);
		} else if (DoubleUtil.isInteger(newDenominatorValue)) {
			// if new denominator is integer but not 1 or -1
			result = utils.newNode(
					numerator.multiplyR(conjugate),
					Operation.DIVIDE, utils.newDouble(newDenominatorValue));

		}
		return result;
	}

	private ExpressionNode getMinusConjugate(ExpressionNode node, Operation op) {
		ExpressionNode minusConjugate =
				new ExpressionNode(kernel, node.getLeft().wrap().multiplyR(-1),
						Operation.inverse(op),
						node.getRight().wrap().multiplyR(-1));
		return minusConjugate;
	}

	private static boolean isMinusOne(double newDenominatorValue) {
		return DoubleUtil.isEqual(newDenominatorValue, -1, Kernel.STANDARD_PRECISION);
	}

	private static boolean isOne(double newDenominatorValue) {
		return DoubleUtil.isEqual(newDenominatorValue, 1, Kernel.STANDARD_PRECISION);
	}

	private ExpressionNode getConjugateFactor(ExpressionNode node) {
		return new ExpressionNode(kernel, node.getLeft(), Operation.inverse(node.getOperation()),
				node.getRight());
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

	private MyDouble newNumber(double outer) {
		return new MyDouble(kernel, outer);
	}
}