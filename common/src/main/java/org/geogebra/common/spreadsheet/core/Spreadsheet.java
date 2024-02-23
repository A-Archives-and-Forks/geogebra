package org.geogebra.common.spreadsheet.core;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A spreadsheet (of arbitrary size). This class provides public API  for both rendering
 * and event handling, using {@link SpreadsheetRenderer} and {@link SpreadsheetController}.
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularDataChangeListener {

	private final SpreadsheetController controller;

	private final SpreadsheetRenderer renderer;
	private Rectangle viewport;

	/**
	 * @param tabularData data source
	 * @param rendererFactory converts custom data type to rendable objects
	 */
	public Spreadsheet(TabularData<?> tabularData, CellRenderableFactory rendererFactory) {
		controller = new SpreadsheetController(tabularData, null);
		renderer = new SpreadsheetRenderer(controller.getLayout(), rendererFactory,
				controller.getStyle());
		setViewport(new Rectangle(0, 0, 0, 0));
		tabularData.addChangeListener(this);
	}

	// layout

	// styling

	// drawing

	/**
	 * Draws current viewport of the spreadsheet
	 * @param graphics graphics to draw to
	 */
	public void draw(GGraphics2D graphics) {
		graphics.setPaint(GColor.WHITE);
		graphics.fillRect(0, 0, (int) viewport.getWidth(), (int) viewport.getHeight());
		List<TabularRange> visibleSelections = controller.getVisibleSelections();
		for (TabularRange range: visibleSelections) {
			renderer.drawSelection(range, graphics,
					viewport, controller.getLayout());
		}
		drawCells(graphics, viewport);
		for (TabularRange range: visibleSelections) {
			renderer.drawSelectionBorder(range, graphics,
					viewport, controller.getLayout());
		}
		GPoint2D draggingDot = controller.getDraggingDot();
		if (draggingDot != null) {
			renderer.drawDraggingDot(draggingDot, graphics);
		}
	}

	void drawCells(GGraphics2D graphics, Rectangle viewport) {
		TableLayout layout = controller.getLayout();
		TableLayout.Portion portion =
				layout.getLayoutIntersecting(viewport);
		double offsetX = viewport.getMinX() - layout.getRowHeaderWidth();
		double offsetY = viewport.getMinY() - layout.getColumnHeaderHeight();
		drawContentCells(graphics, portion, offsetX, offsetY);
		renderer.drawHeaderBackgroundAndOutline(graphics, viewport);
		for (Selection range: controller.getSelections()) {
			renderer.drawSelectionHeader(range, graphics,
					this.viewport, controller.getLayout());
		}
		graphics.translate(-offsetX, 0);
		graphics.setColor(controller.getStyle().getGridColor());
		for (int column = portion.fromColumn + 1; column <= portion.toColumn; column++) {
			renderer.drawColumnBorder(column, graphics);
		}

		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			setHeaderColor(graphics, controller.isSelected(-1, column));
			renderer.drawColumnHeader(column, graphics, controller.getColumnName(column));
		}

		graphics.translate(offsetX, -offsetY);
		graphics.setColor(controller.getStyle().getGridColor());
		for (int row = portion.fromRow + 1; row <= portion.toRow; row++) {
			renderer.drawRowBorder(row, graphics);
		}
		for (int row = portion.fromRow; row <= portion.toRow; row++) {
			setHeaderColor(graphics, controller.isSelected(row, -1));
			renderer.drawRowHeader(row, graphics, controller.getRowName(row));
		}
		graphics.translate(0, offsetY);
		graphics.setColor(controller.getStyle().getHeaderBackgroundColor());
		graphics.fillRect(0, 0, (int) layout.getRowHeaderWidth(),
				(int) layout.getColumnHeaderHeight());
	}

	private void drawContentCells(GGraphics2D graphics, TableLayout.Portion portion,
			double offsetX, double offsetY) {
		graphics.translate(-offsetX, -offsetY);
		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			for (int row = portion.fromRow; row <= portion.toRow; row++) {
				renderer.drawCell(row, column, graphics,
						controller.contentAt(row, column));
			}
		}
		graphics.translate(offsetX, offsetY);
	}

	private void setHeaderColor(GGraphics2D graphics, boolean isSelected) {
		if (isSelected) {
			graphics.setColor(controller.getStyle().getSelectedTextColor());
		} else {
			graphics.setColor(controller.getStyle().getTextColor());
		}
	}

	// keyboard (use com.himamis.retex.editor.share.event.KeyListener?)

	// touch

	// - TabularSelection

	/**
	 * @param viewport viewport relative to the table, in pixels
	 */
	public void setViewport(Rectangle viewport) {
		this.viewport = viewport;
		this.controller.setViewport(this.viewport);
	}

	public void setControlsDelegate(SpreadsheetControlsDelegate controlsDelegate) {
		controller.setControlsDelegate(controlsDelegate);
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerUp(int x, int y, Modifiers modifiers) {
		controller.handlePointerUp(x, y, modifiers);
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerDown(int x, int y, Modifiers modifiers) {
        controller.handlePointerDown(x, y, modifiers);

		// start selecting
	}

	public void handlePointerMove(int x, int y, Modifiers modifiers) {
		controller.handlePointerMove(x, y, modifiers);
	}

	public void handleKeyPressed(int keyCode, String key, Modifiers modifiers) {
		controller.handleKeyPressed(keyCode, key, modifiers);
	}

	public SpreadsheetController getController() {
        return controller;
	}

	@Override
	public void tabularDataDidChange(int row, int column) {
		renderer.invalidate(row, column);
	}

	public void setWidthForColumns(double width, int minColumn, int maxColumn) {
		controller.getLayout().setWidthForColumns(width, minColumn, maxColumn);
	}

	public void setHeightForRows(double height, int minRow, int maxRow) {
		controller.getLayout().setHeightForRows(height, minRow, maxRow);
	}

	public MouseCursor getCursor(int x, int y) {
		return controller.getDragAction(x, y).activeCursor;
	}

	public double getTotalWidth() {
		return controller.getLayout().getTotalWidth();
	}

	public double getTotalHeight() {
		return controller.getLayout().getTotalHeight();
	}

	public boolean isEditorActive() {
		return controller.isEditorActive();
	}
}
