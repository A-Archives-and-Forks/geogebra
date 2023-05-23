package org.geogebra.web.test;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.regexp.client.NativeRegExpFactory;
import org.geogebra.regexp.shared.RegExpFactory;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLElement;

public class DomMocker {

	/**
	 * @return element with mocked style
	 */
	public static GeoGebraElement getGeoGebraElement() {
		GeoGebraElement ge= spy(GeoGebraElement.as(DOM.createDiv()));
		RegExpFactory.setPrototypeIfNull(new NativeRegExpFactory());
		Mockito.doAnswer(invocation->1.0).when(ge).readScaleX();
		Mockito.doAnswer(invocation->1.0).when(ge).getScaleX();
		Mockito.doAnswer(invocation->1.0).when(ge).getScaleY();
		Mockito.doNothing().when(ge).resetScale();
		return ge;
	}

	/**
	 * @return element with consistent set/get behavior for attributes
	 */
	public static HTMLElement getElement() {
		HTMLElement element = mock(HTMLElement.class);
		final Map<String, String> attributes = new HashMap<>();
		Mockito.doAnswer((Answer<Void>) invocation -> {
			attributes.put(invocation.getArgumentAt(0, String.class),
					invocation.getArgumentAt(1, String.class));
			return null;
		}).when(element).setAttribute(Matchers.anyString(), Matchers.anyString());

		doAnswer((Answer<Void>) invocation -> {
			attributes.put("innerText", invocation.getArgumentAt(0, String.class));
			return null;
		}).when(element).textContent = Matchers.anyString();

		when(element.getAttribute(Matchers.anyString()))
				.thenAnswer((Answer<String>) invocation ->
								attributes.get(invocation.getArgumentAt(0, String.class)));
		CSSStyleDeclaration mockStyle = mock(CSSStyleDeclaration.class);
		element.style = mockStyle;
		return element;
	}

	/**
	 * @param button widget
	 * @param <T> widget type
	 * @return widget with consistent backing element
	 */
	public static <T extends Widget> T withElement(T button) {
		T mock = spy(button);
		HTMLElement element = DomMocker.getElement();
		when(mock.getElement()).thenReturn(element);
		return mock;
	}

	/**
	 * @return label with consistent backing element
	 */
	public static Label newLabel() {
		Label lbl = withElement(new Label());
		bypassSetTextMethod(lbl);
		return lbl;
	}

	private static void bypassSetTextMethod(final Label lbl) {
		doAnswer((Answer<Void>) invocation -> {
			lbl.getElement().textContent = (invocation.getArgumentAt(0, String.class));
			return null;
		}).when(lbl).setText(Matchers.anyString());
	}
}
