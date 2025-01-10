package org.geogebra.common.main;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.junit.Before;
import org.junit.Test;

public class EuclidianSettingsTest extends BaseUnitTest {
	private static EuclidianSettings settings;

	@Before
	public void setUp() {
		settings = new EuclidianSettings(getApp());
	}

	@Test
	public void isometricBackgroundShouldShowGrid() {
		assertGridAt(BackgroundType.ISOMETRIC);
	}

	private static void assertGridAt(BackgroundType backgroundType) {
		settings.setBackgroundType(backgroundType);
		assertTrue(settings.getShowGrid());
	}

	@Test
	public void polarBackgroundShouldShowGrid() {
		assertGridAt(BackgroundType.POLAR);
	}

	@Test
	public void changeBackgroundFromIsometricShouldHideGrid() {
		changeBackgroundShouldHideGridFrom(BackgroundType.ISOMETRIC);
	}

	@Test
	public void changeBackgroundFromPolarShouldHideGrid() {
		changeBackgroundShouldHideGridFrom(BackgroundType.POLAR);
	}

	private void changeBackgroundShouldHideGridFrom(BackgroundType backgroundType) {
		for (BackgroundType type: BackgroundType.values()) {
			if (noGridBackground(type)) {
				changeShouldHideGrid(backgroundType, type);
			}
		}
	}

	private static boolean noGridBackground(BackgroundType type) {
		return type != BackgroundType.ISOMETRIC && type != BackgroundType.POLAR;
	}

	private void changeShouldHideGrid(BackgroundType before, BackgroundType after) {
		assertGridAt(before);
		assertNoGridAt(after);
	}

	private static void assertNoGridAt(BackgroundType backgroundType) {
		settings.setBackgroundType(backgroundType);
		assertFalse(settings.getShowGrid());
	}

	@Test
	public void noBackgroundShouldClearShowGrid() {
		settings.setBackgroundType(BackgroundType.ISOMETRIC);
		settings.setBackgroundType(BackgroundType.NONE);
		assertFalse(settings.getShowGrid());
	}
}
