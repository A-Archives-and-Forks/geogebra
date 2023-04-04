package org.geogebra.common.euclidian;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;

public class StrokeSplitHelper extends StrokeHelper {

	GeoElement initialStroke;

	List<GeoElement> splitParts;

	List<String> initialStateXML;

	List<String> splitStrokesXML;

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
		splitStrokesXML = getStrokesXML(splitParts);
	}

	/**
	 * returns an array of XMLs with the initial state of the stroke before splitting.
	 * used for the undoing of splitting the stroke
	 * @return array of XMLs
	 */
	public String[] toMergeActionArray() {
		return Stream.concat(Collections.singletonList(DEL + initialStroke.getLabelSimple())
				.stream(), splitStrokesXML.stream()).toArray(String[]::new);
	}

	/**
	 * returns an array of XMLs with the children stokes after splitting the initial stroke.
	 * used for the redoing of splitting the stroke
	 * @return array of XMLs
	 */
	public String[] toSplitActionArray() {
		return Stream.concat(splitParts.stream().map(s -> DEL + s.getLabelSimple()),
				initialStateXML.stream()).toArray(String[]::new);
	}
}