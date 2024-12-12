package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RealSchuleExamRestrictionsTest extends BaseExamTests {

	private Settings settings;
	private EuclidianSettings evSettings;

	@BeforeEach
	public void setupExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		settings = app.getSettings();
		evSettings = settings.getEuclidian(1);
	}

	@Test
	public void testSettingsRestrictions() {
		createDefaultSetting();
		startExam();
		realSchuleRestrictionsShouldBeApplied();
		finishExam();
		defaultSettingsShouldBeRestored();
	}

	private void defaultSettingsShouldBeRestored() {
		coordFormatShouldBe(Kernel.COORD_CARTESIAN);
		axisLabelsShouldBe("xAxis", "yAxis");
		gridShouldBe(EuclidianView.GRID_ISOMETRIC);
		axisNumberDistanceShouldBe(1.5, 0);
		axisNumberDistanceShouldBe(4.1, 1);
	}

	private void createDefaultSetting() {
		settings.getGeneral().setCoordFormat(Kernel.COORD_CARTESIAN);
		evSettings.setAxisLabel(0, "xAxis");
		evSettings.setAxisLabel(1, "yAxis");
		evSettings.setAxisNumberingDistance(0, 1.5);
		evSettings.setAxisNumberingDistance(1, 4.1);
		evSettings.setGridType(EuclidianView.GRID_ISOMETRIC);
	}

	private void realSchuleRestrictionsShouldBeApplied() {
		coordFormatShouldBe(Kernel.COORD_STYLE_AUSTRIAN);
		axisLabelsShouldBe("x", "y");
		gridShouldBe(EuclidianView.GRID_CARTESIAN);
		axisNumberDistanceShouldBe(0.5, 0);
		axisNumberDistanceShouldBe(0.5, 1);
	}

	private void startExam() {
		examController.startExam(ExamType.REALSCHULE, null);
	}

	private void finishExam() {
		examController.finishExam();
		examController.exitExam();
	}

	private void gridShouldBe(int expected) {
		assertEquals(expected, evSettings.getGridType());
	}

	private void axisLabelsShouldBe(String... labels) {
		if (labels.length != 2) {
			fail();
		}
		String[] axesLabels = evSettings.getAxesLabels();
		assertEquals(labels[0], axesLabels[0]);
		assertEquals(labels[1], axesLabels[1]);
	}

	private void coordFormatShouldBe(int expected) {
		assertEquals(expected, settings.getGeneral().getCoordFormat());
	}

	private void axisNumberDistanceShouldBe(double expected, int axisNumber) {
		assertEquals(expected, evSettings.getAxisNumberingDistance(axisNumber).evaluateDouble(),
				0);
	}

	@Test
	public void testSettingsRestrictionsAfterFileNew() {
		startExam();
		realSchuleRestrictionsShouldBeApplied();

		// emulating AppW.fileNew(), which is not reachable from common.
		evSettings.reset();
		examController.reapplySettingsRestrictions();

		realSchuleRestrictionsShouldBeApplied();
	}
}
