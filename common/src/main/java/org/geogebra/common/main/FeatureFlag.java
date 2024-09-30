package org.geogebra.common.main;

public enum FeatureFlag {
	ALL_LANGUAGES,
	TUBE_BETA,
	// when moved to stable, move ImplicitSurface[] from TABLE_ENGLISH
	// in Command.Java
	IMPLICIT_SURFACES,
	/** TRAC-4845 */
	LOG_AXES,
	ANALYTICS,
	/** GGB-334, TRAC-3401 */
	ADJUST_WIDGETS(false),
	/** SolveQuartic in CAS GGB-1635 */
	SOLVE_QUARTIC,
	/** MOB-1319 */
	MOB_NOTIFICATION_BAR_TRIGGERS_EXAM_ALERT_IOS_11(false),
	/** MOB-1537 */
	MOB_PREVIEW_WHEN_EDITING,
	/** GGB-2255 */
	GEOMETRIC_DISCOVERY,
	/** G3D-343 */
	G3D_SELECT_META(false),
	/** MOB-1722 */
	MOB_EXPORT_STL,
	/** APPS-4961 */
	CVTE_EXAM,
	/** APPS-4867 */
	MMS_EXAM,
	/** APPS-5641 */
	IB_EXAM,
	/** APPS-5740 */
	REALSCHULE_EXAM;

	private final boolean isEnabled;

	FeatureFlag() {
		this(true);
	}

	FeatureFlag(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isEnabled() {
		return isEnabled;
	}
}
