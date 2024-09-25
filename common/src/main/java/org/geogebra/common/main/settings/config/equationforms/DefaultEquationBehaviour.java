package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Common behaviour across all apps for equation forms.
 *
 * @apiNote Subclass and override for app-specific differences.
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation form matrix"</a>.
 */
public class DefaultEquationBehaviour implements EquationBehaviour {

	@Override
	public int getLinearAlgebraInputEquationForm() {
		return GeoLine.EQUATION_USER;
	}

	@Override
	public int getLineCommandEquationForm() {
		return GeoLine.EQUATION_EXPLICIT;
	}

	@Override
	public int getRayCommandEquationForm() {
		return GeoLine.EQUATION_USER;
	}

	@Override
	public int getFitLineCommandEquationForm() {
		return GeoLine.EQUATION_EXPLICIT;
	}

	@Override
	public int getConicAlgebraInputEquationForm() {
		return GeoConicND.EQUATION_USER;
	}

	@Override
	public int getConicCommandEquationForm() {
		return GeoConicND.EQUATION_IMPLICIT;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return true;
	}
}
