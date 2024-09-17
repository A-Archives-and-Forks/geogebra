package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Inspecting.OperationChecker;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public class RationalizableFraction {

	public static boolean isSupported(GeoElementND geo) {
		if (!(geo instanceof GeoNumeric)) {
			return false;
		}
		ExpressionNode root = geo.getDefinition();
		if (root == null) {
			return false;
		}

		if (!Operation.DIVIDE.equals(root.getOperation())) {
			return false;
		}

		int sq1 = getSquareRootCount(root.getLeft());
		int sq2 = getSquareRootCount(root.getRight());
		if (((sq1 > 1 || sq2 > 1) || (sq1 + sq2 == 0))) {
			return false;
		}

		return isSupported(root.getLeftTree()) && isSupported(root.getRightTree());
	}

	private static int getSquareRootCount(ExpressionValue node) {
		OperationChecker operationChecker = OperationChecker.get(Operation.SQRT);
		node.inspect(operationChecker);
		return operationChecker.getCount();
	}

	private static boolean isSupported(ExpressionNode node) {
		return node.isLeaf() || node.inspect(
				v -> v.isOperation(Operation.SQRT) || v.isOperation(Operation.PLUS)
						|| v.isOperation(Operation.MINUS));
	}

	public static String apply(GeoNumeric num, StringTemplate tpl) {
		ExpressionNode node = num.getDefinition();
		ExpressionNode numerator = node.getLeftTree();
		ExpressionNode denominator = node.getRightTree();
		ExpressionNode result=null;
		Kernel kernel = num.getKernel();
		ExpressionValue rationalized = denominator.getLeft();
		if (numerator.isLeaf()) {
			result = getNumeratorIsLeaf(kernel, numerator, denominator);
		} else if (numerator.isOperation(Operation.SQRT)
				|| (hasTwoTags(numerator) && hasTwoTags(denominator))) {
			result = getNumeratorIsSqrt(kernel, numerator, denominator);
		} else {
 			ExpressionNode numeratorLeft =
					simplifiedMultiply(kernel, rationalized, numerator.getLeftTree(), denominator);
			ExpressionNode numeratorRight =
					simplifiedMultiply(kernel, rationalized, numerator.getRightTree(), denominator);
			ExpressionNode newNumerator =
					new ExpressionNode(kernel, numeratorLeft, numerator.getOperation(),
							numeratorRight);
			result = new ExpressionNode(kernel,
					newNumerator,
					Operation.DIVIDE, rationalized);

		}
		return result != null ? result.toOutputValueString(tpl) : "";
	}

	private static boolean hasTwoTags(ExpressionNode node) {
		// isSupported() guarantees that exactly one of the leaves is SQRT by now,
		// so no check is needed here.
		return node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS);
	}

	private static ExpressionNode getNumeratorIsLeaf(Kernel kernel, ExpressionNode numerator,
			ExpressionNode denominator) {
		if (denominator.isOperation(Operation.SQRT)) {
				return new ExpressionNode(kernel,
						numerator.multiply(denominator),
						Operation.DIVIDE, denominator.getLeft());

		}

		try {
			return checkFactorization(kernel, numerator, denominator, Operation.MINUS);
		} catch (NoFactorization e) {
			//
		}

		try {
			return checkFactorization(kernel, numerator, denominator, Operation.PLUS);
		} catch (NoFactorization e) {
			//
		}

		return null;
	}

	private static ExpressionNode checkFactorization(Kernel kernel, ExpressionNode numerator,
			ExpressionNode denominator, Operation op) throws NoFactorization {
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
						new ExpressionNode(kernel, denominator.getLeft().wrap().multiplyR(-1), Operation.inverse(op),
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

	private static ExpressionNode simplifiedMultiply(Kernel kernel, ExpressionValue rationalized,
			ExpressionNode node1, ExpressionNode denominator) {
		return rationalized.equals(node1.getLeft())
				? rationalized.wrap()
				: doMultiply(kernel, node1, denominator);
	}

	private static ExpressionNode doMultiply(Kernel kernel, ExpressionNode left, ExpressionNode right) {
		ExpressionNode sqrts = checkJoinSqrts(kernel, left, right);
		if (sqrts != null) {
			return sqrts;
		}
		return left.multiply(right);
	}

	private static ExpressionNode getNumeratorIsSqrt(Kernel kernel, ExpressionNode numerator,
			ExpressionNode denominator) {
		if (numerator.isOperation(Operation.SQRT)) {
			ExpressionNode sqrts = checkJoinSqrts(kernel, numerator, denominator);

			if (sqrts != null) {
				return new ExpressionNode(kernel,
						sqrts,
						Operation.DIVIDE, denominator.getLeft());
			}
		}

		try {
			return checkFactorization(kernel, numerator, denominator, Operation.MINUS);
		} catch (NoFactorization e) {
			//
		}

		try {
			return checkFactorization(kernel, numerator, denominator, Operation.PLUS);
		} catch (NoFactorization e) {
			//
		}

		return null;
	}

	private static ExpressionNode checkJoinSqrts(Kernel kernel, ExpressionNode numerator,
			ExpressionNode denominator) {
		return denominator.isOperation(Operation.SQRT) ?
				new ExpressionNode(kernel,
					new MyDouble(kernel, numerator.getLeftTree().multiply(denominator.getLeft())
						.wrap().evaluateDouble()),
				Operation.SQRT, null) : null;
	}
}
