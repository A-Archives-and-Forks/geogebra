package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CSSStyleDeclaration;

/**
 * Creates widgets for navigating the construction with Voiceover (iOS) or
 * Talkback (Android)
 * 
 * @author Zbynek
 */
public class WidgetFactory {

	/**
	 * For sliders we want more restrictive hide than for other widgets
	 */
	private static void hideSlider(Widget ui) {
		CSSStyleDeclaration style = ui.getElement().style;
		style.setProperty("opacity", ".01");
		style.position = "fixed";
		style.setProperty("width", "1px");
		style.setProperty("height", "1px");
		style.overflow = "hidden";
	}

	/**
	 * @param index  slider identifier in case listener has more sliders
	 * @param source listener
	 * @return slider
	 */
	public static SliderW makeSlider(final int index, final HasSliders source,
			BaseWidgetFactory factory) {
		final SliderW range = factory.newSlider(0, 100);
		hideSlider(range);
		range.getElement().classList.add("slider");
		range.addValueChangeHandler(event -> source.onValueChange(index, event.getValue()));
		range.getElement().tabIndex = 5000;
		return range;
	}

}
