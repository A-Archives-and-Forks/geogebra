package org.geogebra.web.full.gui.util;

import org.geogebra.gwtutil.NavigatorUtil;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTextAreaElement;
import jsinterop.base.Js;

public class AdvancedFocusPanel extends SimplePanel implements AdvancedFocusPanelI {

	HTMLTextAreaElement focusTextarea;
	boolean disabledTextarea;

	/**
	 * Create new focus panel
	 */
	public AdvancedFocusPanel() {
		// Here it is not the getContainerElement()
		// that shall get the focus, but another element!

		// In theory, getContainerElement() will play the role
		// of parent element of any widget added to this panel;
		// which should be the same as getElement(), the
		// DOM representation of AdvancedFocusPanel when added
		// to other panels... so it makes no sense changing
		// any of these to textarea, but instead, we shall add
		// the textarea as a child element of:
		// getContainerElement() == getElement(),
		// so the textarea will be a sibling of Widget.getElement
		super();

		// now this focusTextarea shall get focus,
		// which will make it possible for us to add "paste"
		// events to the AdvancedFocusPanel, not just "keydown",
		// "keypress", "keyup", etc events.
		focusTextarea = Js.uncheckedCast(DOM.createTextArea());

		// the only problem with focusTextarea seems to be its style:
		// so it is still visible on the page, unless we hide it!
		focusTextarea.classList.add("AdvancedFocusPanelsTextarea");
		if (NavigatorUtil.isMobile()) {
			focusTextarea.disabled = true;
			this.disabledTextarea = true;
			focusTextarea.style.display = "none";
			getContainerElement().tabIndex = 1;
		}

		// as the setWidget call happens later, we shall accept
		// that focusTextarea will actually be the first child
		// of the AdvancedFocusPanel... but it's not trivial
		// that it will work together well with SimplePanel...
		// but it turned out that it's Okay (testing)
		DOM.appendChild(getContainerElement(), focusTextarea);

	}

	/**
	 * Create new focus panel and set its wrapped widget.
	 * 
	 * @param widget
	 *            wrapped widget
	 */
	public AdvancedFocusPanel(Widget widget) {
		this();
		setWidget(widget);
	}

	/**
	 * In theory, we only use GWT's functionality of setFocus,
	 * and don't mind changing focus by tabIndex features
	 * @param focus true focus false blur
	 */
	@Override
	public void setFocus(boolean focus) {
		HTMLElement el = this.disabledTextarea ? getContainerElement()
				: this.focusTextarea;
		if (focus) {
			el.focus();
		} else {
			el.blur();
		}
	}

	/**
	 * @return textarea for focus events
	 */
	public HTMLElement getTextarea() {
		return focusTextarea;
	}

	/**
	 * In order for the copy/cut events to work naturally, the focusTextarea
	 * should contain a selection which contains the string to be copied. So as
	 * to work properly, this string should be continuously updated as the
	 * spreadsheet view has selected cells
	 * 
	 * TODO the focus should only be called when we want to copy
	 * 
	 * @param str
	 *            string for copying/cutting
	 */
	@Override
	public void setSelectedContent(String str) {
		if (focusTextarea.value.isEmpty()
				&& (str == null || str.isEmpty())) {
			return;
		}
		focusTextarea.value = str;
		focusTextarea.select();
	}

}
