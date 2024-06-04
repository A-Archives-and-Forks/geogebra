package org.geogebra.common.spreadsheet.core;

import java.util.function.BiConsumer;

import org.geogebra.common.util.shape.Rectangle;

/**
 * A utility class designed to adjust the spreadsheet's viewport if needed<br/>
 * This can happen in two different scenarios:
 * <li>A cell, row, or column that is not fully visible is clicked</li>
 * <li>The paste selection is dragged to the top / right / bottom / left edge</li>
 */
public class ViewportAdjuster {

	private final TableLayout layout;
	private final ViewportAdjustmentHandler viewportAdjustmentHandler;
	private final static int SCROLL_INCREMENT = 2;
	private final static int SCROLL_AMOUNT_FOR_PASTE_SELECTION = 7;

	/**
	 * @param layout TableLayout
	 * @param viewportAdjustmentHandler ViewportAdjustmentHandler
	 */
	public ViewportAdjuster(TableLayout layout,
			ViewportAdjustmentHandler viewportAdjustmentHandler) {
		this.layout = layout;
		this.viewportAdjustmentHandler = viewportAdjustmentHandler;
	}

	/**
	 * If the right edge of the cell is to the right of the viewport, scroll right.
	 * If the left edge of the cell is to the left of the viewport
	 *  - if we already scrolled right (cell bigger than viewport), we cancel the scroll
	 *  - otherwise scroll left
	 * @param row Row index
	 * @param column Column index
	 * @param viewport Viewport
	 * @return True if the viewport was adjusted, false else
	 */
	public boolean adjustViewportIfNeeded(int row, int column, Rectangle viewport) {
		double scrollAmountX = getScrollAmountX(column, viewport);
		double scrollAmountY = getScrollAmountY(row, viewport);

		if (scrollAmountX != 0 || scrollAmountY != 0) {
			viewportAdjustmentHandler.setScrollPosition(
					(int) (viewport.getMinX() + scrollAmountX),
					(int) (viewport.getMinY() + scrollAmountY));
			return true;
		}
		return false;
	}

	/**
	 * Starts scrolling the view when the paste selection is at the top / right / bottom / left
	 * edge of the viewport
	 * @param x Horizontal pointer position
	 * @param y Vertical pointer position
	 * @param viewport Viewport
	 * @param extendVertically True if the paste selection is extended vertically, false else
	 * (horizontally)
	 * @param callback Function used to update the destination of the paste selection
	 */
	public void scrollForPasteSelectionIfNeeded(int x, int y, Rectangle viewport,
			boolean extendVertically, BiConsumer<Integer, Integer> callback) {
		int viewportWidth = (int) viewport.getWidth();
		int viewportHeight = (int) viewport.getHeight();
		int scrollAmountX = 0;
		int scrollAmountY = 0;

		if (extendVertically) {
			scrollAmountY = getVerticalScrollAmountForDrag(viewportHeight, y);
		} else {
			scrollAmountX = getHorizontalScrollAmountForDrag(viewportWidth, x);
		}

		if (scrollAmountX == 0 && scrollAmountY == 0) {
			return;
		}

		viewportAdjustmentHandler.setScrollPosition(
				(int) viewport.getMinX() + scrollAmountX,
				(int) viewport.getMinY() + scrollAmountY);
		callback.accept(x + scrollAmountX, y + scrollAmountY);
	}

	private double getScrollAmountX(int column, Rectangle viewport) {
		double scrollAmountX = 0;
		boolean scrolledRight = false;
		if (shouldAdjustViewportHorizontallyRightwards(column, viewport)) {
			scrollAmountX = Math.ceil(layout.getX(column + 1) - viewport.getMinX()
					+ layout.getRowHeaderWidth() - viewport.getWidth()
					+ viewportAdjustmentHandler.getScrollBarWidth() + SCROLL_INCREMENT);
			scrolledRight = true;
		}
		if (shouldAdjustViewportHorizontallyLeftwards(column, viewport)) {
			scrollAmountX = scrolledRight ? 0
					: -Math.floor(viewport.getMinX() - layout.getX(column));
		}
		return scrollAmountX;
	}

	private double getScrollAmountY(int row, Rectangle viewport) {
		double scrollAmountY = 0;
		boolean scrolledDown = false;
		if (shouldAdjustViewportVerticallyDownwards(row, viewport)) {
			scrollAmountY = Math.ceil(layout.getY(row + 1) - viewport.getMinY()
					+ layout.getColumnHeaderHeight() - viewport.getHeight()
					+ viewportAdjustmentHandler.getScrollBarWidth() + SCROLL_INCREMENT);
			scrolledDown = true;
		}
		if (shouldAdjustViewportVerticallyUpwards(row, viewport)) {
			scrollAmountY = scrolledDown ? 0 : -Math.floor(viewport.getMinY() - layout.getY(row));
		}
		return scrollAmountY;
	}

	private boolean shouldAdjustViewportHorizontallyRightwards(int column, Rectangle viewport) {
		if (!isValidColumnIndex(column)) {
			return false;
		}
		return layout.getX(column + 1) - viewport.getMinX() + layout.getRowHeaderWidth()
				> viewport.getWidth() - viewportAdjustmentHandler.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportHorizontallyLeftwards(int column, Rectangle viewport) {
		return isValidColumnIndex(column) && layout.getX(column) < viewport.getMinX();
	}

	private boolean shouldAdjustViewportVerticallyDownwards(int row, Rectangle viewport) {
		if (!isValidRowIndex(row)) {
			return false;
		}
		return layout.getY(row + 1) - viewport.getMinY() + layout.getColumnHeaderHeight()
				> viewport.getHeight() - viewportAdjustmentHandler.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportVerticallyUpwards(int row, Rectangle viewport) {
		return isValidRowIndex(row) && layout.getY(row) < viewport.getMinY();
	}

	private boolean isValidColumnIndex(int column) {
		return column >= 0 && column < layout.numberOfColumns();
	}

	private boolean isValidRowIndex(int row) {
		return row >= 0 && row < layout.numberOfRows();
	}

	public void updateScrollPaneSize() {
		viewportAdjustmentHandler.updateScrollPanelSize();
	}

	private int getVerticalScrollAmountForDrag(int viewportHeight, int y) {
		if (viewportHeight - y < viewportHeight / 10) {
			return SCROLL_AMOUNT_FOR_PASTE_SELECTION;
		} else if (y < viewportHeight / 10) {
			return -SCROLL_AMOUNT_FOR_PASTE_SELECTION;
		}
		return 0;
	}

	private int getHorizontalScrollAmountForDrag(int viewportWidth, int x) {
		if (viewportWidth - x < viewportWidth / 10) {
			return SCROLL_AMOUNT_FOR_PASTE_SELECTION;
		} else if (x < viewportWidth / 10) {
			return -SCROLL_AMOUNT_FOR_PASTE_SELECTION;
		}
		return 0;
	}
}
