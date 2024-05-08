package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial {
	private final Polynomial polynomial;
	private final GRectangle bounds;
	private final int degX;
	private final int degY;
	private final Polynomial output;
	private final Kernel kernel;
	private final ExpressionValue[][] coeffs;

	public BernsteinPolynomial(Polynomial polynomial, Kernel kernel,
			GRectangle bounds, int degX, int degY) {
		this.polynomial = polynomial;
		this.bounds = bounds;
		this.degX = degX;
		this.degY = degY;
		coeffs = polynomial.getCoeff();
		output = new Polynomial(kernel);
		this.kernel = kernel;
	}

	void construct(int degX) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < degX; i++) {
			getBasis(i, sb);
		}
		Log.debug("Output: " + sb);
	}

	int getBasis(int i, StringBuilder sb) {
		for (int j = 0; j < i; j++) {
			int b_ij = b(i, j);

			sb.append(b_ij);
			if (j > 0) {
				sb.append(" * (1 - x)");
				if (j > 1) {
					sb.append("^");
					sb.append(j);
				}
				sb.append(" * x");
				if (i-j > 1) {
					sb.append("^");
					sb.append((i-j));
				}
			}
			sb.append(" + ");

		}
		return 0;
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
		return (int) (MyMath.binomial(n - i, j) * coeffX(n - i)
				+ xl * b( i - 1, j)
				+ xh * b( i - 1, j - 1));
	}

	private int coeffX(int i) {
		Term term = polynomial.getTerm(i);
		return (int) term.coefficient.evaluateDouble();
	}
}
