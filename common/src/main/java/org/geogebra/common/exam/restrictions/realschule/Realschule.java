package org.geogebra.common.exam.restrictions.realschule;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.AlgoFitLineX;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;
import org.geogebra.common.kernel.statistics.FitAlgo;

/**
 * Shared logic for Realschule exam restrictions.
 */
public class Realschule {

	@SuppressWarnings("PMD.SimplifyBooleanReturns")
	static boolean isCalculatedEquationAllowed(@Nullable GeoElementND element) {
		if (element == null) {
			return false;
		}
		if (isAllowedFitCommand(element.getParentAlgorithm())) {
			return true;
		}
		// is Line, Ray, Conic, Implicit Equation or Function, ...
		if ((element.isGeoLine()
				|| element.isGeoRay()
				|| element.isGeoConic()
				|| element.isGeoFunction()
				|| element.isImplicitEquation())
				// ...created with a command or tool;
				&& (element.getParentAlgorithm() != null)) {
			return false;
		}
		return true;
	}

	private static boolean isAllowedFitCommand(@CheckForNull AlgoElement algo) {
		if (algo == null) {
			return false;
		}
		return algo instanceof FitAlgo || algo instanceof AlgoFitLineX
				|| algo instanceof AlgoFitLineY;
	}
}
