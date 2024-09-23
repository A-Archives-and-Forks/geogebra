package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.arithmetic.Inspecting.OperationChecker;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public final class RationalizableFraction {

	/**
	 *
	 * @param geo to check
	 * @return if geo is a rationalizable fraction and supported by this algo.
	 */
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
		if ((sq1 > 1 || sq2 > 1) || (sq1 + sq2 == 0)) {
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

	/**
	 * Rationalize the fraction.
	 * @param node to rationalize
	 * @return the rationalized fraction.
	 */
	public static ExpressionNode rationalize(ExpressionNode node) {
		Integer v = getValueIfTrivial(node);
		if (v != null) {
			return new MyDouble(node.getKernel(), v).wrap();
		}

		RationalizeFractionAlgo algo =
				new RationalizeFractionAlgo(node.getKernel(), node.getLeftTree(),
						node.getRightTree());
		return algo.compute();
	}

	public static boolean isNonTrivial(GeoElement geo) {
		if (geo.isGeoNumeric()) {
			double v = ((GeoNumeric) geo).evaluateDouble();

			return !DoubleUtil.isEqual(Math.abs(v), (int) v);
		};
		return false;
	}

	public static Integer getValueIfTrivial(ExpressionNode num) {
		double v = num.evaluateDouble();
		return DoubleUtil.isEqual(v,  (int) v)? (int) v : null;


	}
}
