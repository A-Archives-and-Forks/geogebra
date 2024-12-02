package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.SimplifyUtils.isIntegerValue;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.SimplifyUtils;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class FactorOut implements SimplifyNode {

	private final SimplifyUtils utils;

	public FactorOut(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (node.isOperation(Operation.DIVIDE)) {
			return utils.div(apply(node.getLeftTree()), apply(node.getRightTree()));
		}
		if (node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS)) {
			ExpressionNode factored = factorOutIfPossible(node);
			return factored != null ? factored : node;
		}
		return node;
	}

	private ExpressionNode factorOutIfPossible(ExpressionNode node) {
		ExpressionNode leftTree = node.getLeftTree();
		ExpressionNode rightTree = node.getRightTree();

		if (node.isOperation(Operation.PLUS)) {
			return factorOutAddition(node);
		}
		if (node.isOperation(Operation.MINUS)) {
			return factorOutSubtraction(node);
		}

		return null;
	}

	private ExpressionNode factorOutAddition(ExpressionNode node) {
		Log.debug("FactorOutAddition");
		ExpressionNode leftTree = node.getLeftTree();
		ExpressionNode rightTree = node.getRightTree();
		double leftValue = leftTree.evaluateDouble();
		double rightValue = rightTree.evaluateDouble();

		if (leftValue > 0 && isIntegerValue(leftTree) && rightValue > 0
				&& !hasGCD(leftTree, rightTree)) {
			Log.debug("NotChanging");
			return node;
		}

		if (leftTree.isLeaf()) {
			return factorOutGCD((int) leftValue, rightTree, Operation.PLUS);
		}
		return factorOutGCD(leftTree, (int) rightValue, Operation.PLUS);
	}

	private ExpressionNode factorOutSubtraction(ExpressionNode node) {
		ExpressionNode leftTree = node.getLeftTree();
		ExpressionNode rightTree = node.getRightTree();
		double leftValue = leftTree.evaluateDouble();
		double rightValue = rightTree.evaluateDouble();

		if (leftValue > 0 && isIntegerValue(leftTree) && rightValue > 0
				&& !hasGCD(leftTree, rightTree)) {
			Log.debug("NotChanging");
			return node;
		}

		if (leftTree.isLeaf()) {
			int number = (int) leftTree.evaluateDouble();
			if (leftValue < 0 && rightValue > 0) {
				return factorOutSubOfTwoNegatives(leftTree, rightTree);
			}
			if (leftValue > 0 && rightValue > 0) {
				return factorOutSubtraction(number, rightTree);
			}
		}

		if (rightTree.isLeaf()) {
			if (leftValue < 0 && rightValue > 0) {
				return factorOutSubOfTwoNegatives(leftTree, rightTree);
			}

			if (leftValue > 0 && rightValue > 0) {
				return factorOutSubtraction(leftTree, rightTree);
			}
		}


		return null;
	}

	private ExpressionNode factorOutSubOfTwoPositives(ExpressionNode num, ExpressionNode expr) {
		if (expr.isOperation(Operation.MULTIPLY) && isIntegerValue(expr.getLeft())) {
			return factorOutGCD((int) num.evaluateDouble(), expr, Operation.MINUS);
		}
		return utils.newNode(num, Operation.MINUS, expr);
	}

	private boolean hasGCD(ExpressionNode leftTree, ExpressionNode rightTree) {
		ExpressionNode treeMul = rightTree.getLeftTree();
		if (leftTree.isLeaf() && isIntegerValue(leftTree) && treeMul != null && isIntegerValue(
				treeMul)) {
			int v = (int) leftTree.evaluateDouble();
			int v1 = (int) treeMul.evaluateDouble();
			long gcd = Kernel.gcd(v, v1);
			return (v % v1 == 0) || gcd != v1;
		}
		ExpressionNode treeMul2 = leftTree.getLeftTree();
		if (rightTree.isLeaf() && isIntegerValue(rightTree) && treeMul != null && isIntegerValue(
				treeMul)) {
			long gcd = Kernel.gcd((int) rightTree.evaluateDouble(), (int) treeMul.evaluateDouble());
			int v = (int) rightTree.evaluateDouble();
			int v1 = (int) treeMul.evaluateDouble();
			return (v % v1 == 0) || gcd != v1;
		}
		return true;
	}

	ExpressionNode factorOutSubOfTwoNegatives(ExpressionNode leftTree, ExpressionNode rightTree) {
		return utils.makeNegative(factorOutSubtraction(leftTree, rightTree));
	}

	private ExpressionNode factorOutSubtraction(ExpressionNode leftTree, ExpressionNode rightTree) {
		if (isIntegerValue(leftTree)) {
			int constNumber = (int) leftTree.evaluateDouble();
			if (rightTree.isOperation(Operation.MULTIPLY) && isIntegerValue(rightTree.getLeft())) {
				return factorOutGCD(constNumber, rightTree, Operation.PLUS);
			}
			return utils.newNode(
					utils.newDouble(-constNumber), Operation.PLUS, rightTree);
		}

		if (isIntegerValue(rightTree)) {
			int constNumber = (int) rightTree.evaluateDouble();
			if (leftTree.isOperation(Operation.MULTIPLY) && isIntegerValue(leftTree.getLeft())) {
				return factorOutGCDWithSub(leftTree, constNumber, Operation.PLUS);

			}
			double rightValue = rightTree.evaluateDouble();
			return utils.newNode(
					leftTree.getRightTree(), Operation.PLUS, rightTree);
		}
		return null;
	}

	private ExpressionNode factorOutSubtraction(int number, ExpressionNode expr) {
		int treeMultiplier = utils.getLeftMultiplier(expr);
		if (treeMultiplier != 1) {
			return factorOut(number, treeMultiplier, expr.getRightTree());
		}

		return utils.newNode(utils.newDouble(number), Operation.PLUS, expr);
	}

	private ExpressionNode factorOut(int num, int exprMultiplier, ExpressionNode expresssion) {
		long gcd = Kernel.gcd(num, exprMultiplier);

		double numFactor = num / gcd;
		double exprFactor = exprMultiplier / gcd;
		return utils.multiplyR(
				utils.newNode(utils.newDouble(numFactor),
						Operation.MINUS, utils.multiplyR(expresssion, exprFactor)),
				gcd);
	}

	private ExpressionNode factorOutGCD(ExpressionNode node, int constNumber, Operation operation) {
		return factorOutGCD(node, constNumber, operation, false);
	}

	private ExpressionNode factorOutGCD(int constNumber, ExpressionNode node, Operation operation) {
		return factorOutGCD(node, constNumber, operation, true);
	}

	private ExpressionNode factorOutGCDWithSub(ExpressionNode node, int constNumber,
			Operation operation) {
		return factorOutGCDWithSub(node, constNumber, operation, false);
	}

	ExpressionNode factorOutGCD(ExpressionNode rightTree, int constNumber, Operation operation,
			boolean numberFirst) {
		int treeMultiplier = (int) rightTree.getLeft().evaluateDouble();
		long gcd = Kernel.gcd(constNumber, treeMultiplier);

		double constFactor = constNumber / gcd;
		double treeFactor = treeMultiplier / gcd;
		boolean shouldInvert = operation.equals(Operation.PLUS) && constFactor < 0;

		MyDouble factoredNumber = utils.newDouble(shouldInvert ? -constFactor : constFactor);
		ExpressionNode expFactor = utils.newDouble(treeFactor).wrap();

		ExpressionNode factoredExpression = utils.multiplyR(
				expFactor,
				rightTree.getRightTree()
		);

		ExpressionNode addition = !numberFirst
				? utils.newNode(factoredExpression, operation, factoredNumber)
				: utils.newNode(factoredNumber, operation, factoredExpression);
		return utils.multiplyR(addition, shouldInvert ? -gcd : gcd);
	}

	ExpressionNode factorOutGCDWithSub(ExpressionNode rightTree, int constNumber,
			Operation operation,
			boolean numberFirst) {
		int treeMultiplier = (int) rightTree.getLeft().evaluateDouble();
		long gcd = Kernel.gcd(constNumber, treeMultiplier);

		double constFactor = constNumber / gcd;
		double treeFactor = treeMultiplier / gcd;

		MyDouble factoredNumber = utils.newDouble(constFactor);
		ExpressionNode expFactor = utils.newDouble(treeFactor).wrap();

		ExpressionNode factoredExpression = utils.multiplyR(
				expFactor,
				rightTree.getRightTree()
		);

		ExpressionNode addition = !numberFirst
				? utils.newNode(factoredExpression, Operation.MINUS, factoredNumber)
				: utils.newNode(factoredNumber, Operation.MINUS, factoredExpression);
		return utils.multiplyR(addition, gcd);
	}
}