package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public class QuadraticEquationFormDelegate extends AbstractGeoElementDelegate {

	public QuadraticEquationFormDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		if (element instanceof QuadraticEquationRepresentable) {
			if (element.getKernel() != null && element.getKernel().getEquationBehaviour() != null) {
				return element.getKernel().getEquationBehaviour()
						.allowsChangingEquationFormsByUser();
			}
			return true;
		}
		return false;
	}
}