package org.geogebra.common.exam;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.PROBABILITY_APPCODE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.FeaturePreview;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.restriction.ExamRestrictionModel;

public enum ExamType {

	GENERIC() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return loc.getMenu(config.getAppTransKey());
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			String shortAppName = config.getAppCode().equals(GeoGebraConstants.SUITE_APPCODE)
					? GeoGebraConstants.SUITE_SHORT_NAME
					: config.getAppNameShort();
			return loc.getMenu(shortAppName);
		}

		@Deprecated
		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			// no specific restrictions
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			// no restrictions -> no default needed
		}
	},

	REALSCHULE() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Bayern Realschulrechner";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Realschule";
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			// deprecated, will be removed
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			// deprecated, will be removed
		}
	},

	CVTE() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "CvTE goedgekeurde examenstand";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "CvTE";
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			// deprecated, will be removed
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			// deprecated, will be removed
		}
	},

	MMS() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Deutschland IQB MMS Abitur";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "MMS Abitur";
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			// deprecated, will be removed
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			// deprecated, will be removed
		}
	},

	IB() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "IB Exam";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "IB Exam";
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			// deprecated, will be removed
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			// deprecated, will be removed
		}
	},

	NIEDERSACHSEN() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Niedersachsen Abitur";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Niedersachsen";
		}

		@Deprecated
		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setRestrictedSubAppCodes(G3D_APPCODE);
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			model.setDefaultAppCode(GRAPHING_APPCODE);
		}
	},

	BAYERN_CAS() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Schulversuch CAS in Pr\u00FCfungen";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Schulversuch CAS";
		}

		@Deprecated
		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setRestrictedSubAppCodes(GRAPHING_APPCODE, GEOMETRY_APPCODE, G3D_APPCODE,
					PROBABILITY_APPCODE);
			model.setCommandFilter(CommandFilterFactory.createBayernCasFilter());
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			model.setDefaultAppCode(CAS_APPCODE);
		}
	},

	VLAANDEREN() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Vlaanderen";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Vlaanderen";
		}

		@Deprecated
		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setRestrictedSubAppCodes(CAS_APPCODE);
			model.setCommandFilter(CommandFilterFactory.createVlaanderenFilter());
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			model.setDefaultAppCode(GRAPHING_APPCODE);
		}
	};

	public static final String CHOOSE = "choose";

	/**
	 * Case-insensitive version of valueOf
	 * @param shortName exam name
	 * @return exam region
	 */
	public static ExamType byName(String shortName) {
		for (ExamType region : values()) {
			if (region.name().equalsIgnoreCase(shortName)) {
				return region;
			}
		}
		return null;
	}

	/**
	 * Returns the available exam types. Filters based on the AppConfig and the available Features.
	 * Sorts the result based on {@link ExamType#getDisplayName(Localization, AppConfig)}, making
	 * sure that {@link ExamType#GENERIC} is always first.
	 * @param app application
	 * @return available exam types
	 */
	public static List<ExamType> getAvailableExamTypes(App app) {
		AppConfig config = app.getConfig();
		Localization localization = app.getLocalization();
		if (!config.getAppCode().equals(GeoGebraConstants.SUITE_APPCODE)) {
			return Collections.emptyList();
		}
		List<ExamType> examTypes = new ArrayList<>();
		examTypes.add(GENERIC);
		if (app.isPreviewEnabled(FeaturePreview.REALSCHULE_EXAM)) {
			examTypes.add(REALSCHULE);
		}
		if (app.isPreviewEnabled(FeaturePreview.CVTE_EXAM)) {
			examTypes.add(CVTE);
		}
		if (app.isPreviewEnabled(FeaturePreview.MMS_EXAM)) {
			examTypes.add(MMS);
		}
		if (app.isPreviewEnabled(FeaturePreview.IB_EXAM)) {
			examTypes.add(IB);
		}
		examTypes.add(NIEDERSACHSEN);
		examTypes.add(VLAANDEREN);
		examTypes.add(BAYERN_CAS);

		examTypes.sort(new Comparator<ExamType>() {
			@Override
			public int compare(ExamType o1, ExamType o2) {
				if (o1 == ExamType.GENERIC) {
					return -1;
				} else if (o2 == ExamType.GENERIC) {
					return 1;
				} else {
					return o1.getDisplayName(localization, config)
							.compareTo(o2.getDisplayName(localization, config));
				}
			}
		});
		return examTypes;
	}

	public abstract String getDisplayName(Localization loc, AppConfig config);

	public abstract String getShortDisplayName(Localization loc, AppConfig config);

	@Deprecated
	public abstract void applyRestrictions(ExamRestrictionModel model);

	@Deprecated
	public abstract void setDefaultSubAppCode(ExamRestrictionModel model);
}
