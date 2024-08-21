package org.geogebra.common.euclidian.plot.implicit;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;

public class CurvePlotContext implements Splittable<CurvePlotContext> {
	CurvePlotBoundingBox boundingBox;
	BernsteinPolynomial polynomial;
	private BernsteinPolynomial dx;
	private BernsteinPolynomial dy;
	private ContextCass contextCass = ContextCass.NONE;

	ContextCass getContextCass() {
		return contextCass;
	}

	public void markCellUnused() {
		contextCass = ContextCass.NONE;
	}


	enum ContextCass {
		NONE,
		CELL0,
		CELL1,
		CELL2;

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

	private ContextCass classify() {
		if (polynomial.hasNoSolution()) {
			return ContextCass.CELL2;
		}

		if (dx.hasNoSolution() != dy.hasNoSolution()) {
			return ContextCass.CELL1;
		}

		return ContextCass.CELL0;
	}



	List<BoxEdge> createEdges() {
		BoxEdge top  = BoxEdge.create(polynomial, boundingBox.getX1(),
				boundingBox.getX2(),
				boundingBox.getY1(), EdgeKind.TOP);

		BoxEdge left = BoxEdge.create(polynomial, boundingBox.getY1(), boundingBox.getY2(),
				boundingBox.getX1(), EdgeKind.LEFT);

		BoxEdge bottom  = BoxEdge.create(polynomial, boundingBox.getX1(),
				boundingBox.getX2(),
				boundingBox.getY2(), EdgeKind.BOTTOM);

		BoxEdge right = BoxEdge.create(polynomial, boundingBox.getY1(), boundingBox.getY2(),
				boundingBox.getX2(), EdgeKind.RIGHT);

		return Arrays.asList(top, left, bottom, right);
//		return Arrays.asList(bottom, right);
	}

	@Override
	public String toString() {
		return "CurvePlotContext{" +
				"boundingBox=" + boundingBox +
				", polynomial=" + polynomial +
				", contextCass=" + contextCass +
				'}';
	}

	public boolean mightHaveSolution() {
		return contextCass != ContextCass.CELL2;
	}
}
