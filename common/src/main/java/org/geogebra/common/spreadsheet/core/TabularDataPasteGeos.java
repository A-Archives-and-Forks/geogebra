package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Handles copy/paste of {@link TabularData} when the content of the cell
 * is a {@link GeoElement}.
 */
public final class TabularDataPasteGeos implements TabularDataPasteInterface<GeoElement> {

	private CopyPasteCellOperationList operations = new CopyPasteCellOperationList();

	/**
	 * Copy and paste geos ensuring that the creation order of the new, pasted geos
	 * will be the same as the copied ones.
	 */
	@Override
	public void pasteInternal(TabularData<GeoElement> tabularData,
			TabularClipboard<GeoElement> clipboard, TabularRange destination) {
		collectOperations(clipboard, destination);
		operations.sort();
		operations.apply(clipboard, tabularData);
	}

	private void collectOperations(TabularClipboard<GeoElement> buffer, TabularRange destination) {
		operations.clear();
		TabularRange source = buffer.getSourceRange();
		for (int col = source.getFromColumn(); col <= source.getToColumn(); ++col) {
			int bufferCol = col - source.getFromColumn();
			for (int row = source.getFromRow(); row <= source.getToRow(); ++row) {
				int bufferRow = row - source.getFromRow();

				// check if we're pasting back into what we're copying from
				if (bufferCol + destination.getFromColumn() <= destination.getToColumn()
						&& bufferRow + destination.getFromRow() <= destination.getToRow()
						&& (!isInSource(col, row, source, destination))) {

					GeoElement geo = buffer.contentAt(bufferRow, bufferCol);
					if (geo != null) {
						operations.add(geo.getConstructionIndex(), bufferRow, bufferCol,
								destination.getFromRow() + bufferRow,
								destination.getFromColumn() + bufferCol);
					}
				}
			}
		}
	}

	private static boolean isInSource(int col, int row, TabularRange source,
			TabularRange destination) {
		return col + (destination.getFromColumn() - source.getFromColumn()) <= source.getToColumn()
				&& col + (destination.getFromColumn() - source.getFromColumn()) >= source.getFromColumn() && row + (
				destination.getFromRow() - source.getFromRow()) <= source.getToRow()
				&& row + (destination.getFromRow() - source.getFromRow()) >= source.getFromRow();
	}
}
