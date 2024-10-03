package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationForm;

/**
 * Equation behaviour for the standalone Graphing app.
 *
 * @See <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Standalone Graphing Wiki page</a>
 */
public final class EquationBehaviourStandaloneGraphing extends DefaultEquationBehaviour {

	@Override
	public int getLinearAlgebraInputEquationForm() {
		return EquationForm.Linear.USER;
	}

	@Override
	public int getLineCommandEquationForm() {
		return EquationForm.Linear.EXPLICIT;
	}

	@Override
	public int getRayCommandEquationForm() {
		return EquationForm.Linear.USER;
	}

	@Override
	public int getConicAlgebraInputEquationForm() {
		return EquationForm.Quadric.USER;
	}

	@Override
	public int getConicCommandEquationForm() {
		return EquationForm.Quadric.USER;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return false;
	}
}
