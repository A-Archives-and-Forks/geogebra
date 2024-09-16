package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Inspecting.OperationChecker;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;

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
		if (denominator.isOperation(Operation.SQRT)) {
			ExpressionValue rationalized = denominator.getLeft();
			if (numerator.isLeaf()) {
				result = new ExpressionNode(kernel,
						numerator.multiply(denominator),
						Operation.DIVIDE, rationalized);
			} else {
				ExpressionNode numeratorLeft =
						simplifiedMultiply(rationalized, numerator.getLeftTree(), denominator);
				ExpressionNode numeratorRight =
						simplifiedMultiply(rationalized, numerator.getRightTree(), denominator);
				ExpressionNode newNumerator =
						new ExpressionNode(kernel, numeratorLeft, numerator.getOperation(),
								numeratorRight);
				result = new ExpressionNode(kernel,
						newNumerator,
						Operation.DIVIDE, rationalized);

			}
		}
		return result != null ? result.toOutputValueString(tpl) : "";
	}

	private static ExpressionNode simplifiedMultiply(ExpressionValue rationalized,
			ExpressionNode node1, ExpressionNode denominator) {
		return rationalized.equals(node1.getLeft())
				? rationalized.wrap()
				: node1.multiply(denominator);
	}
}
