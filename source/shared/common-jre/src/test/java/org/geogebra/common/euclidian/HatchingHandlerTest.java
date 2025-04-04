package org.geogebra.common.euclidian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HatchingHandlerTest {

	private AppCommon app;
	private HatchingHandler hatchingHandler;

	@BeforeEach
	public void setup() {
		app = Mockito.spy(AppCommonFactory.create());
		when(app.isHTML5Applet()).thenReturn(true);
		EuclidianView view = app.getActiveEuclidianView();
		hatchingHandler = new HatchingHandler(view);
	}

	/** Test for APPS-4819 */
	@Test
	public void symbolDimensions() {
		for (Integer size: Arrays.asList(27, 28, 29)) {
			AwtFactoryCommon.GTexturePaintCommon texture = getSymbolFill(app, size);
			assertEquals(27, texture.subImage.getHeight());
			assertEquals(27, texture.rect.getHeight(), 0.1);
		}
	}

	@Test
	public void testTextureTypes() {
		app.setExporting(App.ExportType.PNG, 2);
		GBasicStroke defObjStroke = AwtFactoryCommon.getPrototype().newBasicStroke(1);
		GPaint texture = hatchingHandler.getHatchingTexture(defObjStroke,
				null, null, .5, 1, 45, FillType.HATCH, null, app);
		assertEquals(AwtFactoryCommon.GTexturePaintCommon.class, texture.getClass());
		app.setExporting(App.ExportType.SVG, 2);
		texture = hatchingHandler.getHatchingTexture(defObjStroke,
				null, null, .5, 1, 45, FillType.HATCH, null, app);
		assertEquals(VectorPatternPaint.class, texture.getClass());
		app.setExporting(App.ExportType.PDF_HTML5, 2);
		texture = hatchingHandler.getHatchingTexture(defObjStroke,
				null, null, .5, 1, 45, FillType.HATCH, null, app);
		assertEquals(VectorPatternPaint.class, texture.getClass());
	}

	private AwtFactoryCommon.GTexturePaintCommon getSymbolFill(App app, int size) {
		return (AwtFactoryCommon.GTexturePaintCommon) hatchingHandler.getHatchingTexture(
				new DefaultBasicStroke(),
				GColor.GREEN, GColor.RED, 0, size / 2.5, 0,
				FillType.SYMBOLS, "X", app);
	}

}
