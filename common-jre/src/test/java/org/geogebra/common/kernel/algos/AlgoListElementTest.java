package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.GeoClass;
import org.junit.Test;

public class AlgoListElementTest extends BaseUnitTest {

	private GeoList list;
	private GeoElement[] output;

	@Test
	public void testSimpleList() {
		GeoList list = add("{1,2,3,4}");
		GeoNumeric index = new GeoNumeric(getConstruction(), 2);
		AlgoListElement algo = new AlgoListElement(getConstruction(), list, index);
		GeoElement[] output = algo.getOutput();
		assertThat(output[0], hasValue("2"));
	}

	@Test
	public void testExistingValueInFlatList() {
		withList("{1,2,3,4}").at(1).shouldHaveValue("1");
	}

	@Test
	public void testTypeInNonExistingIndexFlatList() {
		withList("{(1,1)}").at(2).shouldHaveType(GeoClass.POINT);
	}

	@Test
	public void testEmptyFlatListShouldHaveElementTypeNumeric() {
		withList("{}").at(2).shouldHaveType(GeoClass.NUMERIC);
	}

	private void shouldHaveType(GeoClass geoClass) {
		assertThat(output[0], hasProperty("type",
				GeoElement::getGeoClassType, geoClass));
	}

	private AlgoListElementTest withList(String list) {
		this.list = add(list);
		return this;
	}

	private AlgoListElementTest at(int x) {
		GeoNumeric index = new GeoNumeric(getConstruction(), x);
		AlgoListElement algo = new AlgoListElement(getConstruction(), list, index);
		output = algo.getOutput();
		return this;
	}

	private void shouldHaveValue(String value) {
		assertThat(output[0], hasValue(value));

	}
}
