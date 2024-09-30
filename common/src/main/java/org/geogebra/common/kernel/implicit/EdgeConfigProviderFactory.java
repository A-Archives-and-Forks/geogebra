package org.geogebra.common.kernel.implicit;

public class EdgeConfigProviderFactory {
	public static EdgeConfigProvider create(GeoImplicitCurve curve, int factor) {
		return new QuadTreeEdgeConfigProvider(curve, factor);
	}
}
