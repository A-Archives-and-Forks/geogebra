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
		BernsteinPolynomial[] polynomials = polynomial.split();
		CurvePlotBoundingBox[] boxes = boundingBox.split();
		CurvePlotContext[] contexts = new CurvePlotContext[4];
		for (int i = 0; i < polynomials.length; i++) {
			contexts[i] = new CurvePlotContext(boxes[i], polynomials[i]);
		}
		return contexts;
	}

	public void classifyCells() {

	}

	public void findSolutionsInEdges() {

	}

	public void findSolutionsInBox() {

	}

	public void linkSolutions() {

	}
}
