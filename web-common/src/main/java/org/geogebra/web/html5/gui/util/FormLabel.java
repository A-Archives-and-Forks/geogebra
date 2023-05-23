package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.StringUtil;
import org.gwtproject.dom.client.LabelElement;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.UIObject;

import elemental2.dom.HTMLElement;

/**
 * Label for form elements
 * 
 * @author zbynek
 */
public class FormLabel extends FlowPanel {
	/**
	 * Interface for objects that wrap an input element
	 */
	public interface HasInputElement {
		/**
		 * @return wrapped input element
		 */
		HTMLElement getInputElement();
	}

	/**
	 * @param string
	 *            plain text content
	 */
	public FormLabel(String string) {
		this();
		getElement().textContent = string;
	}

	/**
	 * Create empty form label
	 */
	public FormLabel() {
		super(LabelElement.TAG);
		addStyleName("gwt-Label");
	}

	/**
	 * @param string
	 *            (plain) text content
	 */
	public void setText(String string) {
		getElement().textContent = string;
	}

	/**
	 * @param ui
	 *            UI element to be labeled by this
	 * @return this
	 */
	public FormLabel setFor(UIObject ui) {
		HTMLElement target = ui.getElement();
		if (ui instanceof HasInputElement) {
			target = ((HasInputElement) ui).getInputElement();
		}
		if (StringUtil.empty(target.id)) {
			target.id = DOM.createUniqueId();
		}
		getElement().setAttribute("for", target.id);
		return this;
	}
}
