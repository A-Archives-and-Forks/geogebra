package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.awt.GColor;

public enum EdgeKind {
	TOP(GColor.YELLOW),
	LEFT(GColor.RED),
	BOTTOM(GColor.GREEN),
	RIGHT(GColor.BLUE);

	private GColor color;

	EdgeKind(GColor color) {
		this.color = color;
	}

	public boolean isHorizontal() {
		return this == TOP || this == BOTTOM;
	}

	public GColor getColor() {
		return color;
	}
}
