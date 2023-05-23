package org.geogebra.web.html5.multiuser;

import org.geogebra.common.awt.GColor;
import org.gwtproject.user.client.ui.Label;

import elemental2.dom.CSSStyleDeclaration;

public class TooltipChip extends Label {
	public static final int LEFT_MARGIN = 8;

	/**
	 * Create a tooltip showing user interaction
	 * @param user name of the user to be shown
	 * @param color background color of the tooltip
	 */
	public TooltipChip(String user, GColor color) {
		addStyleName("tooltipChip");
		setText(user);
		CSSStyleDeclaration style = getElement().style;
		style.backgroundColor = color.toString();
	}

	public void hide() {
		getElement().classList.add("invisible");
	}

	/**
	 * Show the tooltip at the given coordinates
	 * @param x x pixel coordinate
	 * @param y y pixel coordinate
	 */
	public void show(double x, double y) {
		getElement().classList.remove("invisible");
		CSSStyleDeclaration style = getElement().style;
		style.left = (x + LEFT_MARGIN) + "px";
		style.top = (y - (getOffsetHeight() / 2d)) + "px";
	}
}
