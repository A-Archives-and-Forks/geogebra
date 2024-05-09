package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial {
	private final Polynomial polynomial;
	private final GRectangle bounds;
	private final int degX;
	private ExpressionNode output;
	private final Kernel kernel;
	private final FunctionVariable[] functionVariables;

	public BernsteinPolynomial(Polynomial polynomial, Kernel kernel,
			GRectangle bounds, int degX, int degY, FunctionVariable[] functionVariables) {
		this.polynomial = polynomial;
		this.bounds = bounds;
		this.degX = degX;
		this.kernel = kernel;
		this.functionVariables = functionVariables;
	}

	void construct(int degX) {
		for (int i = 0; i < degX; i++) {
			makeBasis(i, functionVariables[0]);
		}
		output.simplifyLeafs();
		Log.debug("Out: " + output);
	}

	void makeBasis(int i, FunctionVariable fv) {
		for (int j = 0; j <= i; j++) {
			if (j == 0 && i == j) {
				continue;
			}

			MyDouble binomialCoefficient = new MyDouble(kernel, b(i, j));
			ExpressionNode oneMinusXPowerJ = powerOf(getOneMinusX(fv), j);
			ExpressionNode xPowerIMinusJ = powerOf(fv.wrap(), i - j);
			ExpressionNode tag = binomialCoefficient.wrap();
			if (oneMinusXPowerJ != null) {
				tag = tag.multiply(oneMinusXPowerJ);
			}
			if (xPowerIMinusJ != null) {
				tag = tag.multiply(xPowerIMinusJ);
			}

			if (output == null) {
				output = tag;
			} else {
				output = new ExpressionNode(kernel, output, Operation.PLUS, tag);
			}

		}
	}

	private ExpressionNode powerOf(ExpressionNode node, int power) {
		if (power == 0) {
			return null;
		}

		if (power == 1) {
			return node;
		}

		return new ExpressionNode(kernel,
				node,
				Operation.POWER,
				new MyDouble(kernel, power));
	}

	private ExpressionNode getOneMinusX(FunctionVariable fv) {
		return new ExpressionNode(kernel, new MyDouble(kernel, 1), Operation.MINUS,
				fv);
	}

	int b(int i, int j) {
		if (i == 0 && j == 0) {
			return coeffX(degX);
		}

		double xl = bounds.getMinX();
		double xh = bounds.getMaxX();

		if (j == 0) {
			return (int) (coeffX(degX - i) + xl * b(i - 1, 0));
		}

		if (i == j) {
			return (int) (coeffX(degX - i) + xh * b(i - 1, i - 1));
		}

		int n = degX;
		double binomial = MyMath.binomial(n - i, j);
		int a_ni = coeffX(n - i);
		int b_i1j = b(i - 1, j);
		int b_i1j1 = b(i - 1, j - 1);
		return (int) (binomial * a_ni
				+ xl * b_i1j
				+ xh * b_i1j1);
	}

	private int coeffX(int i) {
		Term term = polynomial.getTerm(i - 1);
		return (int) term.coefficient.evaluateDouble();
	}
}
