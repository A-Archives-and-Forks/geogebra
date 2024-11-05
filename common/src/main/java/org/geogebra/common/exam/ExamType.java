package org.geogebra.common.exam;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;

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
	};

	public static final String CHOOSE = "choose";

	/**
	 * Case-insensitive version of valueOf, returns null if name is invalid or null
	 * @param shortName exam name
	 * @return exam type or null
	 */
	public static ExamType byName(@Nullable String shortName) {
		for (ExamType region: values()) {
			if (region.name().equalsIgnoreCase(shortName)) {
				return region;
			}
		}
		return null;
	}

	public abstract String getDisplayName(Localization loc, AppConfig config);

	public abstract String getShortDisplayName(Localization loc, AppConfig config);

	/**
	 * List of exam types sorted by localized names (except GENERIC goes first)
	 * @param loc localization
	 * @param config app config
	 * @return available types
	 */
	public static List<ExamType> getAvailableValues(Localization loc, AppConfig config) {
		Comparator<ExamType> genericFirst = Comparator.comparing(type -> !GENERIC.equals(type));
		return Arrays.stream(values()).filter(ExamType::isAvailable)
				.sorted(genericFirst.thenComparing(type -> type.getDisplayName(loc, config)))
				.collect(Collectors.toList());
	}

	private boolean isAvailable() {
		switch (this) {
		case CVTE:
		case MMS:
		case IB:
		case REALSCHULE:
			return true; // TODO feature flag goes here
		default: return true;
		}
	}
}
