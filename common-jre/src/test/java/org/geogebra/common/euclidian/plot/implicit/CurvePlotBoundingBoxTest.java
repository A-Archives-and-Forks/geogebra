package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CurvePlotBoundingBoxTest {
	@Test
	public void testSpitBox() {
		CurvePlotBoundingBox box = newBox(-10, 10, -10, 10);
		CurvePlotBoundingBox[] boxes = box.split();
		CurvePlotBoundingBox[] expected = {
				newBox(-10, 0, -10, 0),
				newBox(0, 10, -10,0),
				newBox(-10, 0, 0, 10),
				newBox(0, 10, 0,10)
		};

		assertArrayEquals(expected, boxes);
	}

	private static CurvePlotBoundingBox newBox(double xmin, double xmax, double ymin, double ymax) {
		return new CurvePlotBoundingBox(xmin, ymin, xmax, ymax);
	}
}
