package org.geogebra.web.full.gui.layout;

import org.gwtproject.layout.client.Layout.Layer;
import org.gwtproject.user.client.DOM;

import elemental2.dom.HTMLElement;

public class SplitterImpl {

	protected HTMLElement baseDivElement;

	public HTMLElement createElement() {
		return baseDivElement = DOM.createDiv();
	}

	/**
	 * @param event
	 *            pointer event
	 * @param mouseDown
	 *            whether pointer is currently down
	 * @return whether event should be handled
	 */
	public boolean shouldHandleEvent(elemental2.dom.Event event, boolean mouseDown) {
		return true;
	}

	/**
	 * @param splitterSize
	 *            splitter width
	 */
	public void setToHorizontal(int splitterSize) {
		baseDivElement.style.setProperty("width", splitterSize + "px");
		baseDivElement.classList.add("gwt-SplitLayoutPanel-HDragger");
	}

	/**
	 * @param splitterSize
	 *            splitter height
	 */
	public void setToVertical(int splitterSize) {
		baseDivElement.style.setProperty("height", splitterSize + "px");
		baseDivElement.classList.add("gwt-SplitLayoutPanel-VDragger");
	}
	
	public HTMLElement getSplitterElement() {
		return baseDivElement;
	}
	
	/**
	 * @param layer
	 *            parent layer
	 */
	public void splitterInsertedIntoLayer(Layer layer) {
		// overridden in touch
	}
}
