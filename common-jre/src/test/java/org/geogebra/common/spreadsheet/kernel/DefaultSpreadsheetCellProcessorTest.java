package org.geogebra.common.spreadsheet.kernel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.DoubleUtil;
import org.junit.Before;
import org.junit.Test;

public class DefaultSpreadsheetCellProcessorTest extends BaseUnitTest {
	private DefaultSpreadsheetCellProcessor processor;
	private final DefaultSpreadsheetCellDataSerializer
			serializer = new DefaultSpreadsheetCellDataSerializer();

	@Before
	public void setUp() {
		ErrorHandler errorHandler = getApp().getDefaultErrorHandler();
		processor =
				new DefaultSpreadsheetCellProcessor(getKernel().getAlgebraProcessor(),
						errorHandler);
	}

	@Test
	public void testTextInput() {
		processor.process("(1, 1)", "A1");
		assertTrue(lookup("A1").isGeoText());
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testTextInputWithQuotes() {
		processor.process("\"1+2\"", "A1");
		assertThat(lookup("A1"), hasValue("1+2"));
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testPointInput() {
		processor.process("=(1, 1)", "A1");
		assertTrue(lookup("A1").isGeoPoint());
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testComputation() {
		processor.process("=1 + 2", "A1");
		GeoElement a1 = lookup("A1");
		assertTrue(a1.isGeoNumeric()
				&& DoubleUtil.isEqual(((GeoNumeric) a1).getDouble(), 3.0));
//		assertIsAuxiliary();
		assertIsEuclidianInvisible();

	}

	@Test
	public void testSerializeText() {
		processor.process("(1, 1)", "A1");
		assertSerializedAs("(1, 1)");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testSerializePoint() {
		processor.process("=(1,1)", "A1");
		assertSerializedAs("=(1, 1)");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testSerializeComputation() {
		processor.process("=1+ 2", "A1");
		assertSerializedAs("=1 + 2");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	private void assertSerializedAs(String value) {
		assertEquals("The values do not match!", value,
				serializer.getStringForEditor(lookup("A1")));
	}

	private void assertIsAuxiliary() {
		assertTrue("The created element is not auxiliary!", lookup("A1").isAuxiliaryObject());
	}

	private void assertIsEuclidianInvisible() {
		assertFalse("The created element is visible within the EV!",
				lookup("A1").isEuclidianVisible());
	}
}
