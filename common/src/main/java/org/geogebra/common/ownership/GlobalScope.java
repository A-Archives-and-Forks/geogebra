package org.geogebra.common.ownership;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A container for objects with global lifetime, i.e., objects that may live from
 * host app launch until host app termination.
 * <p/>
 * <i>Note: By "host app", we mean the iOS/Android/Web app that hosts the GeoGebra code.</i>
 * <p/>
 * This container serves as a home for objects with global lifetime that don't have a
 * direct owner (i.e., no "parent"). Try to put as few objects as possible in here.
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
public final class GlobalScope {

	// Note: source order is initialization order!
	// https://stackoverflow.com/questions/4446088/java-in-what-order-are-static-final-fields-initialized

	public static final PropertiesRegistry propertiesRegistry = new DefaultPropertiesRegistry();

	// intentionally assignable (for testing)
	public static ExamController examController = new ExamController(propertiesRegistry);

	/**
	 * Flag indicating whether the current build is in production or in development.
	 * When {@code true}, certain features may be disabled to prevent their usage in production.
	 */
	private static final boolean isReleaseBuild = false;

	/**
	 * Prevent instantiation.
	 */
	private GlobalScope() {
	}

	/**
	 * Checks whether a given feature is enabled, considering the build type.
	 *
	 * @param previewFeature The feature to check.
	 * @return {@code true} if the feature is enabled
	 * and the build is in development, {@code false} otherwise.
	 */
	public static boolean isFeatureEnabled(PreviewFeature previewFeature) {
		return previewFeature.isEnabled && !isReleaseBuild;
	}
}
