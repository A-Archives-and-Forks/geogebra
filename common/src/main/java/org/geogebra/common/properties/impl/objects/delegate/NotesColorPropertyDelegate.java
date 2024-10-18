package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public class NotesColorPropertyDelegate extends AbstractGeoElementDelegate {

	public NotesColorPropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return element.isPenStroke() || element.isGeoSegment()
				|| (element.isGeoPolygon() && element.isMask());
	}
}
