package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.CoordSystemAnimationListener;
import org.geogebra.common.euclidian.CoordSystemInfo;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;

public class BernsteinPlotter implements CoordSystemAnimationListener {
	public static final boolean VISUAL_DEBUG_ENABLED = true;
	public static final int SMALLEST_BOX_IN_PIXELS = 10;
	public static final double SMALLEST_EDGE_IN_PIXELS = 2;
	private final GeoElement curve;
	private final EuclidianViewBounds bounds;
	private final GeneralPathClippedForCurvePlotter gp;
	private final BernsteinPolynomialConverter converter;
	private final BernsteinPlotterVisualDebug visualDebug;
	private final BernsteinImplicitAlgo algo;
	private final BernsteinCellGrid grid;
	private boolean updateEnabled =true;

	public BernsteinPlotter(GeoElement curve, EuclidianViewBounds bounds,
			GeneralPathClippedForCurvePlotter gp) {
		this.curve = curve;
		this.bounds = bounds;
		this.gp = gp;
		grid = new BernsteinCellGrid();
		algo = new BernsteinImplicitAlgo(grid, bounds);
		converter = new BernsteinPolynomialConverter();
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug = new BernsteinPlotterVisualDebug(bounds);
		}
	}

	public void draw(GGraphics2D g2) {
		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.draw(g2);
		}

		if (updateEnabled) {
			update();
		}

		drawResults(g2);

	}

	public void update() {
		grid.resize(bounds);

		List<BernsteinPlotCell> cells = initialSplit(createRootCell());

		cells.forEach(algo::findSolutions);

		if (VISUAL_DEBUG_ENABLED) {
			visualDebug.setCells(grid.toList());
		}
	}

	private BernsteinPlotCell createRootCell() {
		BoundsRectangle limits = new BoundsRectangle(bounds);
		BernsteinPolynomial polynomial = converter.from(curve, limits);
		BernsteinBoundingBox box = new BernsteinBoundingBox(limits);
		return new BernsteinPlotCell(box, polynomial);
	}

	private List<BernsteinPlotCell> initialSplit(BernsteinPlotCell rootCell) {
		List<BernsteinPlotCell> list = new ArrayList<>();
		Collections.addAll(list, rootCell.split());
		return cellsWithPossibleSolution(list);
	}

	private void drawResults(GGraphics2D g2) {
		gp.reset();

		g2.setColor(GColor.BLUE);

		for (GPoint2D p : grid.toPointList()) {
			drawPointToScreen(p);
		}

		g2.draw(gp);
		gp.reset();

	}

	private void drawPointToScreen(GPoint2D p) {
		gp.moveTo((int) bounds.toScreenCoordXd(p.x), (int) bounds.toScreenCoordYd(p.y));
		gp.lineTo((int) bounds.toScreenCoordXd(p.x),
				(int) bounds.toScreenCoordYd(p.y));
	}


	private static List<BernsteinPlotCell> cellsWithPossibleSolution(List<BernsteinPlotCell> list) {
		return list.stream().filter(BernsteinPlotCell::mightHaveSolution)
				.collect(Collectors.toList());
	}

	public int plotCellCount() {
		return grid.toList().size();
	}


	@Override
	public void onZoomStop(CoordSystemInfo info) {
		updateEnabled = true;
	}

	@Override
	public void onMove(CoordSystemInfo info) {
		updateEnabled = false;
	}

	@Override
	public void onMoveStop() {
		updateEnabled = true;
	}
}
