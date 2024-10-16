package org.geogebra.common.properties.impl.objects;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.ColorValues;
import org.geogebra.common.main.color.GeoColorValues;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.ElementColorPropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Color property
 */
public class ElementColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {

	private final GeoElement element;
	private final GeoElementDelegate delegate;

	/**
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public ElementColorProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.Color");
		this.element = element;
		delegate = new ElementColorPropertyDelegate(element);
		setValues(createColorValues());
	}

	@Override
	public GColor getValue() {
		return element.getObjectColor();
	}

	@Override
	public void doSetValue(GColor color) {
		EuclidianStyleBarStatic.applyColor(color, element.getAlphaValue(), element.getApp(),
				List.of(element));
	}

	@Override
	public boolean isEnabled() {
		return element.isEuclidianVisible() && delegate.isEnabled();
	}

	private List<GColor> createColorValues() {
		ColorValues[] colorValues = GeoColorValues.values();
		List<GColor> colorList = new ArrayList<>();

		for (ColorValues value : colorValues) {
			colorList.add(value.getColor());
		}

		return colorList;
	}
}
