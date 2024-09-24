package org.geogebra.common.main.settings.updater;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoLine;

// TODO remove entirely / this is not needed
public class GraphingSettingsUpdater extends SettingsUpdater {

	@Override
	public void resetSettingsAfterClearAll() {
		super.resetSettingsAfterClearAll();
		setExplicitEquationModeForDefaultLine();
	}

	private void setExplicitEquationModeForDefaultLine() {
		ConstructionDefaults defaults = getKernel().getConstruction().getConstructionDefaults();
		GeoLine line = (GeoLine) defaults.getDefaultGeo(ConstructionDefaults.DEFAULT_LINE);
		line.setMode(GeoLine.EQUATION_EXPLICIT); // this is not used anywhere
	}
}
