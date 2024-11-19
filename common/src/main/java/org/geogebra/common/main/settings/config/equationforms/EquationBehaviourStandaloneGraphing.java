package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.ConicEquationRepresentable;

/**
 * Equation behaviour for the standalone Graphing app.
 *
 * @See <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Standalone Graphing Wiki page</a>
 */
public final class EquationBehaviourStandaloneGraphing extends DefaultEquationBehaviour {

	/**
	 * From <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Wiki</a>:
	 * "When manually entered, Lines, Conics, Implicit Equations and Functions are restricted to
	 * user/input form."
	 */
	@Override
	public LinearEquationRepresentable.Form getLinearAlgebraInputEquationForm() {
		return LinearEquationRepresentable.Form.USER;
	}

	@Override
	public LinearEquationRepresentable.Form getLineCommandEquationForm() {
		return LinearEquationRepresentable.Form.EXPLICIT;
	}

	@Override
	public LinearEquationRepresentable.Form getRayCommandEquationForm() {
		return LinearEquationRepresentable.Form.USER;
	}

	@Override
	public ConicEquationRepresentable.Form getConicAlgebraInputEquationForm() {
		return ConicEquationRepresentable.Form.IMPLICIT;
	}

	@Override
	public ConicEquationRepresentable.Form getConicCommandEquationForm() {
		return ConicEquationRepresentable.Form.USER;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return false;
	}
}
