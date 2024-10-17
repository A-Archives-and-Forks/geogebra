package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

public interface PlotRectConfig {

	int flag();

	MyPoint[] getPoints(PlotRect r);

	boolean isValid();

	boolean isInvalid();

	boolean isEmpty();
}
