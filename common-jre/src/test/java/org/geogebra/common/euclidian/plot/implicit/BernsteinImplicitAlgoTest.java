package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.junit.Test;

public class BernsteinImplicitAlgoTest extends BaseUnitTest {
	@Test
	public void name() {
		EuclidianView view = getApp().getEuclidianView1();
		EuclidianViewBoundsMock bounds
				= new EuclidianViewBoundsMock(-2, 2, -2, 2) {
			@Override
			public double toScreenCoordXd(double x) {
				return view.toScreenCoordXd(x);
			}

			@Override
			public double toScreenCoordYd(double y) {
				return view.toScreenCoordYd(y);
			}


		};
		bounds.setSize(800, 600);
		GeoImplicitCurve curve = add("(3x^2 - y^2)^2 * y^2 - (x^2 + y^2)^4 = 0");
		List<MyPoint> points = new ArrayList<>();
		List<BernsteinPlotCell> cells = new ArrayList<>();
		BernsteinPlotterSettings settings =
				new BernsteinPlotterDefaultSettings();
		BernsteinImplicitAlgo algo =
				new BernsteinImplicitAlgo(bounds, curve, points, cells, settings);
		algo.compute();
		List<BernsteinPlotCell> emptyCells =
				cells.stream().filter(t -> t.getRectConfig() == BernsteinEdgeConfig.EMPTY).collect(
						Collectors.toList());
		assertEquals(Collections.emptyList(), cells);
	}
}
