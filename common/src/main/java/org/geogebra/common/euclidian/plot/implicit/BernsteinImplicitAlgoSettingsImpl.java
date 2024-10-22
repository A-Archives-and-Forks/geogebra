package org.geogebra.common.euclidian.plot.implicit;

class BernsteinImplicitAlgoSettingsImpl
		implements BernsteinImplicitAlgoSettings {
	private int boxSize = 5;
	private int edgeSize = 2;

	@Override
	public int minBoxWidthInPixels() {
		return boxSize;
	}

	@Override
	public int minBoxHeightInPixels() {
		return boxSize;
	}

	@Override
	public int minEdgeWidth() {
		return edgeSize;
	}

	public void setBoxSize(int size) {
		this.boxSize = size;
	}

	public void setEdgeSize(int size) {
		edgeSize = size;
	}
}
