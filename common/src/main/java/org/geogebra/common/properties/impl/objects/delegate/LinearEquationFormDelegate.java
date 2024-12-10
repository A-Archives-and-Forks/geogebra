package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.AlgoFitLineX;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;

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
			// TOOO fix testEquationPropertyVisibilityGraphing
			EquationBehaviour equationBehaviour = element.getKernel().getEquationBehaviour();
			if (!equationBehaviour.allowsChangingEquationFormsByUser()) {
				return false;
			}
			boolean isUserInput = element.getParentAlgorithm() == null;
			if (isUserInput) {
				return equationBehaviour.getLinearAlgebraInputEquationForm() == null;
			}
			if (element instanceof GeoLine) {
				AlgoElement algo = element.getParentAlgorithm();
				boolean isFitLineOuput = (algo instanceof AlgoFitLineX)
						|| (algo instanceof AlgoFitLineY);
				if (isFitLineOuput) {
					return equationBehaviour.getFitLineCommandEquationForm() != null;
				}
				if (element.isGeoRay()) {
					return equationBehaviour.getRayCommandEquationForm() != null;
				}
				return equationBehaviour.getLineCommandEquationForm() != null;
			}
		}
		return false;
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && !isEnforcedEquationForm(element);
	}

	private boolean isEnforcedEquationForm(GeoElement element) {
		EquationBehaviour equationBehaviour = element.getKernel().getEquationBehaviour();
		if (element instanceof LinearEquationRepresentable) {
			boolean isUserInput = element.getParentAlgorithm() == null;
			if (isUserInput) {
				return equationBehaviour.getLinearAlgebraInputEquationForm() != null
						&& !equationBehaviour.allowsChangingEquationFormsByUser();
			}
			if (element instanceof GeoLine) {
				AlgoElement algo = element.getParentAlgorithm();
				boolean isFitLineOuput = (algo instanceof AlgoFitLineX)
						|| (algo instanceof AlgoFitLineY);
				if (isFitLineOuput) {
					return equationBehaviour.getFitLineCommandEquationForm() != null
							&& !equationBehaviour.allowsChangingEquationFormsByUser();
				}
				return equationBehaviour.getLineCommandEquationForm() != null
						&& !equationBehaviour.allowsChangingEquationFormsByUser();
			}
		}
		return false;
	}
}
