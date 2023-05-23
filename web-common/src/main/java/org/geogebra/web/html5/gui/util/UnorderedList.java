package org.geogebra.web.html5.gui.util;

import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.ComplexPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.HTMLElement;

/**
 * Wrapper for the &lt;UL&rt; tag
 */
public class UnorderedList extends ComplexPanel {

	/**
	 * Create new UL
	 */
	public UnorderedList() {
		setElement(DOM.createElement("UL"));
	}

	@Override
	public void add(Widget w) {
		HTMLElement el = getElement();
		add(w, el);
	}
}