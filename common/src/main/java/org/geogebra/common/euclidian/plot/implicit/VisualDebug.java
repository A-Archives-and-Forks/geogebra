package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.awt.GGraphics2D;

public interface VisualDebug<T> {
	void draw(GGraphics2D g2);

	void setData(List<T> data);
}
