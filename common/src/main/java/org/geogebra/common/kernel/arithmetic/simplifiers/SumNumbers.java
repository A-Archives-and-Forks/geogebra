package org.geogebra.common.kernel.arithmetic.simplifiers;


import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;

public class SumNumbers implements SimplifyNode{
	private final Kernel kernel;
	private int sum;

	public SumNumbers(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		ExpressionNode node1 = node.deepCopy(kernel);
		node.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				ExpressionNode n = ev.wrap();
				if (ev.isOperation(Operation.PLUS)) {
					if (isDiff(n.getLeftTree(), n.getRightTree()) || isDiff(n.getRightTree(), n.getLeftTree())) {
						ExpressionNode leftTree = n.getLeftTree();
						n.setLeft(n.getRight());
						n.setRight(leftTree);
					}
				}
				return ev;
			}

			private boolean isDiff(ExpressionNode n1, ExpressionNode n2) {
				return n1.isLeaf() && !n2.isLeaf();
			}
		});

		node = node.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				ExpressionNode n = ev.wrap();
				double v = n.evaluateDouble();

				return isInt(v) ? new MyDouble(kernel, v) : ev;

			}

		}).wrap();
		return node.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				double v = ev.evaluateDouble();
				if (isInt(v)) {
					return new MyDouble(kernel, v);
				}
				return ev;
			}

			}).wrap();
	}


	private boolean isInt(double v) {
		return Math.round(v) == v;
	}
}
