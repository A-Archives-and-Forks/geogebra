package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;

public class ImplicitCurveData {
	private final List<GPoint2D> output = new ArrayList<>();
	private final List<BoxEdge> edges = new ArrayList<>();
	private final List<BoxEdge> intersects = new ArrayList<>();
	private GPoint2D pixelInRW = new GPoint2D();

	public void addOutput(GPoint2D point) {
		output.add(point);
	}

	public void addEdge(BoxEdge edge) {
		edges.add(edge);
	}

	public void addIntersect(BoxEdge edge) {
		intersects.add(edge);
	}

	public void clear() {
		output.clear();
		edges.clear();
		intersects.clear();
	}

	public void setPixelInRW(double x, double y) {
		pixelInRW.setLocation(x, y);
	}

	public final List<GPoint2D> output() {
		return output;
	}

	public final List<BoxEdge> edges() {
		return edges;
	}

	public final List<BoxEdge> intersects() {
		return intersects;
	}

	public final GPoint2D pixelInRW() {
		return pixelInRW;
	}
}
