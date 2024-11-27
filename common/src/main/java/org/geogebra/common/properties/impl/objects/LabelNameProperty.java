package org.geogebra.common.properties.impl.objects;

import javax.annotation.Nullable;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class LabelNameProperty extends AbstractValuedProperty<String> implements StringProperty {


	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public LabelNameProperty(Localization localization, String name) {
		super(localization, name);
	}

	@Nullable
	@Override
	public String validateValue(String value) {
		return "";
	}

	@Override
	protected void doSetValue(String value) {

	}

	@Override
	public String getValue() {
		return "";
	}
}
