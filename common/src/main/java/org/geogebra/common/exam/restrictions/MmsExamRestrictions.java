package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;

final class MmsExamRestrictions extends ExamRestrictions {

	MmsExamRestrictions() {
		super(ExamType.MMS,
				Set.of(SuiteSubApp.GRAPHING, SuiteSubApp.GEOMETRY, SuiteSubApp.G3D),
				SuiteSubApp.CAS,
				Set.of(ExamFeatureRestriction.DATA_TABLE_REGRESSION),
				null,
				null,
				Set.of(CommandFilterFactory.createMmsFilter()),
				null,
				null,
				null,
				null,
				null);
	}
}
