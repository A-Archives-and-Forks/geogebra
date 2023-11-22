package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Rectangle;

public interface SpreadsheetCellEditor {
	void setBounds(Rectangle editorBounds);

	void setTargetCell(int row, int column);

	void setContent(Object content);
}
