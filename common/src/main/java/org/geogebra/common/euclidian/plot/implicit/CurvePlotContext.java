package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;

public class CurvePlotContext implements Splittable<CurvePlotContext> {
	CurvePlotBoundingBox boundingBox;
	BernsteinPolynomial polynomial;
	private BernsteinPolynomial dx;
	private BernsteinPolynomial dy;
	private ContextCass contextCass = ContextCass.NONE;

	enum ContextCass {
		NONE,
		CELL0,
		CELL1,
		CELL2
	}

	public CurvePlotContext(CurvePlotBoundingBox box, BernsteinPolynomial polynomial) {
		boundingBox = box;
		this.polynomial = polynomial;
		dx = polynomial.derivative("x");
		dy = polynomial.derivative("y");
		contextCass = classify();
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
		findSolutionsInEdges();
		findSolutionsInBox();
		linkSolutions();
	}

	private ContextCass classify() {
		if (!polynomial.hasNoSolution()) {
			return ContextCass.CELL2;
		}

		if (dx.hasNoSolution() != dy.hasNoSolution()) {
			return ContextCass.CELL1;
		}

		return contextCass = ContextCass.CELL0;
	}

	private void findSolutionsInEdges() {

	}

	private void findSolutionsInBox() {

	}

	private void linkSolutions() {

	}

	@Override
	public String toString() {
		return "CurvePlotContext{" +
				"boundingBox=" + boundingBox +
				", polynomial=" + polynomial +
//				", dx=" + dx +
//				", dy=" + dy +
				", hasSolution: " + polynomial.hasNoSolution() +
				", contextCass=" + contextCass +
				'}';
	}
}
