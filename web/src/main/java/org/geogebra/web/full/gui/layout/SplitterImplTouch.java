package org.geogebra.web.full.gui.layout;

import org.gwtproject.layout.client.Layout.Layer;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;

import elemental2.dom.CSSProperties;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

/**
 * Override splitter behavior on touch devices: add glass dragging zone
 *
 */
public class SplitterImplTouch extends SplitterImpl {

	private HTMLElement mainDivElement;
	private HTMLElement glassDivElement;

	private static final int GLASS_SIZE = 30;

	@Override
	public HTMLElement createElement() {
		super.createElement();
		createElements();
		return baseDivElement;
	}

	private void createElements() {
		mainDivElement = DOM.createDiv();
		glassDivElement = DOM.createDiv();

		glassDivElement.appendChild(mainDivElement);
		baseDivElement.appendChild(glassDivElement);
	}

	@Override
	public boolean shouldHandleEvent(elemental2.dom.Event event, boolean mouseDown) {
		if (mouseDown) {
			return true;
		}
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEMOVE:
		case Event.ONMOUSEUP:
			EventTarget tg = event.target;
			if (DOM.isElement(tg) && (Js.<HTMLElement>uncheckedCast(tg) == glassDivElement)) {
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	public void setToHorizontal(int splitterSize) {
		baseDivElement.classList.remove("splitPaneDragger");
		mainDivElement.classList.add("gwt-SplitLayoutPanel-HDragger", "splitPaneDragger");
		CSSStyleDeclaration mainDivStyle = mainDivElement.style;
		mainDivStyle.setProperty("width", splitterSize + "px");
		mainDivStyle.setProperty("height", "100%");
		mainDivStyle.setProperty("position", "absolute");
		mainDivStyle.left = "15px";

		CSSStyleDeclaration glassDivStyle = glassDivElement.style;
		glassDivStyle.setProperty("width", (splitterSize + GLASS_SIZE) + "px");
		glassDivStyle.setProperty("height", "100%");
		glassDivStyle.setProperty("position", "absolute");
		glassDivStyle.setProperty("left", (-(GLASS_SIZE / 2)) + "px");
		glassDivStyle.zIndex = CSSProperties.ZIndexUnionType.of(10);
	}

	@Override
	public void setToVertical(int splitterSize) {
		baseDivElement.classList.remove("splitPaneDragger");
		mainDivElement.classList.add("gwt-SplitLayoutPanel-VDragger splitPaneDragger");
		CSSStyleDeclaration mainDivStyle = mainDivElement.style;
		mainDivStyle.setProperty("height", splitterSize + "px");
		mainDivStyle.setProperty("width", "100%");
		mainDivStyle.setProperty("position", "absolute");
		mainDivStyle.top = "15px";

		CSSStyleDeclaration glassDivStyle = glassDivElement.style;
		glassDivStyle.setProperty("height", (splitterSize + GLASS_SIZE) + "px");
		glassDivStyle.setProperty("width", "100%");
		glassDivStyle.setProperty("position", "absolute");
		glassDivStyle.setProperty("top", (-(GLASS_SIZE / 2)) + "px");
		glassDivStyle.zIndex = CSSProperties.ZIndexUnionType.of(10);
	}

	@Override
	public void splitterInsertedIntoLayer(Layer layer) {
		layer.getContainerElement().style.overflow = "visible";
	}
	
	@Override
	public HTMLElement getSplitterElement() {
	    return mainDivElement;
	}
}
