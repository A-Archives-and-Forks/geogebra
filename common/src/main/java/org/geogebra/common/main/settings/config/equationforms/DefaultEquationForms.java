package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationForms;
import org.geogebra.common.kernel.geos.GeoLine;

public class DefaultEquationForms implements EquationForms {

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
		return GeoLine.EQUATION_EXPLICIT;
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
