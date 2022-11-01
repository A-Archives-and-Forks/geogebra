package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

public class EnableDynamicCaptionModel extends BooleanOptionModel {

	public EnableDynamicCaptionModel(App app) {
		super(app);
	}

	private GeoElementND at(int index) {
		return (GeoElementND) getObjectAt(index);
	}

	@Override
	public boolean getValueAt(int index) {
		return at(index).hasDynamicCaption();
	}

	@Override
	public boolean isValidAt(int index) {
		return getObjectAt(index) instanceof GeoElementND;
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElementND asGeoText = at(index);
		if (value) {
			asGeoText.clearDynamicCaption();
		} else {
			asGeoText.removeDynamicCaption();
		}

		asGeoText.updateRepaint();
	}

	@Override
	public String getTitle() {
		return "UseTextAsCaption";
	}
}
