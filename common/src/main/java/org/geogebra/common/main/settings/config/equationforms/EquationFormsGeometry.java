package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.EquationForms;

// TODO fill in correct values
public class EquationFormsGeometry implements EquationForms {

	@Override
	public int getDefaultLineEquationForm() {
		return GeoLine.EQUATION_EXPLICIT;
	}

	@Override
	public int getAlgebraicLineEquationForm() {
		return GeoLine.EQUATION_USER;
	}

	@Override
	public int getLineCommandEquationForm() {
		return GeoLine.EQUATION_EXPLICIT;
	}

	@Override
	public int getFitLineCommandEquationForm() {
		return -1;
	}

	@Override
	public int getTangentCommandEquationForm() {
		return -1;
	}

	@Override
	public int getConicEquationForm() {
		return -1;
	}
}
