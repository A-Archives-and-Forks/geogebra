package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.dropdown.DrawDropDownList;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Before;
import org.junit.Test;

public class DrawDropdownListTest extends BaseUnitTest {

	@Before
	public void setUp() {
		getApp().set3dConfig();
	}

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void dropdownShouldSelectFirstItem() {
		GeoList dropdown = add("{1,2,3}");
		dropdown.setDrawAsComboBox(true);
		dropdown.setEuclidianVisible(true);
		dropdown.updateRepaint();
		DrawableND drawableFor = getApp().getActiveEuclidianView().getDrawableFor(dropdown);
		assertNotNull(drawableFor);
		DrawDropDownList dropDownList = (DrawDropDownList) drawableFor;
		assertEquals(0, dropDownList.getOptionCount());
		dropDownList.toggleOptions();
		dropDownList.draw(getApp().getActiveEuclidianView().getGraphicsForPen());
		assertEquals(3, dropDownList.getOptionCount());
	}

	@Test
	public void emptyStringShouldBeValidElement() {
		GeoList dropdown = add("{\"a\", \"\", \"c\"}");
		dropdown.setDrawAsComboBox(true);
		dropdown.setEuclidianVisible(true);
		dropdown.updateRepaint();
		DrawableND drawableFor = getApp().getActiveEuclidianView().getDrawableFor(dropdown);
		assertNotNull(drawableFor);
		DrawDropDownList dropDownList = (DrawDropDownList) drawableFor;
		assertEquals(0, dropDownList.getOptionCount());
		dropDownList.toggleOptions();
		dropDownList.draw(getApp().getActiveEuclidianView().getGraphicsForPen());
		assertEquals(3, dropDownList.getOptionCount());
	}

	@Test
	public void createdTextItemsShouldNotBe3DVisible() {
		GeoList dropdown = add("{\"a\", \"b\", \"c\"}");
		dropdown.setDrawAsComboBox(true);
		assertFalse(dropdown.get(0).isVisibleInView3D());
   }
 }
