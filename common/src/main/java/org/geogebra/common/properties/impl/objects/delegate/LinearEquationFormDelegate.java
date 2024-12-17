package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public class LinearEquationFormDelegate extends AbstractGeoElementDelegate {

	public LinearEquationFormDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		if (element instanceof LinearEquationRepresentable) {
			// see e.g., APPS-1691
			if (element.getKernel() != null && element.getKernel().getEquationBehaviour() != null) {
				return element.getKernel().getEquationBehaviour()
						.allowsChangingEquationFormsByUser();
			}
			return true;
		}
		return false;
	}
}
