package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.SegmentStyle;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SegmentPropertyDelegate;

public class SegmentStartProperty extends AbstractEnumeratedProperty<SegmentStyle>
		implements IconsEnumeratedProperty<SegmentStyle> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_SEGMENT_START_DEFAULT, PropertyResource.ICON_SEGMENT_START_LINE,
			PropertyResource.ICON_SEGMENT_START_SQUARE_OUTLINE,
			PropertyResource.ICON_SEGMENT_START_SQUARE, PropertyResource.ICON_SEGMENT_START_ARROW,
			PropertyResource.ICON_SEGMENT_START_ARROW_FILLED,
			PropertyResource.ICON_SEGMENT_START_CIRCLE_OUTLINE,
			PropertyResource.ICON_SEGMENT_START_CIRCLE
	};

	private final GeoElementDelegate delegate;

	/**
	 * Constructs an AbstractEnumeratedProperty.
	 * @param localization the localization used
	 * @param element the name of the property
	 */
	public SegmentStartProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.LineStartStyle");
		delegate = new SegmentPropertyDelegate(element);
		setValues(SegmentStyle.DEFAULT, SegmentStyle.LINE, SegmentStyle.SQUARE_OUTLINE,
				SegmentStyle.SQUARE, SegmentStyle.ARROW, SegmentStyle.ARROW_FILLED,
				SegmentStyle.CIRCLE_OUTLINE, SegmentStyle.CIRCLE);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(SegmentStyle value) {
		GeoSegment element = (GeoSegment) delegate.getElement();
		element.setStartStyle(value);
		element.updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public SegmentStyle getValue() {
		return ((GeoSegment) delegate.getElement()).getStartStyle();
	}
}

