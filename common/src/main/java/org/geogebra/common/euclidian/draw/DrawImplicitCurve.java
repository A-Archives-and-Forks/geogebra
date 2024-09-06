/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.implicit.BernsteinPlotter;
import org.geogebra.common.euclidian.plot.implicit.CoordSystemAnimatedPlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.implicit.GeoImplicit;

/**
 * Draw GeoImplicitCurve on euclidian view
 */
public class DrawImplicitCurve extends DrawLocus {

	public static final boolean BERNSTEIN_BASED_PLOTTER = true;
	private CoordSystemAnimatedPlotter bernsteinPlotter;
	private final GeoImplicit implicitCurve;
	private final boolean isBernsteinBasedPlotter;

	// private int fillSign; //0=>no filling, only curve -1=>fill the negativ
	// part, 1=>fill positiv part

	/**
	 * Creates new drawable for implicit Curvenomial
	 * 
	 * @param view
	 *            view
	 * @param implicitCurve
	 *            implicit Curvenomial
	 */
	public DrawImplicitCurve(EuclidianView view, GeoImplicit implicitCurve) {
		super(view, implicitCurve.getLocus(),
				implicitCurve.getTransformedCoordSys());
		this.view = view;
		this.implicitCurve = implicitCurve;
		this.geo = implicitCurve.toGeoElement();
		isBernsteinBasedPlotter = BERNSTEIN_BASED_PLOTTER
				&& BernsteinPolynomialConverter.iSupported(geo);

		if (isBernsteinBasedPlotter) {
			createBernsteinPlotter();
		} else {
			update();
		}
	}

	private void createBernsteinPlotter() {
		bernsteinPlotter = new BernsteinPlotter(geo, new EuclidianViewBoundsImp(view),
				new GeneralPathClippedForCurvePlotter(view));

		view.getEuclidianController()
				.addZoomerAnimationListener(bernsteinPlotter, geo);
	}

	@Override
	protected void drawLocus(GGraphics2D g2) {
		if (isBernsteinBasedPlotter) {
			bernsteinPlotter.draw(g2);
		} else {
			super.drawLocus(g2);
		}
	}

	@Override
	public GArea getShape() {
		return AwtFactory.getPrototype().newArea();
	}

	/**
	 * Returns the Curve to be draw (might not be equal to geo, if this is part
	 * of bigger geo)
	 * 
	 * @return Curve
	 */
	public GeoImplicit getCurve() {
		return implicitCurve;
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (!implicitCurve.isDefined()) {
			return false;
		}
		return super.hit(x, y, hitThreshold);
	}

	@Override
	protected void ensureLocusUpdated() {
		if (isBernsteinBasedPlotter) {
			return;
		}

		implicitCurve.getLocus();
	}

	@Override
	protected void updateAlgos() {
		// nothing to do
	}
}
