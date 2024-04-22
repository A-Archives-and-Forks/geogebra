package org.geogebra.common.properties;

/**
 * A settings property of the GeoGebra App.
 */
public interface Property {

    /**
     * Returns the localized name of the property.
     *
     * @return the name of the property
     *
     * TODO rename to getLocalizedName()
     */
    String getName();

    /**
     * @return The raw, unlocalized name of the property.
     */
    String getRawName();

    /**
     * Returns whether the property is enabled. In some cases
     * properties can depend on other settings, so they may be disabled.
     *
     * @return true iff the property is enabled
     */
    boolean isEnabled();

    boolean isFrozen();

    void setFrozen(boolean frozen);
}
