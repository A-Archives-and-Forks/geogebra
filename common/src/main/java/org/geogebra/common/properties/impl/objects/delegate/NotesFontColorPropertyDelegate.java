package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;

public class NotesFontColorPropertyDelegate extends AbstractGeoElementDelegate {

	public NotesFontColorPropertyDelegate(GeoElement element)
			throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return element instanceof GeoInline;
	}
}