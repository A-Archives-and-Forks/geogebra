package org.geogebra.common.gui.view.table;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.settings.TableSettings;

public class TableValuesInputProcessor implements TableValuesProcessor {

	private final Construction cons;
	private final TableValuesView tableValues;
	private final TableValuesModel model;
	private final TableSettings settings;

	/**
	 * Creates a TableValuesInputProcessor
	 * @param cons construction
	 * @param tableValues Table Values view
	 */
	public TableValuesInputProcessor(
			Construction cons, TableValuesView tableValues, TableSettings settings) {
		this.cons = cons;
		this.tableValues = tableValues;
		this.settings = settings;
		model = tableValues.getTableValuesModel();
	}

	@Override
	public void processInput(@Nonnull String input, GeoList list, int rowIndex) {
		GeoElement element = parseInput(input);
		if (model.isEmptyValue(element) && (list == null || rowIndex >= list.size())) {
			// Do not process empty input at the end of the table
			// And do not add empty element to an already empty list
			return;
		}
		model.set(element, ensureList(list, rowIndex), rowIndex);
		if (list == tableValues.getValues()) {
			settings.setValueList(list);
		}
		cons.getUndoManager().storeUndoInfo();
	}

	private GeoList ensureList(GeoList list, int rowIndex) {
		if (list != null) {
			return list;
		}
		GeoList column = new GeoList(cons);
		column.setAuxiliaryObject(true);
		column.notifyAdd();
		tableValues.doShowColumn(column);
		return column;
	}

	private GeoElement parseInput(String input) {
		String trimmedInput = input.trim();
		if (trimmedInput.equals("")) {
			return model.createEmptyValue();
		}
		try {
			double parsedInput = Double.parseDouble(trimmedInput);
			return new GeoNumeric(cons, parsedInput);
		} catch (NumberFormatException e) {
			return new GeoText(cons, input);
		}
	}
}
