package org.geogebra.common.kernel.geos;

/**
 * Contract for setting up or configuring a {@link GeoElement}.
 */
public interface GeoElementSetup {
    /**
     * Sets up or configures the given {@link GeoElement}.
     * @param geoElement The {@link GeoElement} to be configured
     */
    void applyTo(GeoElement geoElement);
}
