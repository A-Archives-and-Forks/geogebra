package org.geogebra.common.main.settings.config.equationforms;

/**
 * <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Standalone Graphing Wiki page</a>
 */
public class EquationBehaviourGraphing extends DefaultEquationBehaviour {

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return false;
	}
}
