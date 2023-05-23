package com.himamis.retex.editor.web;

import static com.himamis.retex.editor.web.MathFieldW.SCROLL_THRESHOLD;

import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

import elemental2.dom.HTMLElement;

/**
 * Class to handle scrolling of the mathfield container.
 */
public class MathFieldScroller {

	/**
	 * Scrolls content horizontally,  based on the cursor position
	 */
	public static void scrollHorizontallyToCursor(Widget parent, int rightMargin, int cursorX) {
		HTMLElement parentElement = parent.getElement();
		int parentWidth = parent.getOffsetWidth() - rightMargin;
		double scrollLeft = parentElement.scrollLeft;

		double scroll = MathFieldInternal.getHorizontalScroll(scrollLeft, parentWidth, cursorX);
		if (scroll != scrollLeft) {
			parentElement.scrollLeft = scroll;
		}
	}

	/**
	 * Scrolls content verically, based on the cursor position
	 *
	 * @param margin
	 *            minimal distance from cursor to top/bottom border
	 */
	public static void scrollVerticallyToCursor(FlowPanel parent, int margin, int cursorY) {
		HTMLElement parentElement = parent.getElement();
		int height = parent.getOffsetHeight();
		double scrollTop = parentElement.scrollTop + margin;
		int position = cursorY < SCROLL_THRESHOLD ? 0 : cursorY;
		if (position < scrollTop
				|| position > scrollTop + height - SCROLL_THRESHOLD) {
			parentElement.scrollTop = position;
		}
	}
}
