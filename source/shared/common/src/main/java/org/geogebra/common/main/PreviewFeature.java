package org.geogebra.common.main;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal (preview) features that are visible only in test builds.
 *
 * @apiNote When an internal feature is released, the corresponding enum case has to be removed,
 * together with any if/guard statements.
 */
public enum PreviewFeature {

	ALL_LANGUAGES,
	RESOURCES_API_BETA,
	IMPLICIT_SURFACES,
	/** APPS-5763 */
	IMPLICIT_PLOTTER,
	/** MOB-1537 */
	MOB_PREVIEW_WHEN_EDITING,
	/** SolveQuartic in CAS GGB-1635 */
	SOLVE_QUARTIC,
	/** TRAC-4845 */
	LOG_AXES,
	/** GGB-2255 */
	GEOMETRIC_DISCOVERY,
	/** APPS-4867 */
	MMS_EXAM,
	/** APPS-5641 */
	IB_EXAM,
	WTR_EXAM,
	CREATE_CHART_MENU_ITEM,
	/** APPS-6016 */
	SPREADSHEET_STYLEBAR;

	/**
	 * Global flag to activate feature previews.
	 *
	 * @apiNote Set the {@code true} at run time (early in the app startup code) to enable
	 * feature previews in test builds.
	 */
	@SuppressFBWarnings("MS_PKGPROTECT")
	public static boolean enableFeaturePreviews = false;

	/**
	 * Whether a preview feature is enabled (the default), or not. The latter case can be used
	 * during development, i.e., when an internal feature is being worked on, but not yet considered
	 * ready for internal preview.
	 */
	private final boolean isEnabled;

	PreviewFeature() {
		this(true);
	}

	PreviewFeature(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * Enables preview features. May be called at startup.
	 */
	public static void setPreviewFeaturesEnabled(boolean enabled) {
		enableFeaturePreviews = enabled;
	}

	/**
	 * Checks whether a preview feature is available.
	 * @param previewFeature A preview feature.
	 * @return {@code true} iff the preview feature's {@code isEnabled} flag is {@code true} and
	 * {@link #enableFeaturePreviews} is {@code true} as well; {@code false} otherwise.
	 */
	public static boolean isAvailable(PreviewFeature previewFeature) {
		return enableFeaturePreviews && previewFeature.isEnabled;
	}
}
