package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier;

public class ContextMenuItems {
	static final int HEADER_INDEX = -1;
	private final CopyPasteCutTabularData copyPasteCut;
	private final SpreadsheetSelectionController selectionController;
	private final SpreadsheetController spreadsheetController;

	/**
	 * @param spreadsheetController {@link SpreadsheetController}
	 * @param selectionController {@link SpreadsheetSelectionController}
	 * @param copyPasteCut {@link CopyPasteCutTabularData}
	 */
	public ContextMenuItems(SpreadsheetController spreadsheetController,
			SpreadsheetSelectionController selectionController,
			CopyPasteCutTabularData copyPasteCut) {
		this.spreadsheetController = spreadsheetController;
		this.selectionController = selectionController;
		this.copyPasteCut = copyPasteCut;
	}

	/**
	 * Gets the context menu items for the specific <b>single</b> cell / row / column
	 * @param row of the cell.
	 * @param column of the cell.
	 * @return map of the menu key and its action.
	 */
	public List<ContextMenuItem> get(int row, int column) {
		return get(row, row, column, column);
	}

	/**
	 * Gets the context menu items for the specific <b>multiple</b> cells / rows / columns
	 * @param fromRow Index of the uppermost row
	 * @param toRow Index of the bottommost row
	 * @param fromCol Index of the leftmost column
	 * @param toCol Index of the rightmost column
	 * @return map of the menu key and its action.
	 */
	public List<ContextMenuItem> get(int fromRow, int toRow, int fromCol, int toCol) {
		if (shouldShowTableItems(fromRow, fromCol)) {
			return tableItems(fromRow, fromCol);
		} else if (fromRow == HEADER_INDEX) {
			return columnItems(fromCol, toCol);
		} else if (fromCol == HEADER_INDEX) {
			return rowItems(fromRow, toRow);
		}
		return cellItems(fromRow, toRow, fromCol, toCol);
	}

	/**
	 * @param fromRow Index of the uppermost row
	 * @param fromCol Index of the leftmost column
	 * @return Whether the table items should be shown. This is the case if either all cells are
	 * selected or the user clicked the top left cell (between A and 1).
	 */
	private boolean shouldShowTableItems(int fromRow, int fromCol) {
		return spreadsheetController.areAllCellsSelected()
				|| (fromRow == HEADER_INDEX && fromCol == HEADER_INDEX);
	}

	private List<ContextMenuItem> tableItems(int row, int column) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> cutCells(row, column)),
				new ContextMenuItem(Identifier.COPY, () -> copyCells(row, column)),
				new ContextMenuItem(Identifier.PASTE, () -> pasteCells(row, column))
		);
	}

	private List<ContextMenuItem> cellItems(int fromRow, int toRow, int fromCol, int toCol) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> cutCells(fromRow, fromCol)),
				new ContextMenuItem(Identifier.COPY, () -> copyCells(fromRow, fromCol)),
				new ContextMenuItem(Identifier.PASTE, () -> pasteCells(fromRow, fromCol)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.INSERT_ROW_ABOVE,
						() -> insertRowAt(fromRow, false)),
				new ContextMenuItem(Identifier.INSERT_ROW_BELOW,
						() -> insertRowAt(toRow + 1, true)),
				new ContextMenuItem(Identifier.INSERT_COLUMN_LEFT,
						() -> insertColumnAt(fromCol, false)),
				new ContextMenuItem(Identifier.INSERT_COLUMN_RIGHT,
						() -> insertColumnAt(toCol + 1, true)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.DELETE_ROW, () -> deleteRowAt(fromRow)),
				new ContextMenuItem(Identifier.DELETE_COLUMN,
						() -> deleteColumnAt(fromCol))
		);
	}

	private void pasteCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			copyPasteCut.paste(row, column);
		} else {
			for (Selection selection: selections) {
				copyPasteCut.paste(selection.getRange());
			}
		}
	}

	private void copyCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			copyPasteCut.copyDeep(new TabularRange(row, row, column, column));
		} else {
			for (Selection selection: selections) {
				copyPasteCut.copyDeep(selection.getRange());
			}
		}
	}

	private void cutCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			copyPasteCut.cut(new TabularRange(row, row, column, column));
		} else {
			for (Selection selection: selections) {
				copyPasteCut.cut(selection.getRange());
			}
		}
	}

	/*private void deleteCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			tabularData.setContent(row, column, null);
		} else {
			selections.stream().forEach(selection -> deleteCells(selection.getRange()));
		}
	}

	private void deleteCells(TabularRange range) {
		for (int row = range.getFromRow(); row < range.getToRow(); row++) {
			for (int column = range.getFromColumn(); column < range.getToRow(); column++) {
				tabularData.setContent(row, column, null);
			}
		}
	}*/

	private List<ContextMenuItem> rowItems(int fromRow, int toRow) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> {}),
				new ContextMenuItem(Identifier.COPY, () -> {}),
				new ContextMenuItem(Identifier.PASTE, () -> {}),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.INSERT_ROW_ABOVE,
						() -> insertRowAt(fromRow, false)),
				new ContextMenuItem(Identifier.INSERT_ROW_BELOW,
						() -> insertRowAt(toRow + 1, true)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.DELETE_ROW, () -> deleteRowAt(fromRow))
		);
	}

	private List<ContextMenuItem> columnItems(int fromCol, int toCol) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> {}),
				new ContextMenuItem(Identifier.COPY, () -> {}),
				new ContextMenuItem(Identifier.PASTE, () -> {}),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.INSERT_COLUMN_LEFT,
						() -> insertColumnAt(fromCol, false)),
				new ContextMenuItem(Identifier.INSERT_COLUMN_RIGHT,
						() -> insertColumnAt(toCol + 1, true)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.DELETE_COLUMN,
						() -> deleteColumnAt(fromCol))
				);
	}

	private void deleteRowAt(int row) {
		spreadsheetController.deleteRowAt(row);
	}

	private void deleteColumnAt(int column) {
		spreadsheetController.deleteColumnAt(column);
	}

	private void insertColumnAt(int column, boolean right) {
		if (column == -1) {
			column = 0;
		}
		spreadsheetController.insertColumnAt(column, right);
	}

	private void insertRowAt(int row, boolean below) {
		if (row == -1) {
			row = 0;
		}
		spreadsheetController.insertRowAt(row, below);
	}
}