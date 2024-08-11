package org.geogebra.common.euclidian.plot.implicit;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.Splittable;
import org.geogebra.common.util.debug.Log;

public class CurvePlotContext implements Splittable<CurvePlotContext> {
	CurvePlotBoundingBox boundingBox;
	BernsteinPolynomial polynomial;
	private BernsteinPolynomial dx;
	private BernsteinPolynomial dy;
	private ContextCass contextCass = ContextCass.NONE;

	public ContextCass getContextCass() {
		return contextCass;
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

	public void process() {
		List<BernsteinPolynomial> edges = createEdgePolynomials();
		//findSolutionsInEdges(edges.toArray(new BernsteinPolynomial[0]));
		findSolutionsInBox();
		linkSolutions();
	}


	private ContextCass classify() {
		if (polynomial.hasNoSolution()) {
			return ContextCass.CELL2;
		}

		if (!dx.hasNoSolution() && !dy.hasNoSolution()) {
			return ContextCass.CELL0;
		}

		return contextCass = ContextCass.CELL1;
	}

	private void findSolutionsInEdges(BernsteinPolynomial[] edges) {
		for (BernsteinPolynomial edge: edges) {
			findSolutionsInOneEdge(edge);
		}
	}

	private void findSolutionsInOneEdge(BernsteinPolynomial edge) {
			BernsteinPolynomial dx = edge.derivative("x");
			BernsteinPolynomial dy = edge.derivative("y");
			if (dx.getSign() != dy.getSign()) {
				BernsteinPolynomial[] split = edge.split();
				findSolutionsInOneEdge(split[0]);
				findSolutionsInOneEdge(split[1]);
	    	} else {
				findSignChangeInEdge(edge);
			}
	}

	private void findSignChangeInEdge(BernsteinPolynomial edge) {
		BernsteinPolynomial[] split = edge.split();
		if (split[0].hasNoSolution()) {
			splitIfNeeded(split[1]);
		} else {
			splitIfNeeded(split[0]);
		}

		Log.debug("findSignChangeInEdge");
	}

	private void splitIfNeeded(BernsteinPolynomial edge) {
		if (true) {
			findSignChangeInEdge(edge);
		}

	}


	private List<BernsteinPolynomial> createEdgePolynomials() {
		List<BernsteinPolynomial> list = Arrays.asList(
			polynomial.substitute("y", boundingBox.getYmin()),
			polynomial.substitute("y", boundingBox.getYmax()),
			polynomial.substitute("x", boundingBox.getXmin()),
			polynomial.substitute("x", boundingBox.getXmax())
		);

		return list.stream().filter(polynomial -> !polynomial.hasNoSolution())
				.collect(Collectors.toList());
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
				", contextCass=" + contextCass +
				'}';
	}

	public boolean mightHaveSolution() {
		return contextCass != ContextCass.CELL2;
	}
}
