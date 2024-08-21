package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Before;
import org.junit.Test;

public class ImplicitCurvePlotterTest extends BaseUnitTest {
	private ImplicitCurvePlotter plotter;
	private EuclidianView view;
	private EuclidianViewBoundsImp bounds;

	@Before
	public void setUp() {
		view = getApp().getActiveEuclidianView();
		bounds = new EuclidianViewBoundsImp(view);
	}

	@Test
	public void testUpdate() {
		GeoElement curve = add("");
		plotter = new ImplicitCurvePlotter(curve, bounds);
		plotter.update();
		assertEquals(1024, plotter.subContentCount());
	}

}
