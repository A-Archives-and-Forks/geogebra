package org.geogebra.common.exam;

import static java.util.Comparator.comparing;
import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.PROBABILITY_APPCODE;
import static org.geogebra.common.main.FeatureFlag.CVTE_EXAM;
import static org.geogebra.common.main.FeatureFlag.IB_EXAM;
import static org.geogebra.common.main.FeatureFlag.MMS_EXAM;
import static org.geogebra.common.main.FeatureFlag.REALSCHULE_EXAM;
import static org.geogebra.common.ownership.GlobalScope.isFeatureEnabled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
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
		if (!config.getAppCode().equals(GeoGebraConstants.SUITE_APPCODE)) {
			return Collections.emptyList();
		}

		ArrayList<ExamType> examTypes = Arrays.stream(ExamType.values())
				.collect(Collectors.toCollection(ArrayList::new));
		if (!isFeatureEnabled(REALSCHULE_EXAM)) {
			examTypes.remove(REALSCHULE);
		}
		if (!isFeatureEnabled(CVTE_EXAM)) {
			examTypes.remove(CVTE);
		}
		if (!isFeatureEnabled(MMS_EXAM)) {
			examTypes.remove(MMS);
		}
		if (!isFeatureEnabled(IB_EXAM)) {
			examTypes.remove(IB);
		}

		// Sort exam types by display name, ignoring the GENERIC type, which is always first
		ArrayList<ExamType> sortedExamTypes = new ArrayList<>();
		sortedExamTypes.add(GENERIC);
		sortedExamTypes.addAll(examTypes.subList(1, examTypes.size()).stream().sorted(
				comparing(examType -> examType.getDisplayName(app.getLocalization(), config))
		).collect(Collectors.toList()));

		return sortedExamTypes;
	}

	public abstract String getDisplayName(Localization loc, AppConfig config);

	public abstract String getShortDisplayName(Localization loc, AppConfig config);

	@Deprecated
	public abstract void applyRestrictions(ExamRestrictionModel model);

	@Deprecated
	public abstract void setDefaultSubAppCode(ExamRestrictionModel model);
}
