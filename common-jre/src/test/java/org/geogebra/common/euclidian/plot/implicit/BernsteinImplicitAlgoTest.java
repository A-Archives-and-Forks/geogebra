package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.junit.Ignore;
import org.junit.Test;

public class BernsteinImplicitAlgoTest extends BaseUnitTest {
	@Ignore
	@Test
	public void name() {
		EuclidianView view = getApp().getEuclidianView1();
		view.setPreferredSize(AwtFactory.getPrototype().newDimension(1280, 1920));
		view.setXmin(-0.048);
		view.setXmax(0.048);
		view.setYmax(1.0);
		view.setYmin(-1.0);
		EuclidianViewBounds bounds = new EuclidianViewBoundsImp(view);
		GeoImplicitCurve curve = add("(3x^2 - y^2)^2 * y^2 - (x^2 + y^2)^4 = 0");
		List<MyPoint> points = new ArrayList<>();
		List<BernsteinPlotCell> cells = new ArrayList<>();
		BernsteinPlotterSettings settings =
				new BernsteinPlotterDefaultSettings();
		BernsteinImplicitAlgo algo =
				new BernsteinImplicitAlgo(bounds, curve, points, cells, settings.getAlgoSettings());
		algo.compute();
		BernsteinPlotCell cell0 = cells.get(0);
		BernsteinPlotRect rect = new BernsteinPlotRect(cell0, null);
		BernsteinPlotRectConfigProvider provider =
				new BernsteinPlotRectConfigProvider(cell0);
		BernsteinRectConfig config = provider.getConfigFromPlotRect(rect);

		assertEquals(BernsteinRectConfig.T1100, config);
	}
}
