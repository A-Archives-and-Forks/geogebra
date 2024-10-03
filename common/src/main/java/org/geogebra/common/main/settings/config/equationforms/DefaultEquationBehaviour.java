package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.EquationForm;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Equation behavior that doesn't apply any customization to the default equation forms.
 *
 * @apiNote Subclass and override for app-specific differences.
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation form matrix"</a>.
 */
public class DefaultEquationBehaviour implements EquationBehaviour {

	@Override
	public int getLinearAlgebraInputEquationForm() {
		return -1;
	}

	@Override
	public int getLineCommandEquationForm() {
		return -1;
	}

	@Override
	public int getFitLineCommandEquationForm() {
		return EquationForm.Linear.EXPLICIT;
	}

	@Override
	public int getRayCommandEquationForm() {
		return -1;
	}

	@Override
	public int getConicAlgebraInputEquationForm() {
		return -1;
	}

	@Override
	public int getConicCommandEquationForm() {
		return -1;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return true;
	}
}
