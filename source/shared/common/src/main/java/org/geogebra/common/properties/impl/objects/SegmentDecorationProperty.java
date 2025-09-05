package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class SegmentDecorationProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {

	private static List<Integer> values = List.of(
		GeoSegment.getDecoTypes()
	);
	private final GeoElement element;

	/**
	 * Constructs an AbstractEnumeratedProperty.
	 * @param localization the localization used
	 * @param geo construction element
	 */
	public SegmentDecorationProperty(Localization localization, GeoElement geo)
			throws NotApplicablePropertyException {
		super(localization, "Decoration");
		setValues(values);
		if (!(geo instanceof GeoSegment)) {
			throw new NotApplicablePropertyException(geo);
		}
		this.element = geo;
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return new PropertyResource[]{
				PropertyResource.ICON_SEGMENT_DECO_NONE,
				PropertyResource.ICON_SEGMENT_DECO_1STROKE,
				PropertyResource.ICON_SEGMENT_DECO_2STROKES,
				PropertyResource.ICON_SEGMENT_DECO_3STROKES,
				PropertyResource.ICON_SEGMENT_DECO_1ARROW,
				PropertyResource.ICON_SEGMENT_DECO_2ARROWS,
				PropertyResource.ICON_SEGMENT_DECO_3ARROWS
		};
	}

	@Override
	protected void doSetValue(Integer value) {
		element.setDecorationType(value);
		element.updateVisualStyleRepaint(GProperty.DECORATION);
	}

	@Override
	public Integer getValue() {
		return element.getDecorationType();
	}
}
