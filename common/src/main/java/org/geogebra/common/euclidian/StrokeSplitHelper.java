package org.geogebra.common.euclidian;

import java.util.ArrayList;
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

	public static final String DEL = "DELETE";

	public List<String> getInitialStateXML() {
		return initialStateXML;
	}

	public List<String> getModifiedStateXML() {
		return modifiedStateXML;
	}

	/**
	 * Creates a new helper data structure that stores the original stoke & the split
	 * up parts.
	 * @param initialStroke parent stroke
	 * @param splitParts children stroke created after selection
	 */
	public StrokeSplitHelper(GeoElement initialStroke, List<GeoElement> splitParts) {
		this.initialStroke = initialStroke;
		this.splitParts = splitParts;
		initialStateXML = Collections.singletonList(
				(initialStroke.getParentAlgorithm() == null) ? initialStroke.getXML()
						: initialStroke.getParentAlgorithm().getXML());

		modifiedStateXML = splitParts.stream().map(GeoElement::getParentAlgorithm)
				.map(AlgoElement::getXML).collect(Collectors.toList());
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

		initialStateXML = initialStrokes.stream().map(GeoElement::getParentAlgorithm)
						.map(AlgoElement::getXML).collect(Collectors.toList());
	}

	/**
	 * adds the updated strokes (coloring/thickness etc) to the StrokeSplitHelper object
	 * @param updatedStrokes newly created stroke with the modifications
	 */
	public void addUpdatedStrokes(List<GeoElement> updatedStrokes) {
		this.updatedStrokes = updatedStrokes;
		modifiedStateXML = updatedStrokes.stream().map(GeoElement::getParentAlgorithm)
				.map(AlgoElement::getXML).collect(Collectors.toList());
	}

	/**
	 * returns an array of XMLs with what to delete & create on undo/redo
	 * @param action either splitting children strokes or merging children to create parent stroke
	 * @return array of XMLs
	 */
	public String[] toActionArray(ActionType action) {
		switch (action) {
		case SPLIT_STROKE:
			return Stream.concat(splitParts.stream().map(s -> DEL + s.getLabelSimple()),
							initialStateXML.stream()).toArray(String[]::new);
		case MERGE_STROKE:
			return Stream.concat(Collections.singletonList(DEL + initialStroke.getLabelSimple())
							.stream(), modifiedStateXML.stream()).toArray(String[]::new);
		}
		return new String[0];
	}

	/**
	 * returns an array of XMLs with what to delete & create on undo/redo
	 * @param action either adding or removing the modified strokes
	 * @return array of XMLs
	 */
	public String[] toUpdateArray(ActionType action) {
		switch (action) {
		case REMOVE:
			return Stream.concat(updatedStrokes.stream().map(s -> DEL + s.getLabelSimple()),
							initialStateXML.stream()).toArray(String[]::new);
		case ADD:
			return Stream.concat(initialStrokes.stream().map(s -> DEL + s.getLabelSimple()),
					modifiedStateXML.stream()).toArray(String[]::new);
		}
		return new String[0];
	}

	public boolean containsGeo(ArrayList<GeoElement> geoElements) {
		return !geoElements.retainAll(originalGeos);
	}
}
