package org.geogebra.common.kernel.geos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoAngleTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GEOMETRY);
	}

	@Test
	public void testCopy() {
		GeoAngle angle = evaluateGeoElement("90°");
		angle.setDrawable(true, false);
		GeoAngle copy = angle.copy();
		assertTrue(copy.isDrawable, "Copied angle should be drawable");
	}

	@Test
	public void testSetAllVisualPropertiesExceptEuclidianVisible() {
		GeoAngle hidden = evaluateGeoElement("90°");
		hidden.setDrawable(false, false);
		GeoAngle visible = evaluateGeoElement("90°");
		visible.setDrawable(true, false);
		visible.setAllVisualPropertiesExceptEuclidianVisible(
				hidden, false, true);
		assertTrue(visible.isDrawable, "Angle with copied style should be drawable");
	}

	@ParameterizedTest
	@Issue("APPS-6681")
	@ValueSource(strings = {"Angle((1,0),(0,0),(0,-1))", "Angle((0,-1))"})
	public void testValueString(String command) {
		getApp().setGeometryConfig();
		getKernel().getConstruction().getConstructionDefaults()
				.createDefaultGeoElements();
		GeoAngle reflex = evaluateGeoElement(command);
		reflex.setAngleStyle(GeoAngle.AngleStyle.NOTREFLEX);
		assertEquals("90" + Unicode.DEGREE_STRING,
				reflex.toValueString(StringTemplate.defaultTemplate));
		reflex.setAngleStyle(GeoAngle.AngleStyle.ISREFLEX);
		assertEquals("270" + Unicode.DEGREE_STRING,
				reflex.toValueString(StringTemplate.defaultTemplate));
		reflex.setAngleStyle(GeoAngle.AngleStyle.ANTICLOCKWISE);
		assertEquals("270" + Unicode.DEGREE_STRING,
				reflex.toValueString(StringTemplate.defaultTemplate));
	}
}