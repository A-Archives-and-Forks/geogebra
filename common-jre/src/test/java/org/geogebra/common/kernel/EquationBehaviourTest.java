package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Test;

public class EquationBehaviourTest extends BaseUnitTest {

	@Test
	public void testGraphingEquationBehaviour() {
		getApp().setGraphingConfig();

		GeoLine defaultLine = new GeoLine(getConstruction());
		assertEquals(GeoLine.EQUATION_IMPLICIT, defaultLine.getEquationForm());

		// Line created with tool
		GeoPoint pointA = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint pointB = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), pointA, pointB);
		GeoLine toolLine = algoJoinPoints.getLine();
		assertEquals(GeoLine.EQUATION_EXPLICIT, toolLine.getEquationForm());

		// Ray created with tool
		// TODO APPS-5867
	}

	@Test
	public void testGraphingEquationBehaviourWithCustomizedConstructionDefaults() {
		getApp().setGraphingConfig();

		// change the equation form for lines in the construction defaults
		GeoLine constructionDefaultsLine = (GeoLine) getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_LINE);
		constructionDefaultsLine.setEquationForm(GeoLine.EQUATION_GENERAL);

		GeoLine defaultLine = new GeoLine(getConstruction());
		assertEquals(GeoLine.EQUATION_GENERAL, defaultLine.getEquationForm());

		// check if the Graphing equation forms are still satisfied (i.e., overriding the changes
		// to the construction defaults above)
		GeoPoint pointA = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint pointB = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), pointA, pointB);
		GeoLine toolLine = algoJoinPoints.getLine();
		assertEquals(GeoLine.EQUATION_EXPLICIT, toolLine.getEquationForm());
	}

	@Test
	public void testClassicEquationBehaviour() {
		getApp().setDefaultConfig(); // Default = Classic

		GeoLine defaultLine = new GeoLine(getConstruction());
		assertEquals(GeoLine.EQUATION_IMPLICIT, defaultLine.getEquationForm());

		// Line created with tool
		GeoPoint pointA = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint pointB = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), pointA, pointB);
		GeoLine toolLine = algoJoinPoints.getLine();
		assertEquals(GeoLine.EQUATION_IMPLICIT, toolLine.getEquationForm());

		// Ray created with tool
		// TODO APPS-5867
	}

	@Test
	public void testClassicEquationBehaviourWithCustomizedConstructionDefaults() {
		getApp().setDefaultConfig();

		// change the equation form for lines in the construction defaults
		GeoLine constructionDefaultsLine = (GeoLine) getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_LINE);
		constructionDefaultsLine.setEquationForm(GeoLine.EQUATION_GENERAL);

		GeoLine defaultLine = new GeoLine(getConstruction());
		assertEquals(GeoLine.EQUATION_GENERAL, defaultLine.getEquationForm());

		// check if lines created with a tool or command have the equation form as defined in
		// the construction defaults
		GeoPoint pointA = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint pointB = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), pointA, pointB);
		GeoLine toolLine = algoJoinPoints.getLine();
		assertEquals(GeoLine.EQUATION_GENERAL, toolLine.getEquationForm());
	}
}
