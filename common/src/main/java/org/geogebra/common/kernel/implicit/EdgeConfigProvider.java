package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

public interface EdgeConfigProvider {
	EdgeConfig create(PlotRect rect);

	MyPoint[] getPoints();
}
