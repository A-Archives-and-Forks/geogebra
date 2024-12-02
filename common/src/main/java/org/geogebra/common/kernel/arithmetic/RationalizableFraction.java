package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.SimplifyUtils.isNodeSupported;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

public final class RationalizableFraction {

	private static OperationCountChecker sqrtCountChecker =
			new OperationCountChecker(Operation.SQRT);

	/**
	 *
	 * @param root node to check
	 * @return if geo is a rationalizable fraction and supported by this algo.
	 */
	public static boolean isSupported(ExpressionNode root) {

		if (root == null) {
			return false;
		}

		if (!Operation.DIVIDE.equals(root.getOperation())) {
			return false;
		}

		int sq1 = getSquareRootCount(root.getLeft());
		int sq2 = getSquareRootCount(root.getRight());
		if ((sq1 > 1 || sq2 > 1) || (sq1 + sq2 == 0)) {
			return false;
		}

		ExpressionNode numerator = stripFromIntegerMultiplication(root.getLeftTree());
		ExpressionNode denominator = stripFromIntegerMultiplication(root.getRightTree());

		return isNodeSupported(numerator) && isNodeSupported(denominator);
	}

	private static ExpressionNode stripFromIntegerMultiplication(ExpressionNode leftTree) {
		return isMultipliedByInteger(leftTree)
				? leftTree.getRightTree()
				: leftTree;
	}

	private static boolean isMultipliedByInteger(ExpressionNode node) {
		return node.isOperation(Operation.MULTIPLY) && node.getLeft().isLeaf()
				&& isIntegerEvaluation(node.getLeftTree());
	}

	private static int getSquareRootCount(ExpressionValue node) {
		sqrtCountChecker.reset();
		node.inspect(sqrtCountChecker);
		return sqrtCountChecker.getCount();
	}

	private static boolean isIntegerEvaluation(ExpressionNode node) {
		return evaluateAsInteger(node) != null;
	}

	private static boolean isSquareRootPlusMinusInteger(ExpressionNode node1,
			ExpressionNode node2) {
		return node1.isOperation(Operation.SQRT) && isIntegerEvaluation(node2);
	}

	/**
	 * Rationalize the fraction.
	 * @param node to rationalize
	 * @return the rationalized fraction.
	 */
	public static ExpressionNode getResolution(ExpressionNode node) {
		if (!isSupported(node)) {
			return null;
		}
		
		Integer v = evaluateAsInteger(node);
		Kernel kernel = node.getKernel();
		if (v != null) {
			return new MyDouble(kernel, v).wrap();
		}

		Integer denominatorValue = evaluateAsInteger(node.getRightTree());
		if (denominatorValue != null) {
			return denominatorValue == 1
			? new ExpressionNode(node.getLeftTree())
					: createFractionWithIntegerDenominator(node, kernel, denominatorValue);
		}

		Log.debug("start: " + node.toValueString(StringTemplate.defaultTemplate));
		RationalizeFractionAlgo algo =
				new RationalizeFractionAlgo(kernel, node.getLeftTree(),
						node.getRightTree());
		return algo.compute();
	}

	private static ExpressionNode createFractionWithIntegerDenominator(ExpressionNode node, Kernel kernel,
			Integer denominatorValue) {
		if (denominatorValue < 0) {
			ExpressionNode numerator = RationalizeFractionAlgo.processUnderSqrts(node.getLeftTree().deepCopy(kernel), kernel);
			numerator.traverse(new Traversing() {
				ExpressionNode parent = null;
				@Override
				public ExpressionValue process(ExpressionValue ev) {
					if (ev instanceof MySpecialDouble && (parent == null
							|| !parent.isOperation(Operation.SQRT))) {
						return new MyDouble(kernel, -ev.evaluateDouble());
					}
					parent = ev.wrap();
					return ev;
				}
			});
			numerator.setOperation(SimplifyUtils.flip(numerator.getOperation()));

				return new ExpressionNode(kernel, numerator,
					Operation.DIVIDE, new MyDouble(kernel, -denominatorValue));
		}
		return new ExpressionNode(kernel, node.getLeftTree(), Operation.DIVIDE,
				new MyDouble(kernel, denominatorValue));
	}

	private static Integer evaluateAsInteger(ExpressionNode node) {
		double v = node.evaluateDouble();
		return DoubleUtil.isEqual(v,  Math.round(v), Kernel.MAX_PRECISION) ? (int) v : null;
	}
}