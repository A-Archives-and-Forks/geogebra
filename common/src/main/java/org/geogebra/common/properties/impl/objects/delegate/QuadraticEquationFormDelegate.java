package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.AlgoFitLineX;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;

public class QuadraticEquationFormDelegate extends AbstractGeoElementDelegate {

	public QuadraticEquationFormDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		return hasEquationModeSetting(element);
	}

	private boolean hasEquationModeSetting(GeoElement element) {
		return (element.isGeoConic())
				&& !isEnforcedEquationForm(element)
				&& element.getDefinition() == null; // TODO why?
	}

	private boolean isEnforcedEquationForm(GeoElement element) {
		EquationBehaviour equationBehaviour = element.getKernel().getEquationBehaviour();
		boolean isUserInput = element.getParentAlgorithm() == null;
		if (element instanceof QuadraticEquationRepresentable) {
			if (isUserInput) {
				return equationBehaviour.getConicAlgebraInputEquationForm() != null
						&& !equationBehaviour.allowsChangingEquationFormsByUser();
			}
		}
		return false;
	}
}