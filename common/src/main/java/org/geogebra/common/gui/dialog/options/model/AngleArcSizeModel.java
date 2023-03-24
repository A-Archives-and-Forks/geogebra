package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

public class AngleArcSizeModel extends OptionsModel {
	public static final Integer MIN_VALUE = 20;
	private ISliderListener listener;

	public AngleArcSizeModel(App app) {
		super(app);
	}

	public void setListener(ISliderListener listener) {
		this.listener = listener;
	}

	private AngleProperties getAngleAt(int index) {
		return (AngleProperties) getObjectAt(index);
	}

	public void applyChanges(int size) {
		for (int i = 0; i < getGeosLength(); i++) {
			AngleProperties angle = getAngleAt(i);
			// check if decoration could be drawn
			angle.setArcSize(Math.max(size, getMinSizeForDecoration(angle)));
			angle.updateVisualStyleRepaint(GProperty.ANGLE_STYLE);
		}
	}

	public static int getMinSizeForDecoration(AngleProperties angle) {
		return (angle
				.getDecorationType() == GeoElementND.DECORATION_ANGLE_THREE_ARCS
				|| angle.getDecorationType() == GeoElementND.DECORATION_ANGLE_TWO_ARCS) ? 20 : 10;
	}

	@Override
	public void updateProperties() {
		int min = 10;
		for (GeoElement geo: getGeosAsList()) {
			if (geo instanceof AngleProperties) {
				min = Math.max(min, getMinSizeForDecoration((AngleProperties) geo));
			}
		}
		listener.setSliderMin(min);
		listener.setValue(Math.max(getAngleAt(0).getArcSize(), min));
	}

	@Override
	public boolean isValidAt(int index) {
		return match(getGeoAt(index));
	}

	/**
	 * @param geo
	 *            The geo to math returns true if geo meets the requirements of
	 *            this model
	 */
	public static boolean match(GeoElement geo) {
		if (geo instanceof AngleProperties) {
			AngleProperties angle = (AngleProperties) geo;
			if (angle.isIndependent() || !angle.isDrawable()) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
