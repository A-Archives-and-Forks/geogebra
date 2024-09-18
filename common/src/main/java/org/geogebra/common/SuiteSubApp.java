package org.geogebra.common;

import static org.geogebra.common.GeoGebraConstants.*;

import javax.annotation.Nullable;

public enum SuiteSubApp {
	GRAPHING(GRAPHING_APPCODE),
	GEOMETRY(GEOMETRY_APPCODE),
	G3D(G3D_APPCODE),
	CAS(CAS_APPCODE),
	PROBABILITY(PROBABILITY_APPCODE),
	SCIENTIFIC(SCIENTIFIC_APPCODE);

	public final String appCode;

	SuiteSubApp(String appCode) {
		this.appCode = appCode;
	}

	public static @Nullable SuiteSubApp parse(@Nullable String appCode) {
		switch (appCode) {
		case GRAPHING_APPCODE: return GRAPHING;
		case GEOMETRY_APPCODE: return GEOMETRY;
		case G3D_APPCODE: return G3D;
		case CAS_APPCODE: return CAS;
		case PROBABILITY_APPCODE: return PROBABILITY;
		case SCIENTIFIC_APPCODE: return SCIENTIFIC;
		default: return null;
		}
	}
}
