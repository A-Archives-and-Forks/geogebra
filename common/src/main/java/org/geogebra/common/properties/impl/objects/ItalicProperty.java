package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;

public class ItalicProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final GeoElementDelegate delegate;

	public ItalicProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Italic");
		delegate = new TextFormatterDelegate(element);
	}

	@Override
	protected void doSetValue(Boolean value) {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		if (getLocalization() != null && !value.equals(element.getFormatter()
				.getFormat("italic", false))) {
			element.getFormatter().format("italic", value);
		}
		((GeoElement) element).updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return ((HasTextFormat) delegate.getElement()).getFormat("italic", false);
	}
}
