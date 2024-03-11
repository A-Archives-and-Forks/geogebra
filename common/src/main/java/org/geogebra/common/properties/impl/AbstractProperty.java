package org.geogebra.common.properties.impl;

import javax.annotation.Nullable;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;

/**
 * Helper class for implementing the localized name of a property.
 */
public abstract class AbstractProperty implements Property {

	private Localization localization;
	private String name;
	private boolean frozen = false;

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public AbstractProperty(Localization localization, String name) {
		this.localization = localization;
		this.name = name;
	}

	/**
	 * Constructs a new property.
	 *
	 * @param localization Used for localizing the property's name.
	 * @param name The property's name.
	 * @param propertiesRegistry A {@link PropertiesRegistry} (may be null). If a registry is
	 * passed in, the newly created property will register itself with the registry.
	 */
	public AbstractProperty(Localization localization, String name,
			@Nullable PropertiesRegistry propertiesRegistry) {
		this(localization, name);
		if (propertiesRegistry != null) {
			propertiesRegistry.register(this);
		}
	}

	@Override
	public String getName() {
		return localization.getMenu(name);
	}

	@Override
	public String getRawName() {
		return name;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Returns the localization of the class.
	 * @return localization used
	 */
	protected Localization getLocalization() {
		return localization;
	}

	@Override
	public boolean isFrozen() {
		return frozen;
	}

	@Override
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
}
