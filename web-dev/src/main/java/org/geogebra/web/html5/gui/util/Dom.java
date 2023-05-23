package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.debug.Log;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.UIObject;

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

/**
 * Helper methods for finding DOM elements
 */
public final class Dom {
	private Dom() {
		// no public constructor
	}

	/**
	 * @param className
	 *            class name
	 * @return NodeList of elements found by className
	 */
	public static HTMLCollection<elemental2.dom.Element> getElementsByClassName(
	        String className) {
		return DomGlobal.document.getElementsByClassName(className);
	}

	/**
	 * @param selector
	 *            CSS selector
	 * @return first Element found by selector className
	 */
	public static Element querySelector(String selector) {
		return Js.uncheckedCast(DomGlobal.document.querySelector(selector));
	}

	/**
	 * @param elem
	 *            the root element
	 * @param selector
	 *            selector
	 * @return first Element found by selector className
	 */
	public static HTMLElement querySelectorForElement(Object elem,
			String selector) {
		elemental2.dom.Element parent = Js.uncheckedCast(elem);
		return Js.uncheckedCast(parent.querySelector(selector));
	}

	/**
	 * @param style
	 *            style
	 * @param property
	 *            property name
	 * @param val
	 *            property value
	 */
	public static void setImportant(CSSStyleDeclaration style, String property,
			String val) {
		style.setProperty(property, val, "important");
	}

	/**
	 * 
	 * @param event
	 *            a native event
	 * @param element
	 *            the element to be tested
	 * @return true iff event targets the element or its children
	 */
	public static boolean eventTargetsElement(elemental2.dom.Event event, HTMLElement element) {
		EventTarget target = event.target;
		if (DOM.isElement(target) && element != null) {
			return element.contains(Js.uncheckedCast(target));
		}
		return false;
	}

	/**
	 * @param ui
	 *            UI element
	 * @param className
	 *            CSS class
	 * @param add
	 *            whether to add or remove
	 */
	public static void toggleClass(UIObject ui, String className, boolean add) {
		if (add) {
			ui.getElement().classList.add(className);
		} else {
			ui.getElement().classList.remove(className);
		}
	}

	/**
	 * @param ui
	 *            UI element
	 * @param classTrue
	 *            CSS class when toggle is true
	 * @param classFalse
	 *            CSS class when toggle is false
	 * @param add
	 *            whether to add or remove
	 */
	public static void toggleClass(UIObject ui, String classTrue,
			String classFalse, boolean add) {
		toggleClass(ui.getElement(), classTrue, classFalse, add);
	}

	/**
	 * @param elem
	 *            HTML element
	 * @param classTrue
	 *            CSS class when toggle is true
	 * @param classFalse
	 *            CSS class when toggle is false
	 * @param add
	 *            whether to add or remove
	 */
	public static void toggleClass(HTMLElement elem, String classTrue,
			String classFalse, boolean add) {
		if (add) {
			elem.classList.add(classTrue);
			elem.classList.remove(classFalse);
		} else {
			elem.classList.remove(classTrue);
			elem.classList.add(classFalse);
		}
	}

	/**
	 * @return active element
	 */
	public static elemental2.dom.Element getActiveElement() {
		return DomGlobal.document.activeElement;
	}

	/**
	 * Element.addEventListener extracted to static method for safe cast in tests.
	 * @param element element
	 * @param name event name
	 * @param listener listener
	 */
	public static void addEventListener(elemental2.dom.Element element, String name, EventListener listener) {
		element.addEventListener(name, listener);
	}

	/**
	 * @param element element
	 * @param width CSS property name
	 * @return value, if it was a number in px; otherwise 0
	 */
	public static int getPxProperty(Element element, String width) {
		try {
			return Integer.parseInt(element.getStyle().getProperty(width)
					.replace("px", ""));
		} catch (RuntimeException ex) {
			Log.warn(ex.getMessage());
		}
		return 0;
	}

	public static HTMLImageElement createImage() {
		return (HTMLImageElement) DomGlobal.document.createElement("img");
	}

	/**
	 * @return create button with default type (not submitting)
	 */
	public static HTMLElement createDefaultButton() {
		HTMLElement btn = DOM.createElement("button");
		// avoid default "submit" behavior when GeoGebra is in a form
		btn.setAttribute("type", "button");
		return btn;
	}

	/**
	 * @param cls CSS class name
	 * @return div with given class
	 */
	public static HTMLElement createDiv(String cls) {
		HTMLElement div = Js.uncheckedCast(DomGlobal.document.createElement("div"));
		div.className = cls;
		return div;
	}

	public static CSSStyleDeclaration style(HTMLElement element) {
		return element.style;
	}
}
