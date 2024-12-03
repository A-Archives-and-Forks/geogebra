package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class LabelStyleProperty extends AbstractValuedProperty<Integer>
		implements ValuedProperty<Integer> {
	private final Kernel kernel;
	private final GeoElement element;

	/**
	 * Constructs an angle unit property.
	 * @param localization localization
	 */
	public LabelStyleProperty(Localization localization, Kernel kernel, GeoElement element) {
		super(localization, "");
		this.kernel = kernel;
		this.element = element;
	}

	@Override
	protected void doSetValue(Integer value) {
		element.setLabelVisible(value != -1);
		element.setLabelMode(value);
		element.updateVisualStyle(GProperty.LABEL_STYLE);
		kernel.notifyRepaint();
	}

	@Override
	public Integer getValue() {
		return element.getLabelMode();
	}
}
