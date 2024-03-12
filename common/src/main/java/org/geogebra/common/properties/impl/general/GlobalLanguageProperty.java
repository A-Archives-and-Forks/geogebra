package org.geogebra.common.properties.impl.general;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.util.lang.Language;

public class GlobalLanguageProperty extends AbstractNamedEnumeratedProperty<String> {

	private String value;

	/**
	 * Create a new instance.
	 * @param localization The localization.
	 * @param propertiesRegistry A properties registry to register this property with (optional).
	 */
	public GlobalLanguageProperty(@Nonnull Localization localization,
			@Nullable PropertiesRegistry propertiesRegistry) {
		super(localization, "Language", null);
		if (propertiesRegistry != null) {
			propertiesRegistry.register(this, null); // register in global context
		}
		setupValues(localization);
	}

	private void setupValues(Localization localization) {
		Language[] languages = localization.getSupportedLanguages(false);
		String[] valueNames = new String[languages.length];
		String[] values = new String[languages.length];
		for (int i = 0; i < languages.length; i++) {
			Language language = languages[i];
			valueNames[i] = language.name;
			values[i] = language.toLanguageTag();
		}
		setValueNames(valueNames);
		setValues(values);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	protected void doSetValue(String value) {
		this.value = value;
	}
}
