package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

public class RatioUnitProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private EuclidianView3D view3D;

	/**
	 * Constructs an ratio unit property.
	 * @param view3D EuclidianView3D
	 * @param localization localization
	 */
	RatioUnitProperty(Localization localization, EuclidianView3D view3D) {
		super(localization, "Settings.Unit");
		this.view3D = view3D;
		setValues(EuclidianView3D.RATIO_UNIT_METERS_CENTIMETERS_MILLIMETERS,
				EuclidianView3D.RATIO_UNIT_INCHES);
		setValueNames("Unit.cm", "Unit.inch");
	}

	@Override
	protected void doSetValue(Integer value) {
		view3D.setARRatioMetricSystem(value);
	}

	@Override
	public Integer getValue() {
		return view3D.getARRatioMetricSystem();
	}

	@Override
	public boolean isEnabled() {
		return view3D.isARRatioShown();
	}
}
