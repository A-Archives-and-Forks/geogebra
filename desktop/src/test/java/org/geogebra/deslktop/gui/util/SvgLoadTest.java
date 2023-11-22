package org.geogebra.deslktop.gui.util;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.desktop.gui.util.JSVGImage;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Before;
import org.junit.Test;


public class SvgLoadTest extends BaseUnitTest {

	static AppDNoGui app;

	@Before
	public void setUp() {
		app = new AppDNoGui(new LocalizationD(3), false);
	}

	public static final String E2E_RESOURCES = "src/e2eTest/resources/";
	private static final String svgsBase64 = Base64.encodeToString(
			UtilD.loadFileIntoByteArray(E2E_RESOURCES + "svgs.ggb"),
			false);

	@Test
	public void testLoadSvgs() {
		app.getGgbApi().setBase64(svgsBase64);
	}

	@Test
	public void testLoadWhiteListIssue() {
		load("issue41.svg");
	}

	private static void load(String svg) {
		try {
			JSVGImage image = JSVGImage.fromFile(new File(E2E_RESOURCES + svg));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testLoad2() {
		load("2.svg");

	}
}
