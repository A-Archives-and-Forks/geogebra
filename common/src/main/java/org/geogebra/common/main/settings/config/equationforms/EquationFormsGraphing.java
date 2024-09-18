package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationForms;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;

// TODO fill in correct values
// https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?usp=sharing
public class EquationFormsGraphing implements EquationForms {

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
		return GeoLine.EQUATION_USER;
	}

	@Override
	public int getConicEquationForm() {
		return GeoConic.EQUATION_USER;
	}
}
