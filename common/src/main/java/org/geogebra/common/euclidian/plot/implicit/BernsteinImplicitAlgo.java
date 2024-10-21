package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.LinkSegments;
import org.geogebra.common.util.debug.Log;

public class BernsteinImplicitAlgo implements PlotterAlgo {

	public static final BoundsRectangle UNIT_SQUARE = new BoundsRectangle(0, 1, 0, 1);
	private final EuclidianViewBounds bounds;
	private final GeoElement curve;
	private final List<MyPoint> points;
	private final List<BernsteinPlotCell> cells;
	private final BernsteinPlotterSettings settings;
	private final BernsteinPolynomialConverter converter;
	private final LinkSegments segments;
	BernsteinPolynomial polynomial;

	/**
	 * @param grid {@link CellGrid}
	 * @param bounds {@link EuclidianViewBounds}
	 * @param curve the curve geo.
	 * @param cells
	 * @param settings
	 */
	public BernsteinImplicitAlgo(EuclidianViewBounds bounds, GeoElement curve, List<MyPoint> points,
			List<BernsteinPlotCell> cells, BernsteinPlotterSettings settings) {
		this.bounds = bounds;
		this.curve = curve;
		this.points = points;
		this.cells = cells;
		this.settings = settings;
		converter = new BernsteinPolynomialConverter();
		segments = new LinkSegments(points);
	}

	@Override
	public void compute() {
		Log.debug("compute");
		List<BernsteinPlotCell> cells = initialSplit(createRootCell());
		cells.forEach(this::findSolutions);
		segments.flush();
	}

	private List<BernsteinPlotCell> initialSplit(BernsteinPlotCell rootCell) {
		List<BernsteinPlotCell> list = new ArrayList<>();
		Collections.addAll(list, rootCell.split());
		return cellsWithPossibleSolution(list);
	}

	private BernsteinPlotCell createRootCell() {
		BoundsRectangle limits = new BoundsRectangle(bounds);
		polynomial = converter.from(curve, limits);
		BernsteinBoundingBox box = new BernsteinBoundingBox(limits);
		return new BernsteinPlotCell(box, polynomial);
	}

	private void findSolutions(BernsteinPlotCell cell) {
		findSolutionsInFaces(cell);
	}

	private static List<BernsteinPlotCell> cellsWithPossibleSolution(List<BernsteinPlotCell> list) {
		return list.stream().filter(BernsteinPlotCell::mightHaveSolution)
				.collect(Collectors.toList());
	}

	private void findSolutionsInFaces(BernsteinPlotCell cell) {
		// Stack to replace recursion
		Stack<BernsteinPlotCell> stack = new Stack<>();
		stack.push(cell);

		while (!stack.isEmpty()) {
			BernsteinPlotCell currentCell = stack.pop();

			if (!currentCell.mightHaveSolution()) {
				return;
			}

			BernsteinPlotRectConfigProvider provider =
					new BernsteinPlotRectConfigProvider(currentCell);
			BernsteinPlotRect rect =
					new BernsteinPlotRect(currentCell, polynomial);
			BernsteinRectConfig config =
					BernsteinRectConfig.fromFlag(BernsteinPlotRectConfigProvider.config(rect));

			currentCell.setRectConfig(config);

			if (isBoxSmallEnough(currentCell.boundingBox, config)) {
				addToOutput(currentCell, rect, provider);
			} else {
				for (BernsteinPlotCell c : currentCell.split()) {
					if (c.mightHaveSolution()) {
						stack.push(c);
					}
				}
			}
		}
	}

	private void addToOutput(BernsteinPlotCell currentCell, BernsteinPlotRect rect,
			BernsteinPlotRectConfigProvider provider) {
		findSolutionsInEdges(currentCell);
		segments.add(rect, provider);
		cells.add(currentCell);
	}

	private boolean isBoxSmallEnough(BernsteinBoundingBox box, BernsteinRectConfig config) {
		double width = bounds.toScreenCoordXd(box.x2()) - bounds.toScreenCoordXd(box.x1());
		double height = bounds.toScreenCoordYd(box.y1()) - bounds.toScreenCoordYd(box.y2());
		int maxWidth = settings.minBoxWidthInPixels();
		int maxHeight = settings.minBoxHeightInPixels();
		if (config == BernsteinRectConfig.T1111) {
			maxWidth /= 4;
			maxHeight /= 4;
		}
		return width < maxWidth	&& height < maxHeight;
	}

	@SuppressWarnings("unused")
	private void findSolutionsInEdges(BernsteinPlotCell cell) {
		cell.createEdges();
		for (BernsteinPlotCellEdge edge : cell.getEdges()) {
			if (edge.mightHaveSolutions()) {
				findSolutionsInOneEdge(edge);
			}
		}
	}

	private void findSolutionsInOneEdge(BernsteinPlotCellEdge startEdge) {
		Stack<BernsteinPlotCellEdge> stack = new Stack<>();
		stack.push(startEdge);
		BernsteinPlotCellEdge parent = startEdge;
		while (!stack.isEmpty()) {
			BernsteinPlotCellEdge edge = stack.pop();
			if (edge.isDerivativeSignDiffer()) {
				BernsteinPlotCellEdge[] split = edge.split();
				stack.push(split[0]);
				stack.push(split[1]);
			} else {
				findSignChangeInEdge(edge, parent);
			}
		}

	}

	private void findSignChangeInEdge(BernsteinPlotCellEdge startEdge, BernsteinPlotCellEdge parent) {
		Stack<BernsteinPlotCellEdge> stack = new Stack<>();
		stack.push(startEdge);
		while (!stack.isEmpty()) {
			BernsteinPlotCellEdge edge = stack.pop();
			if (edge.mightHaveSolutions() && isEdgeSmallEnough(edge)) {
				if (edge.hasIntersect()) {
					edge.markSolution();
				}
				return;
			}

			BernsteinPlotCellEdge[] split = edge.split();
			if (split[0].mightHaveSolutions()) {
				stack.push(split[0]);
			}
			if (split[1].mightHaveSolutions()) {
				stack.push(split[1]);
			}
		}
	}

	private boolean isEdgeSmallEnough(BernsteinPlotCellEdge edge) {
		double x1 = edge.startPoint().x;
		double x2 = x1 + edge.length();
		double width = edge.isHorizontal()
				? bounds.toScreenCoordXd(x2) - bounds.toScreenCoordXd(x1)
				: bounds.toScreenCoordYd(x1) - bounds.toScreenCoordYd(x2);
		return width < settings.minEdgeWidth();
	}
}
