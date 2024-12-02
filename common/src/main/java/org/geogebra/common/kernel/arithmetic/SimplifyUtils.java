package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public class SimplifyUtils {
	public final Kernel kernel;

	public SimplifyUtils(Kernel kernel) {
		this.kernel = kernel;
	}

	public ExpressionNode flipTrees(ExpressionNode node) {
		return newNode(node.getRightTree(), node.getOperation(), node.getLeftTree());
	}

	public MyDouble newDouble(double v) {
		return new MyDouble(kernel, v);
	}

	public ExpressionNode newNode(ExpressionValue left, Operation operation, ExpressionValue right) {
		return new ExpressionNode(kernel, left, operation, right);
	}

	public ExpressionNode div(ExpressionValue numerator, double number) {
		ExpressionNode expanded = expand(numerator.wrap());
		return number > 0
				? newNode(numerator, Operation.DIVIDE, newDouble(number))
				: newNode(negate(numerator.wrap()), Operation.DIVIDE, newDouble(-number));
	}

	public ExpressionNode div(ExpressionValue numerator, ExpressionNode denominator) {
		double valDenominator = denominator.evaluateDouble();
		if (isIntegerValue(denominator)) {
			if (valDenominator > 0) {
				return newNode(numerator, Operation.DIVIDE, newDouble(valDenominator));
			}

			return newNode(mulByMinusOneL(numerator), Operation.DIVIDE, newDouble(-valDenominator));
		}
		return newNode(numerator, Operation.DIVIDE, denominator);
	}

	public ExpressionNode multiply(ExpressionNode node1, ExpressionNode node2) {
		if (isOne(node1)) {
			return node2;
		}

		if (isOne(node2)) {
			return node1;
		}

		if (isIntegerValue(node1) && isIntegerValue(node2)) {
			return newDouble(node1.evaluateDouble() * node2.evaluateDouble()).wrap();
		}

		return node1.multiply(node2);
	}

	public ExpressionNode multiply(ExpressionNode node1, double v) {
		if (isOne(node1)) {
			return newDouble(v).wrap();
		}

		if (isOne(v)) {
			return node1;
		}

		if (isIntegerValue(node1) && isIntegerValue(v)) {
			return newDouble(node1.evaluateDouble() * v).wrap();
		}

		return node1.multiply(v);
	}

	public ExpressionNode multiplyR(ExpressionNode node1, double v) {
		if (isOne(node1)) {
			return newDouble(v).wrap();
		}

		if (isOne(v)) {
			return node1;
		}
		if (isIntegerValue(node1) && isIntegerValue(v)) {
			return newDouble(node1.evaluateDouble() * v).wrap();
		}

		return node1.multiplyR(v);
	}

	public ExpressionNode multiplyR(ExpressionNode node1, ExpressionNode node2) {
		if (isOne(node1)) {
			return node2;
		}

		if (isOne(node2)) {
			return node1;
		}

		if (isIntegerValue(node1) && isIntegerValue(node2)) {
			return newDouble(node1.evaluateDouble() * node2.evaluateDouble()).wrap();
		}
		return node1.multiplyR(node2);
	}


	private static boolean isOne(ExpressionNode node) {
		return isOne(node.evaluateDouble());
	}

	private static boolean isOne(double v) {
		return DoubleUtil.isEqual(v, 1, Kernel.STANDARD_PRECISION);
	}

	public ExpressionNode newInverseNode(ExpressionNode leftTree, Operation operation,
			ExpressionNode rightTree) {
		ExpressionValue left = invert(leftTree);
		ExpressionValue right = invert(rightTree);
		return newNode(right, invert(operation), left);
	}

	private Operation invert(Operation operation) {
		return operation == Operation.PLUS ? Operation.MINUS : Operation.PLUS;
	}

	ExpressionValue invert(ExpressionNode node) {
		if (node.isLeaf()) {
			return newDouble(node.evaluateDouble());
		}

		ExpressionValue left = node.getLeft();
		if (node.isOperation(Operation.MULTIPLY) && left.evaluateDouble() == -1) {
			node = newLeaf(node.getRight());
		}
		return node;
	}

	private ExpressionNode newLeaf(ExpressionValue node) {
		return newNode(node, Operation.NO_OPERATION, null);
	}

	public ExpressionNode expand(ExpressionNode node) {
		if (node.getOperation() != Operation.MULTIPLY) {
			return node;
		}
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		boolean leftInteger = isIntegerValue(left);
		boolean rightInteger = isIntegerValue(right);
		if (leftInteger && rightInteger) {
			return newDouble(left.evaluateDouble() * right.evaluateDouble()).wrap();
		}

		if (leftInteger && !rightInteger) {
			return multiplyByInteger(right, left);
		}
		if (!leftInteger && rightInteger) {
			return multiplyByInteger(left, right);
		}
		return node;
	}

	private ExpressionNode multiplyByInteger(ExpressionValue right, ExpressionValue left) {
		ExpressionNode opLeft = right.wrap().getLeftTree();
		ExpressionNode opRight = right.wrap().getRightTree();
		double mul = left.evaluateDouble();
		return newNode(multiply(opLeft, mul), right.wrap().getOperation(),
				multiplyR(opRight, mul));
	}

	private static boolean isPositiveIntegerValue(ExpressionValue ev) {
		if (ev == null) {
			return false;
		}

		double value = ev.evaluateDouble();
		return isIntegerValue(value) && value >= 0;
	}
	public static boolean isIntegerValue(ExpressionValue ev) {
		return ev != null && isIntegerValue(ev.evaluateDouble());
	}

	private static boolean isIntegerValue(double value) {
		return DoubleUtil.isEqual(Math.round(value), value, Kernel.STANDARD_PRECISION);

	}

	public ExpressionValue mulByMinusOneL(ExpressionValue ev) {
		ExpressionNode node = ev.wrap();
		ExpressionNode left = node.getLeftTree();
		ExpressionValue right = node.getRight();
		ExpressionNode leftNumber = negate(left);
		return mulByMinusOne(node, leftNumber, right);
	}

	public ExpressionValue mulByMinusOneR(ExpressionValue ev) {
		ExpressionNode node = ev.wrap();
		ExpressionValue left =node.getLeftTree().getRight().isOperation(Operation.MULTIPLY)
				? node.getLeftTree().getRight()
				: node.getLeftTree();
		ExpressionValue right = node.getRightTree();
		ExpressionNode number = newDouble(right.evaluateDouble()).wrap();
		ExpressionNode tree = left.wrap();
		return mulByMinusOne(node, negate(tree), number);
	}

	public ExpressionNode negate(ExpressionNode node) {
		if (node.isOperation(Operation.MULTIPLY) && node.getLeftTree().evaluateDouble() == -1) {
			return node.getRightTree();
		}
		double v = node.evaluateDouble();
		if (isIntegerValue(v)) {
			return newDouble(-v).wrap();
		}
		return node.isOperation(Operation.MINUS) ? node.multiplyR(-1) : node;
	}

	public ExpressionValue mulByMinusOne(ExpressionNode node,
			ExpressionNode leftNumber, ExpressionValue right) {
		Operation operation = node.getOperation();
		if (Operation.PLUS.equals(operation)) {
			return newNode(leftNumber, Operation.MINUS, right);
		}
		if (Operation.MINUS.equals(operation)) {
			return newNode(leftNumber, Operation.PLUS, right);
		}

		return node.multiplyR(-1);
	}

	public ExpressionNode negative(ExpressionValue ev) {
		return negative(ev.wrap());
	}
	public ExpressionNode negative(ExpressionNode node) {
		ExpressionValue mul = mulByMinusOne(node);
		return mul.wrap().multiply(minusOne());
	}

	public ExpressionValue mulByMinusOne(ExpressionNode node) {
		return isIntegerValue(node.getLeft())
				? mulByMinusOneL(node)
				: mulByMinusOneR(node);
	}

	private ExpressionValue newValue(ExpressionValue ev) {
		double v = ev.evaluateDouble();
		return isIntegerValue(v) ? newDouble(v) : ev;
	}
	public static boolean isSquareRootValidInteger(ExpressionValue ev) {
		if (!ev.isOperation(Operation.SQRT)) {
			return false;
		}
		return isPositiveIntegerValue(ev.wrap().getLeft());
	}


	public static boolean isNodeSupported(ExpressionNode node) {
		return (node.isLeaf() && isIntegerValue(node))
				|| isSquareRootValidInteger(node)
				|| isSqrtAndInteger(node);
	}

	private static boolean isSqrtAndInteger(ExpressionNode node) {
		if (!(node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS))) {
			return false;
		}

		return (isSquareRootValidInteger(node.getLeft()) && isIntegerValue(node.getRightTree()))
				|| (isIntegerValue(node.getLeftTree())
				&& isSquareRootValidInteger(node.getRightTree()));
	}

	public MinusOne minusOne() {
		return new MinusOne(kernel);
	}

	public static Operation flip(Operation operation) {
		if (operation == Operation.PLUS) {
			return Operation.MINUS;
		}
		if (operation == Operation.MINUS) {
			return Operation.PLUS;
		}
		return operation;
	}

	public ExpressionNode makeNegative(ExpressionNode node) {
		if (node == null) {
			return null;
		}

		ExpressionNode leftTree = node.getLeftTree();
		double leftNumber = leftTree.evaluateDouble();
		if (leftNumber < 0) {
			return node;
		}

		if (node.isOperation(Operation.MULTIPLY) && isIntegerValue(leftTree)) {
			node.setLeft(newDouble(-leftNumber));
			return node;
		}
		return new ExpressionNode(kernel, minusOne(), Operation.MULTIPLY, node);
	}

	public int getLeftMultiplier(ExpressionNode expr) {
		return expr.isOperation(Operation.MULTIPLY) && isIntegerValue(expr.getLeft())
				? (int) expr.getLeft().evaluateDouble()
				: 1;
	}
}
