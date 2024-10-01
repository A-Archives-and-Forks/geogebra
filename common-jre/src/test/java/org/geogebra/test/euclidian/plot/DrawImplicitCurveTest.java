package org.geogebra.test.euclidian.plot;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawImplicitCurve;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.junit.Test;

public class DrawImplicitCurveTest extends BaseUnitTest {

	public static final String SRC_TEST_RESOURCES = "src/test/resources";
	private boolean save = false;

	@Test
	public void testImplicitCurvesPlotTheSame() {
		GeoImplicit geo = add("sin(x+y)-cos(x y)+1=0");
		final EuclidianView view = getApp().getActiveEuclidianView();
		final PathPlotterMock plotterMock = new PathPlotterMock();

		DrawImplicitCurve drawImplicitCurve = new DrawImplicitCurve(view, geo) {
			@Override
			protected GeneralPathClippedForCurvePlotter createPlotter() {
				return new GeneralPathClippedForCurvePlotterMock(view, plotterMock);
			}
		};
		geo.setEuclidianVisible(true);
		drawImplicitCurve.update();
		drawImplicitCurve.draw(view.getGraphicsForPen());
		if (save) {
			saveLog(plotterMock, "implicitPath.txt");
			return;
		}

		try {
			String expected = load("implicitPath.txt").trim();
			assertEquals(expected, plotterMock.result());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static void saveLog(PathPlotterMock plotterMock, String file) {
		try (PrintWriter out = new PrintWriter(SRC_TEST_RESOURCES + "/" + file)) {
			out.println(plotterMock.result());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private String load(String filename) throws IOException {
		Path filePath = Paths.get(SRC_TEST_RESOURCES, filename);
		return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
	}

}
