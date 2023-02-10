package org.geogebra.common.main;

import static org.geogebra.test.OrderingComparison.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.AutoColor;
import org.junit.Test;

public class GColorTest {

	@Test
	public void colorParserShouldAcceptRGB() {
		GColor color = GColor.parseHexColor("#123");
		assertEquals(16, color.getRed());
		assertEquals(32, color.getGreen());
		assertEquals(48, color.getBlue());
		assertEquals(255, color.getAlpha());
	}

	@Test
	public void colorParserShouldAcceptRGBA() {
		GColor color = GColor.parseHexColor("#1234");
		assertEquals(16, color.getRed());
		assertEquals(32, color.getGreen());
		assertEquals(48, color.getBlue());
		assertEquals(64, color.getAlpha());
	}

	@Test
	public void colorParserShouldAcceptRRGGBB() {
		GColor color = GColor.parseHexColor("#010203");
		assertEquals(1, color.getRed());
		assertEquals(2, color.getGreen());
		assertEquals(3, color.getBlue());
		assertEquals(255, color.getAlpha());
	}

	@Test
	public void colorParserShouldAcceptRRGGBBAA() {
		GColor color = GColor.parseHexColor("#01020304");
		assertEquals(1, color.getRed());
		assertEquals(2, color.getGreen());
		assertEquals(3, color.getBlue());
		assertEquals(4, color.getAlpha());
	}

	@Test
	public void testLuminance() {
		assertEquals(1.0, GColor.WHITE.getLuminance(), 0.01);
		assertEquals(0, GColor.BLACK.getLuminance(), 0.01);
		assertEquals(0.0722, GColor.BLUE.getLuminance(), 0.01);
		assertEquals(0.2126, GColor.RED.getLuminance(), 0.01);
		assertEquals(0.7152, GColor.GREEN.getLuminance(), 0.01);
	}

	@Test
	public void testContrast() {
		assertEquals(21.0, GColor.WHITE.getContrast(GColor.BLACK), 0.01);
		assertEquals(21.0, GColor.BLACK.getContrast(GColor.WHITE), 0.01);
		for (int i = 0; i < 10; i++) {
			GColor objColor = AutoColor.CURVES.getNext(true);
			assertThat(objColor.getContrast(GColor.WHITE), greaterThan(3.0));
		}
	}
}
