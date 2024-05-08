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
		for (int i = 0; i < degX; i++) {
			getBasis(i);
		}
	}

	private int getBasis(int i) {
		for (int j = 0; j < i; j++) {
			Log.debug(b(i, j) + " * (1 - y)^"+j+" * y^" + (i-j));
		}
		return 0;
	}

	private int b(int i, int j) {
		return (int) (MyMath.binomial(degX - i, j) * coeffX(degX - i)
				+ bounds.getMinX() * b( i - 1, j)
				+ bounds.getMaxX() * b( i - 1, j - 1));
	}

	private int coeffX(int i) {
		return (int) coeffs[0][i - 1].evaluateDouble();
	}
}
