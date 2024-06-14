package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Test;

public class AlgoIfTest extends BaseUnitTest {

	@Test
	public void listTypeShouldFollowNonEmptyAlternative() {
		GeoList list = add("If(true,{},If(true,{},{\"text\"}))");
		assertEquals("text", list.getTypeStringForXML());
	}

	@Test
	public void shouldPreserveVarOrder() {
		add("sliderVal=1");
		GeoFunctionNVar fun = add("f(x,y,a,b,c) = If(sliderVal==1, x + 0y +a + 0b + c, "
				+ "sliderVal==2, 0x+ y^2 + 0a +2b +0c, x + y + 0a + b +0c)");
		assertThat(fun.getVarString(StringTemplate.defaultTemplate),
				equalTo("x, y, a, b, c"));
		reload();
		assertThat(((GeoFunctionNVar) lookup("f")).getVarString(StringTemplate.defaultTemplate),
				equalTo("x, y, a, b, c"));
	}
}
