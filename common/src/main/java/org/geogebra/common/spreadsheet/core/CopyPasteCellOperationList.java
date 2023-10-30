package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Sortable list of copy/paste cell operations.
 */
public class CopyPasteCellOperationList {
	private List<CopyPasteCellOperation> list = new ArrayList<>();
	void clear() {
		list.clear();
	}

	private static Comparator<CopyPasteCellOperation> comparator;

	/**
	 * Adds a copy/paste cell operation.
	 *
	 * @param id of the content
	 * @param sourceRow to copy from.
	 * @param sourceCol to copy from.
	 * @param destRow to paste to.
	 * @param destCol to paste to.
	 */
	public void add(int id, int sourceRow, int sourceCol, int destRow, int destCol) {
		list.add(new CopyPasteCellOperation(id, sourceRow, sourceCol,
				destRow, destCol));
	}

	/**
	 * Apply all the operations in list between two data sources.
	 *
	 * @param from buffer to copy from
	 * @param to to paste to.
	 */
	void apply(TabularBuffer<GeoElement> from, TabularData<GeoElement> to) {
		for (CopyPasteCellOperation operation: list) {
			operation.apply(from, to);
		}
	}

	/**
	 * Sort the list by id (construction order)
	 */
	public void sort() {
		list.sort(getComparator());
	}

	private static Comparator<CopyPasteCellOperation> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<CopyPasteCellOperation>() {
				@Override
				public int compare(CopyPasteCellOperation a, CopyPasteCellOperation b) {
					return a.getId() - b.getId();
				}

			};
		}
		return comparator;
	}
}
