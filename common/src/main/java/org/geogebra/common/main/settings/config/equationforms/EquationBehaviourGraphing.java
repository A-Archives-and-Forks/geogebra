package org.geogebra.common.main.settings.config.equationforms;

/**
 * Equation behaviour for the standalone Graphing app.
 *
 * @See <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Standalone Graphing Wiki page</a>
 */
public final class EquationBehaviourGraphing extends DefaultEquationBehaviour {

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return false;
	}
}
