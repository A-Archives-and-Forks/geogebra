package org.geogebra.web.full.gui.util;

import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Unit;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLElement;

import jsinterop.base.Js;

/**
 * Resizing utilities
 */
public class Resizer {

	/**
	 * @param element
	 *            element
	 * @param width
	 *            pixel width
	 */
	public static void setPixelWidth(elemental2.dom.Element element, int width) {
		if (element != null) {
			Js.<HTMLElement>uncheckedCast(element).style.width = CSSProperties.WidthUnionType.of(width + "px");
		}
	}

	/**
	 * @param element
	 *            element
	 * @param height
	 *            pixel height
	 */
	public static void setPixelHeight(elemental2.dom.Element element, int height) {
		if (element != null) {
			Js.<HTMLElement>uncheckedCast(element).style.height = CSSProperties.HeightUnionType.of(height + "px");
		}
	}

}
