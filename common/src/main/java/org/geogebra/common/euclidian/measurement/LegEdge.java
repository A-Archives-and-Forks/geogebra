package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Represents the leg edges of a right triangle.
 */
public final class LegEdge implements MeasurementToolEdge {
	private GeoPoint[] endpoints;

	// GeoImage corners 3 and 4.
	private GeoPoint corner3;
	private GeoPoint corner4;
	private Legs leg;

	/**
	 *
	 * @param leg to specify which leg it is.
	 */
	public LegEdge(Legs leg) {
		this.leg = leg;
	}

	@Override
	public GeoPoint endpoint1() {
		return endpoints[0];
	}

	@Override
	public GeoPoint endpoint2() {
		return endpoints[1];
	}

	@Override
	public void update(GeoImage image) {
		ensureCorners(image.getKernel().getConstruction());
		image.calculateCornerPoint(corner3, 3);
		image.calculateCornerPoint(corner4, 4);
		int i = leg.index() - 1;
		endpoints[i].x = (corner3.x + corner4.x) / 2;
		endpoints[i].y = (corner3.y + corner4.y) / 2;
		endpoints[i].z = 1;
		image.calculateCornerPoint(endpoints[1 - i], leg.index());
		endpoints[0].updateCoords();
		endpoints[1].updateCoords();
	}

	private void ensureCorners(Construction cons) {
		if (endpoints == null) {
			endpoints = new GeoPoint[2];
			endpoints[0] = new GeoPoint(cons, true);
			endpoints[1] = new GeoPoint(cons, true);
			corner3 = new GeoPoint(cons, true);
			corner4 = new GeoPoint(cons, true);
		}
	}
}
