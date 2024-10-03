package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

public interface PlotRectConfig {

	int flag();

	MyPoint[] getPoints(PlotRect r);

	double getQ1(PlotRect r);

	double getQ2(PlotRect r);

	boolean isValid();

	boolean isInvalid();

	boolean isEmpty();
}
