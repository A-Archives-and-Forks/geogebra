package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

final class SpreadsheetSelectionController {
	private final ArrayList<Selection> selections = new ArrayList<>();

	void setDimensions(int numberOfRows, int numberOfColumns) {
		// stub
	}

	 void clearSelection() {
		selections.clear();
	 }

	void selectAll(int numberOfRows, int numberOfColumns) {
		setSelections(new Selection(SelectionType.ALL,
				TabularRange.range(0, numberOfRows - 2, 0, numberOfColumns - 2)));
	}

	List<Selection> selections() {
		return selections;
	}

	/**
	 * Clears the list of selection and adds a single element to it
	 * @param selection Selection
	 */
	public void setSelections(Selection selection) {
		this.selections.clear();
		this.selections.add(selection);
	}

	/**
	 * Selects a row with given index
	 * @param rowIndex Index
	 * @param numberOfColumns Current amount of columns
	 * @param extendSelection Whether we want to extend the current selection
	 * @param addSelection Whether we want to add it to the current selections
	 */
	void selectRow(int rowIndex, int numberOfColumns,
			boolean extendSelection, boolean addSelection) {
		Selection row = new Selection(SelectionType.ROWS,
				TabularRange.range(rowIndex, rowIndex, 0, numberOfColumns - 2));
		select(row, extendSelection, addSelection);
	}

	/**
	 * Selects a column with given index
	 * @param columnIndex Index
	 * @param numberOfRows Current amount of rows
	 * @param extendSelection Whether we want to extend the current selection
	 * @param addSelection Whether we want to add it to the current selections
	 */
	void selectColumn(int columnIndex, int numberOfRows,
			boolean extendSelection, boolean addSelection) {
		Selection column = new Selection(SelectionType.COLUMNS,
				TabularRange.range(0, numberOfRows - 2, columnIndex, columnIndex));
		select(column, extendSelection, addSelection);
	}

	void selectCell(int rowIndex, int columnIndex, boolean extendSelection, boolean addSelection) {
		Selection selection = Selection.getSingleCellSelection(rowIndex, columnIndex);
		select(selection, extendSelection, addSelection);
	}

	/**
	 * @param extendSelection True if the current selection should expand, false else
	 */
	void moveLeft(boolean extendSelection) {
		if (getLastSelection() != null) {
			select(getLastSelection().getLeft(extendSelection), extendSelection, false);
		}
	}

	/**
	 * @param extendSelection True if the current selection should expand, false else
	 * @param numberOfColumns Number of columns in the table
	 */
	void moveRight(boolean extendSelection, int numberOfColumns) {
		if (getLastSelection() != null) {
			select(getLastSelection().getRight(numberOfColumns, extendSelection),
					extendSelection, false);
		}
	}

	/**
	 * @param extendSelection True if the current selection should expand, false else
	 */
	void moveUp(boolean extendSelection) {
		if (getLastSelection() != null) {
			select(getLastSelection().getTop(extendSelection), extendSelection, false);
		}
	}

	/**
	 * @param extendSelection True if the current selection should expand, false else
	 * @param numberOfRows Number of rows
	 */
	void moveDown(boolean extendSelection, int numberOfRows) {
		if (getLastSelection() != null) {
			select(getLastSelection().getBottom(numberOfRows, extendSelection),
					extendSelection, false);
		}
	}

	void enter() {
		// stub
	}

	void cancel() {
		// stub
	}

	void addSelectionListener(SpreadsheetSelectionListener listener) {
		// stub
	}

	/**
	 * @param selection {@link Selection}
	 * @param extendSelection Whether we want to extend the current selection (SHIFT)
	 * @param addSelection Whether we want to add this selection to the current selections (CTRL)
	 */
	public void select(Selection selection, boolean extendSelection, boolean addSelection) {
		if (extendSelection && getLastSelection() != null) {
			extendSelection(getLastSelection(), selection, addSelection);
			return;
		} else if (!addSelection) {
			setSelections(selection);
			return;
		}
		ArrayList<Selection> independent = new ArrayList<>();
		Selection merged = selection;
		for (Selection other: selections) {
			Selection mergeResult = merged.merge(other);
			if (mergeResult == null) {
				independent.add(other);
			} else {
				merged = mergeResult;
			}
		}
		selections.clear();
		selections.addAll(independent);
		selections.add(merged);
	}

	/**
	 * Extends a selection with another selection
	 * @param current Current Selection
	 * @param other Selection used to extend the current selection
	 * @param addSelection Whether we want to add this selection to the current selections
	 */
	private void extendSelection(Selection current, Selection other, boolean addSelection) {
		Selection extendedSelection = current.getExtendedSelection(other);
		this.selections.remove(current);
		if (addSelection) {
			this.selections.add(extendedSelection);
			return;
		}
		setSelections(extendedSelection);
	}

	public boolean isSelected(int row, int column) {
		return selections.stream().anyMatch(s -> s.contains(row, column));
	}

	/**
	 * @return True if there is currently at least one cell selected, false else
	 */
	public boolean hasSelection() {
		return !selections.isEmpty();
	}

	/**
	 * @return Last Selection if present, null otherwise
	 */
	public Selection getLastSelection() {
		return selections.isEmpty() ? null : selections.get(selections.size() - 1);
	}
}
