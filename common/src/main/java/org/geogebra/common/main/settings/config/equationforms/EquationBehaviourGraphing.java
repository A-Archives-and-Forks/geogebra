package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Equation behaviour for the standalone Graphing app.
 *
 * @See <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Standalone Graphing Wiki page</a>
 */
public final class EquationBehaviourGraphing extends DefaultEquationBehaviour {

	@Override
	public int getLinearAlgebraInputEquationForm() {
		return GeoLine.EQUATION_USER;
	}

	@Override
	public int getConicAlgebraInputEquationForm() {
		return GeoConicND.EQUATION_USER;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return false;
	}
}
