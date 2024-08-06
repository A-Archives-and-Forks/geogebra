package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;

public class CurvePlotContext implements Splittable<CurvePlotContext> {
	CurvePlotBoundingBox boundingBox;
	BernsteinPolynomial polynomial;

	public CurvePlotContext(CurvePlotBoundingBox box, BernsteinPolynomial polynomial) {

		boundingBox = box;
		this.polynomial = polynomial;
	}

	@Override
	public CurvePlotContext[] split() {
		BernsteinPolynomial[][] polynomials = polynomial.split2D();
		CurvePlotBoundingBox[] boxes = boundingBox.split();
		CurvePlotContext[] contexts = new CurvePlotContext[4];
		contexts[0] = new CurvePlotContext(boxes[0], polynomials[0][0]);
		contexts[1] = new CurvePlotContext(boxes[1], polynomials[1][0]);
		contexts[2] = new CurvePlotContext(boxes[2], polynomials[0][1]);
		contexts[3] = new CurvePlotContext(boxes[3], polynomials[1][1]);
		return contexts;
	}

	public void process() {
		classifyCells();
		findSolutionsInEdges();
		findSolutionsInBox();
		linkSolutions();
	}

	private void classifyCells() {

	}

	private void findSolutionsInEdges() {

	}

	private void findSolutionsInBox() {

	}

	private void linkSolutions() {

	}
}
