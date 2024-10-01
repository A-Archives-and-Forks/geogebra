package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.implicit.PlotRect;

public class BernsteinPlotRect implements PlotRect {
	private final BernsteinBoundingBox box;
	private final double[] corners = new double[4];

	public BernsteinPlotRect(BernsteinBoundingBox box, BernsteinPolynomial polynomial) {
		this.box = box;
		corners[0] = polynomial.evaluate(x1(), y1());
		corners[1] = polynomial.evaluate(x2(), y1());
		corners[2] = polynomial.evaluate(x2(), y2());
		corners[3] = polynomial.evaluate(x1(), y2());
	}

	@Override
	public double x1() {
		return box.x1();
	}

	@Override
	public double y1() {
		return box.y1();
	}

	@Override
	public double x2() {
		return box.x2();
	}

	@Override
	public double y2() {
		return box.y2();
	}

	@Override
	public double topLeft() {
		return corners[0];
	}

	@Override
	public double topRight() {
		return corners[1];
	}

	@Override
	public double bottomLeft() {
		return corners[3];
	}

	@Override
	public double bottomRight() {
		return corners[2];
	}

	@Override
	public double cornerAt(int i) {
		return corners[i];
	}
}
