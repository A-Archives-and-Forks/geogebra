package org.geogebra.common.euclidian;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.geos.GeoElement;

public class StrokeHelper {
	public static final String DEL = "DEL -";

	/**
	 * returns the xml of the given strokes
	 * @param strokes strokes
	 * @return xmls
	 */
	public List<String> getStrokesXML(List<GeoElement> strokes) {
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