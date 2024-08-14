package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.Splittable;

public interface BoxEdge extends Splittable<BoxEdge> {
	double length();

	boolean mightHaveSolutions();

	GPoint2D startPoint();

	boolean isDerivativeSignDiffer();
}
