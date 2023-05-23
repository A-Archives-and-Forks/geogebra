package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.ElementalMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.user.client.ui.FlowPanel;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({JLMContext2d.class})
public class AccessibilityViewTest {

	private AppW app;
	private AccessibilityView aView;
	private FlowPanel mockPanel;
	private BaseWidgetFactory baseWidgetFactory;

	@BeforeClass
	public static void setupDom() {
		ElementalMocker.setupElemental();
	}

	@Before
	public void setup() {
		baseWidgetFactory = getBaseWidgetFactory();
	}

	private void initAccessibilityViewFull() {
		app = AppMocker.mockGraphing();
		aView = new AccessibilityView(app, baseWidgetFactory);
		mockPanel = aView.getControls();
	}

	private void initAccessibilityViewSimple() {
		app = AppMocker.mockAppletSimple(new AppletParameters("simple"));
		aView = new AccessibilityView(app, baseWidgetFactory);
		mockPanel = aView.getControls();
	}

	@Test
	public void pointsShouldBeSliders() {
		initAccessibilityViewFull();
		add("(1,1)");
		add("(1,2,3)");
		assertDescription(0, "x coordinate of point a");
		assertDescription(1, "y coordinate of point a");
		assertDescription(2, "x coordinate of point b");
		assertDescription(3, "y coordinate of point b");
		assertDescription(4, "z coordinate of point b");
	}

	@Test
	public void buttonsShouldReadCaption() {
		initAccessibilityViewFull();
		GeoElement button = add("Button(\"Click Me\")").toGeoElement();
		button.remove();
		button.setClickScript(new GgbScript(app, "42"));
		button.setLabel("B");
		assertText(0, "click me");
	}

	@Test
	public void elementsShouldReadDescription() {
		initAccessibilityViewFull();
		add("\"legend\"");
		assertText(0, "legend");
	}

	@Test
	public void pointsShouldBeSlidersAppSimple() {
		initAccessibilityViewSimple();
		add("(1,1)");
		assertDescription(0, "x coordinate of point a");
		assertDescription(1, "y coordinate of point a");
	}

	@Test
	public void buttonsShouldReadCaptionAppSimple() {
		initAccessibilityViewSimple();
		GeoElement button = add("Button(\"Click Me\")").toGeoElement();
		button.remove();
		button.setClickScript(new GgbScript(app, "42"));
		button.setLabel("B");
		assertText(0, "click me");
	}

	@Test
	public void elementsShouldReadDescriptionAppSimple() {
		initAccessibilityViewSimple();
		add("\"legend\"");
		assertText(0, "legend");
	}

	private void assertDescription(int i, String string) {
		Assert.assertTrue(mockPanel.getWidgetCount() > i);
		MatcherAssert.assertThat(
				mockPanel.getWidget(i).getElement().getAttribute("aria-label").toLowerCase(),
				CoreMatchers.containsString(string));
	}

	private void assertText(int i, String string) {
		Assert.assertTrue(mockPanel.getWidgetCount() > i);
		MatcherAssert.assertThat(mockPanel.getWidget(i).getElement().textContent.toLowerCase(),
				CoreMatchers.containsString(string));
	}

	private GeoElementND add(String string) {
		return app.getKernel().getAlgebraProcessor().processAlgebraCommand(string, false)[0];
	}

	private BaseWidgetFactory getBaseWidgetFactory() {
		BaseWidgetFactory factory = new BaseWidgetFactory();


		return factory;
	}
}
