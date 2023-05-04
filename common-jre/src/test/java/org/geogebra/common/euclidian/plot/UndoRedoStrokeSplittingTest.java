package org.geogebra.common.euclidian.plot;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.BaseControllerTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBarSelection;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Before;
import org.junit.Test;

public class UndoRedoStrokeSplittingTest extends BaseControllerTest {

	@Before
	public void setupApp() {
		getApp().setConfig(new AppConfigNotes());
		getApp().setUndoActive(true);
	}

	public void drawStroke() {
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(100, 100);
		dragEnd(400, 100);
	}

	public void drawTwoStrokes(){
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(100, 100);
		dragEnd(400, 100);
		dragStart(220, 100);
		dragEnd(300, 100);
	}

	public void selectPartOfStroke() {
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		dragStart(150, 150);
		dragEnd(250, 50);
	}

	public void selectTwoStrokes() {
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		dragStart(150, 150);
		dragEnd(250, 50);
	}


	@Test
	public void splitStroke() {
		EuclidianController ec = getApp().getActiveEuclidianView().getEuclidianController();
		drawStroke();
		selectPartOfStroke();
		dragStart(250, 50);
		dragEnd(400, 200);
		assertSelected(lookup("stroke2"));
		//assertNotEquals(lookup("stroke1").getDefinition(), lookup("stroke2").getDefinition());
	}


	private void assertSelected(GeoElement... geos) {
		assertArrayEquals(getApp().getSelectionManager().getSelectedGeos().toArray(),
				geos);
	}

	@Test
	public void linePropertiesShouldSplitStroke() {
		EuclidianController ec = getApp().getActiveEuclidianView().getEuclidianController();
		EuclidianStyleBarSelection selection = new EuclidianStyleBarSelection(getApp(), ec);
		drawStroke();
		selectPartOfStroke();
		ArrayList<GeoElement> geos = selection.getGeos();

		EuclidianStyleBarStatic.applyColor(GColor.GREEN, 1, getApp(), geos);
		assertEquals(GColor.GREEN, lookup("stroke2").getObjectColor());
		assertNotEquals(GColor.GREEN, lookup("stroke3").getObjectColor());
	}
}
