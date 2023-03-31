package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class StrokeSplitHelper {

	GeoElement initialStroke;

	List<GeoElement> splitParts;

	List<GeoElement> initialStrokes;

	List<GeoElement> updatedStrokes;

	List<String> initialStateXML;

	List<String> modifiedStateXML;

	List<GeoElement> originalGeos;

	public static final String DEL = "Delete(";

	public List<String> getInitialStateXML() {
		return initialStateXML;
	}

	public List<String> getModifiedStateXML() {
		return modifiedStateXML;
	}

	/**
	 * Creates a new helper data structure that stores the original stroke & the split
	 * up parts.
	 * @param initialStroke parent stroke
	 * @param splitParts children stroke created after selection
	 */
	public StrokeSplitHelper(GeoElement initialStroke, List<GeoElement> splitParts) {
		this.initialStroke = initialStroke;
		this.splitParts = splitParts;
		initialStateXML = getStrokesXML(Arrays.asList(initialStroke));
		modifiedStateXML = getStrokesXML(splitParts);
	}

	/**
	 * Creates a new helper data structure that is used to stores the original states of the strokes
	 * before updating (color, line thickness etc) & the updated strokes up parts.
	 * @param originalGeos reference to the parent stroke
	 * @param initialStrokes reference to the children stroke created after splitting
	 */
	public StrokeSplitHelper(List<GeoElement> originalGeos, List<GeoElement> initialStrokes) {
		this.initialStrokes = initialStrokes;
		this.originalGeos = originalGeos;
		initialStateXML = getStrokesXML(initialStrokes);
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
	 * returns an array of XMLs with the initial state of the stroke before splitting.
	 * used for the undoing of splitting the stroke
	 * @return array of XMLs
	 */
	public String[] toMergeActionArray() {
		return Stream.concat(Collections.singletonList(DEL + initialStroke.getLabelSimple() + ")")
				.stream(), modifiedStateXML.stream()).toArray(String[]::new);
	}

	/**
	 * returns an array of XMLs with the children stokes after splitting the initial stroke.
	 * used for the redoing of splitting the stroke
	 * @return array of XMLs
	 */
	public String[] toSplitActionArray() {
		return Stream.concat(splitParts.stream().map(s -> DEL + s.getLabelSimple() + ")"),
				initialStateXML.stream()).toArray(String[]::new);
	}


	/**
	 * returns an array of XMLs with the styled strokes, and XMLS of the unstyled strokes that
	 * need to be removed on undo
	 * @return array of XMLs
	 */
	public String[] toStyledStrokeArray() {
		return Stream.concat(initialStrokes.stream().map(s -> DEL + s.getLabelSimple() + ")"),
				modifiedStateXML.stream()).toArray(String[]::new);
	}

	/**
	 * returns an array of XMLs with the initial unstyled stroke xml, and
	 * XMLs of the styled strokes that need to be removed on undo
	 * @return array of XMLs
	 */
	public String[] toUnStyledStrokeArray() {
		return Stream.concat(updatedStrokes.stream().map(s -> DEL + s.getLabelSimple() + ")"),
				initialStateXML.stream()).toArray(String[]::new);
	}

	public boolean containsGeo(ArrayList<GeoElement> geoElements) {
		return !geoElements.retainAll(originalGeos);
	}

	private static List<String> getStrokesXML(List<GeoElement> strokes) {
		return strokes.stream().map(
				stroke -> {
					if (stroke.getParentAlgorithm() != null) {
						return stroke.getParentAlgorithm().getXML();
					} else {
						return stroke.getXML();
					}
				}
		).collect(Collectors.toList());
	}
}
