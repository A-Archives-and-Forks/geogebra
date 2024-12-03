package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.PlaneEqnModel;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.AlgoFitLineX;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;

public class LinearEquationFormDelegate extends AbstractGeoElementDelegate {

	public LinearEquationFormDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
//		if (isTextOrInput(element)) { // TODO why this check?
//			return false;
//		}
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		return hasEquationModeSetting(element);
	}

	private boolean hasEquationModeSetting(GeoElement element) {
//		if (element.isGeoSegment()) { // TODO takene from LineEqnModel - needed?
//			return false;
//		}
		return (element.isGeoLine() || element.isGeoPlane())
				&& !isEnforcedEquationForm(element)
//				&& !element.isNumberValue() // TODO needed?
				&& element.getDefinition() == null; // TODO why?
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
