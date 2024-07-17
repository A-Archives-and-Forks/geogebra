package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Sortable list of copy/paste cell operations.
 */
final class CopyPasteCellOperationList {
	private List<CopyPasteCellOperation> list = new ArrayList<>();

	void clear() {
		list.clear();
	}

	/**
	 * Adds a copy/paste cell operation.
	 *
	 * @param geo Geo to be copy-pasted
	 * @param sourceRow to copy from.
	 * @param sourceCol to copy from.
	 * @param destRow to paste to.
	 * @param destCol to paste to.
	 */
	void add(GeoElement geo, int sourceRow, int sourceCol, int destRow, int destCol) {
		list.add(new CopyPasteCellOperation(geo, sourceRow, sourceCol,
				destRow, destCol));
	}

	/**
	 * Apply all the operations in list between two data sources.
	 * @param tabularData TabularData where content is pasted to
	 */
	void apply(TabularData<GeoElement> tabularData) {
		for (CopyPasteCellOperation operation: list) {
			operation.apply(tabularData);
		}
	}

	/**
	 * Sort the list by id (construction order)
	 */
	void sort() {
		list.sort(Comparator.comparing(CopyPasteCellOperation::getId));
	}
}
