package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.ActionType;

public class UpdateStrokeStyleStore extends StrokeHelper {

	List<GeoElement> updatedStrokes;

	List<String> initialStateXML;

	List<String> modifiedStateXML;

	List<GeoElement> initialSplitStrokes;

	private final UndoManager undoManager;

	/**
	 * Creates a new helper data structure that is used to stores the original states of the strokes
	 * before updating the style (color, line thickness) & the updated strokes.
	 * @param initialSplitStrokes reference to the children stroke created after splitting
	 */
	public UpdateStrokeStyleStore(List<GeoElement> initialSplitStrokes, UndoManager undoManager) {
		this.initialSplitStrokes = initialSplitStrokes;
		initialStateXML = getStrokesXML(initialSplitStrokes);
		this.undoManager = undoManager;
	}

	/**
	 * returns an array of XMLs with the styled strokes, and XMLS of the unstyled strokes that
	 * need to be removed on undo
	 * @return array of XMLs
	 */
	public String[] toStyledStrokeArray() {
		return Stream.concat(initialSplitStrokes.stream().map(s -> DEL + s.getLabelSimple()),
				modifiedStateXML.stream()).toArray(String[]::new);
	}

	/**
	 * returns an array of XMLs with the initial unstyled stroke xml, and
	 * XMLs of the styled strokes that need to be removed on undo
	 * @return array of XMLs
	 */
	public String[] toUnStyledStrokeArray() {
		return Stream.concat(updatedStrokes.stream().map(s -> DEL + s.getLabelSimple()),
				initialStateXML.stream()).toArray(String[]::new);
	}

	/**
	 * adds the updated strokes (coloring/thickness etc) to the StrokeSplitHelper object
	 * @param updatedStrokes newly created stroke with the modifications
	 */

	public void addUpdatedStrokes(List<GeoElement> updatedStrokes) {
		this.updatedStrokes = updatedStrokes;
		modifiedStateXML = getStrokesXML(updatedStrokes);
	}

	/**
	 * stores an undo for the stroke style update
	 */
	public void storeUndoableStrokeStyleUpdate() {
		undoManager.storeUndoableAction(
				ActionType.UPDATE,
				toStyledStrokeArray(),
				ActionType.UPDATE,
				toUnStyledStrokeArray()
			);
	}
}