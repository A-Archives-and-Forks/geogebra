package org.geogebra.common.spreadsheet.core;

/**
 * Creates renderers for custom data types stored in {@link TabularData}
 */
public interface CellRenderableFactory {

	/**
	 * May return one of these types that are rendered by spreadsheet
	 * * String
	 * * Boolean (render as checkbox) - TODO
	 * * TeXIcon
	 * * button - TODO
	 * @param data cell content
	 * @return renderable representation of the cell data
	 */
	public Object getRenderable(Object data);
}
