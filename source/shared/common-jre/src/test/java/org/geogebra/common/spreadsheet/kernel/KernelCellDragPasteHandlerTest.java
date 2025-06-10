package org.geogebra.common.spreadsheet.kernel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.cas.MockedCasGiac;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.core.CellDragPasteHandler;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class KernelCellDragPasteHandlerTest extends BaseUnitTest {

	private CellDragPasteHandler cellDragPasteHandler;
	private KernelTabularDataAdapter tabularData;

	@Override
	public void setup() {
		super.setup();
		tabularData = new KernelTabularDataAdapter(getApp());
		cellDragPasteHandler = new KernelCellDragPasteHandler(tabularData, getKernel());
	}

	@Test
	public void testPasteSingleCell1() {
		tabularData.setContent(0, 0, add("=12"));
		setRangeToCopy(0, 0, 0, 0);
		pasteToDestination(0, 1);
		assertCellContentIsEqual(0, 0, 0, 1);
	}

	@Test
	public void testPasteSingleCell2() {
		tabularData.setContent(1, 1, add("1 + 3"));
		setRangeToCopy(1, 1, 1, 1);
		pasteToDestination(2, 2);
		assertCellContentIsEqual(1, 1, 2, 1);
	}

	@Test
	public void testPasteSingleCell3() {
		tabularData.setContent(1, 1, add("123"));
		tabularData.setContent(1, 2, add("456"));
		setRangeToCopy(1, 1, 1, 1);
		pasteToDestination(1, 2);
		assertCellContentIsEqual(1, 1, 1, 2);
	}

	@Test
	public void testPasteSingleCell4() {
		tabularData.setContent(0, 0, add("\"=12\""));
		tabularData.setContent(0, 1, add("\"=A1\""));
		setRangeToCopy(0, 0, 1, 1);
		pasteToDestination(1, 1);
		assertCellContentIsEqual(0, 1, 1, 1);
	}

	@Test
	public void testPasteMultipleCells1() {
		tabularData.setContent(1, 1, add("123"));
		tabularData.setContent(1, 2, add("456"));
		setRangeToCopy(1, 1, 1, 2);
		pasteToDestination(2, 2);
		assertCellContentIsEqual(1, 1, 2, 1);
		assertCellContentIsEqual(1, 2, 2, 2);
	}

	@Test
	public void testPasteMultipleCells2() {
		tabularData.setContent(3, 3, add("\"Sample Text\""));
		tabularData.setContent(4, 3, add("1 / 2"));
		setRangeToCopy(3, 4, 3, 3);
		pasteToDestination(6, 4);
		assertCellContentIsEqual(3, 3, 5, 3);
		assertCellContentIsEqual(4, 3, 6, 3);
	}

	@Test
	public void testPasteMultiplceCells3() {
		tabularData.setContent(0, 0, add("7 - 3"));
		setRangeToCopy(0, 0, 0, 0);
		pasteToDestination(2, 0);
		assertCellContentIsEqual(0, 0, 1, 0);
		assertCellContentIsEqual(0, 0, 2, 0);
	}

	@Test
	public void testPasteColumn() {
		tabularData.setContent(2, 2, add("3 * 4"));
		tabularData.setContent(4, 2, add("123"));
		setRangeToCopy(-1, -1, 2, 2);
		pasteToDestination(10, 3);
		assertCellContentIsEqual(2, 2, 2, 3);
		assertCellContentIsEqual(4, 2, 4, 3);
	}

	@Test
	public void testPasteMultipleColumns() {
		tabularData.setContent(1, 2, add("12"));
		tabularData.setContent(2, 3, add("14 + 2"));
		setRangeToCopy(-1, -1, 2, 3);
		pasteToDestination(10, 5);
		assertCellContentIsEqual(1, 2, 1, 4);
		assertCellContentIsEqual(2, 3, 2, 5);
	}

	@Test
	public void testPasteRow() {
		tabularData.setContent(1, 2, add("\"Test\""));
		tabularData.setContent(1, 3, add("pi"));
		setRangeToCopy(1, 1, -1, -1);
		pasteToDestination(0, 25);
		assertCellContentIsEqual(1, 2, 0, 2);
		assertCellContentIsEqual(1, 3, 0, 3);
	}

	@Test
	public void testPasteMultipleRows() {
		tabularData.setContent(2, 3, add("13"));
		tabularData.setContent(3, 3, add("1 + 2 + 3"));
		setRangeToCopy(2, 3, -1, -1);
		pasteToDestination(0, 3);
		assertCellContentIsEqual(2, 3, 0, 3);
		assertCellContentIsEqual(3, 3, 1, 3);
	}

	@Test
	public void testInvalidDestination1() {
		setRangeToCopy(1, 1, 1, 1);
		cellDragPasteHandler.setDestinationForPaste(1, 1);
		assertNull(cellDragPasteHandler.getDragPasteDestinationRange());
	}

	@Test
	public void testInvalidDestination2() {
		setRangeToCopy(-1, -1, 2, 2);
		cellDragPasteHandler.setDestinationForPaste(15, 2);
		assertNull(cellDragPasteHandler.getDragPasteDestinationRange());
	}

	@Test
	public void testInvalidDestination3() {
		setRangeToCopy(2, 4, -1, -1);
		cellDragPasteHandler.setDestinationForPaste(3, 0);
		assertNull(cellDragPasteHandler.getDragPasteDestinationRange());
	}

	@Test
	public void testLinearPattern1() {
		tabularData.setContent(1, 1, add("=12"));
		tabularData.setContent(2, 1, add("=15"));
		setRangeToCopy(1, 2, 1, 1);
		pasteToDestination(6, 1);
		assertCellContentEquals("18", 3, 1);
		assertCellContentEquals("21", 4, 1);
		assertCellContentEquals("24", 5, 1);
		assertCellContentEquals("27", 6, 1);
	}

	@Test
	public void testLinearPattern2() {
		tabularData.setContent(2, 5, add("=4"));
		tabularData.setContent(2, 4, add("=1"));
		setRangeToCopy(2, 2, 4, 5);
		pasteToDestination(2, 2);
		assertCellContentEquals("-2", 2, 3);
		assertCellContentEquals("-5", 2, 2);
	}

	@Test
	public void testLinearPattern3() {
		tabularData.setContent(4, 1, add("=1"));
		tabularData.setContent(5, 1, add("=7"));
		tabularData.setContent(4, 2, add("=3"));
		tabularData.setContent(5, 2, add("=4"));
		setRangeToCopy(4, 5, 1, 2);
		pasteToDestination(2, 2);
		assertCellContentEquals("-5", 3, 1);
		assertCellContentEquals("-11", 2, 1);
		assertCellContentEquals("2", 3, 2);
		assertCellContentEquals("1", 2, 2);
	}

	@Test
	public void testLinearPatternCAS() {
		getApp().setCasConfig();
		getApp().getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		MockedCasGiac mockedCasGiac = new MockedCasGiac();
		mockedCasGiac.applyTo(getApp());
		mockedCasGiac.memorize("Evaluate(1)", "1");
		mockedCasGiac.memorize("Round(1, 13)", "1.0");
		mockedCasGiac.memorize("Evaluate(2)", "2");
		mockedCasGiac.memorize("Round(2, 13)", "2.0");
		mockedCasGiac.memorize("Evaluate(1 + 2)", "3");
		mockedCasGiac.memorize("Round(3, 13)", "3.0");
		add("A1=1");
		add("B1=2");
		add("A2=2");
		add("B2=2");
		add("C1=A1+B1");
		setRangeToCopy(0, 0, 2, 2);
		pasteToDestination(1, 2);
		assertThat(lookup("C1"), hasValue("3"));
	}

	@Test
	@Issue("APPS-5987")
	public void testDragPasteShouldResultInNonEmptySpreadsheetCells1() {
		DefaultSpreadsheetCellProcessor processor
				= new DefaultSpreadsheetCellProcessor(getAlgebraProcessor());
		getKernel().attach(tabularData);
		processor.process("=3", 0, 0);
		processor.process("=A2", 0, 1);

		setRangeToCopy(0, 0, 1, 1);
		pasteToDestination(1, 1);
		assertTrue(lookup("A2").isEmptySpreadsheetCell());
		assertTrue(lookup("A3").isEmptySpreadsheetCell());

		setRangeToCopy(0, 0, 0, 0);
		pasteToDestination(1, 0);
		assertFalse(lookup("A2").isEmptySpreadsheetCell());
		assertTrue(lookup("A3").isEmptySpreadsheetCell());
	}

	@Test
	@Issue("APPS-5987")
	public void testDragPasteShouldResultInNonEmptySpreadsheetCells2() {
		getApp().setCasConfig();
		getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		DefaultSpreadsheetCellProcessor processor
				= new DefaultSpreadsheetCellProcessor(getAlgebraProcessor());
		getKernel().attach(tabularData);

		processor.process("=3", 0, 0);
		processor.process("=A2", 0, 1);
		assertCellContentEquals("0", 1, 0);

		setRangeToCopy(0, 0, 1, 1);
		pasteToDestination(1, 1);
		setRangeToCopy(0, 0, 0, 0);
		pasteToDestination(1, 0);

		assertCellContentEquals("0", 2, 0);
		assertCellContentEquals("3", 1, 0);
	}

	private void setRangeToCopy(int fromRow, int toRow, int fromColumn, int toColumn) {
		cellDragPasteHandler.setRangeToCopy(
				TabularRange.range(fromRow, toRow, fromColumn, toColumn));
	}

	private void pasteToDestination(int destinationRow, int destinationColumn) {
		cellDragPasteHandler.setDestinationForPaste(destinationRow, destinationColumn);
		cellDragPasteHandler.pasteToDestination();
	}

	private void assertCellContentIsEqual(int originRow, int originColumn,
		int destinationRow, int destinationColumn) {
		assertEquals(String.format("The content of cell (%d, %d) should be equal to the content "
								+ "of cell (%d, %d)!", originRow, originColumn, destinationRow,
						destinationColumn), getValueStringForCell(originRow, originColumn),
				getValueStringForCell(destinationRow, destinationColumn));
	}

	private void assertCellContentEquals(String expected, int row, int column) {
		assertEquals(String.format("The content of cell (%d, %d) is expected to be %s!",
						row, column, expected), expected, getValueStringForCell(row, column));
	}

	private String getValueStringForCell(int row, int column) {
		return lookup(GeoElementSpreadsheet.getSpreadsheetCellName(column, row))
				.toValueString(StringTemplate.defaultTemplate);
	}
}
