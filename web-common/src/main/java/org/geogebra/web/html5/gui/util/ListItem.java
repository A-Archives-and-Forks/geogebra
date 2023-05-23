package org.geogebra.web.html5.gui.util;

import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.ComplexPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.HTMLElement;

/**
 * Panel based on li tag
 */
public class ListItem extends ComplexPanel {

	/**
	 * New list element panel
	 */
	public ListItem() {
		HTMLElement el = DOM.createElement("LI");
		setElement(el);
		el.tabIndex = -1;
	}

	@Override
	public void add(Widget w) {
		HTMLElement el = getElement();
		add(w, el);
	}

	/**
	 * Focus or blur this.
	 * 
	 * @param focused
	 *            whether to focus
	 */
	public void setFocus(boolean focused) {
		if (focused) {
			getElement().focus();
		} else {
			getElement().blur();
		}
	}

	public void setText(String text) {
		getElement().textContent = text;
	}
}