package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;

public class NotesInlineColorPropertyDelegate extends AbstractGeoElementDelegate {

	public NotesInlineColorPropertyDelegate(GeoElement element)
			throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return element instanceof GeoInline;
	}
}