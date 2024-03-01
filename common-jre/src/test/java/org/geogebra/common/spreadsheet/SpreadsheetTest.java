package org.geogebra.common.spreadsheet;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.event.KeyEvent;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.spreadsheet.core.CellRenderableFactory;
import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetController;
import org.geogebra.common.spreadsheet.core.TableLayout;
import org.geogebra.common.spreadsheet.core.ViewportAdjustmentHandler;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;

public class SpreadsheetTest extends BaseUnitTest {

	private final int colHeader = TableLayout.DEFAUL_CELL_HEIGHT;
	private final int rowHeader = TableLayout.DEFAULT_ROW_HEADER_WIDTH;
	private Spreadsheet spreadsheet;
	private TestTabularData tabularData;
	private Rectangle viewport;

	@Before
	public void setupSpreadsheet() {
		tabularData = new TestTabularData();
		spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory());
		spreadsheet.setHeightForRows(20, 0, 5);
		spreadsheet.setWidthForColumns(40, 0, 5);
		viewport = new Rectangle(0, 100, 0, 120);
		spreadsheet.setViewport(viewport);
		spreadsheet.setViewportAdjustmentHandler(getMockForScrollable());
	}

	@Test
	public void testTextDataRendering() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		tabularData.setContent(0, 0, "foo");
		tabularData.setContent(0, 1, "bar");
		spreadsheet.draw(graphics);
		assertThat(graphics.toString(), equalTo("col0,col1,1,foo,bar,2,3,4,5"));
	}

	@Test
	public void testSingleColumnResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.setViewport(new Rectangle(0, 120, 0, 100));
		spreadsheet.draw(graphics);
		// initially we have 2 columns
		assertThat(graphics.toString(), startsWith("col0,col1,1"));
		spreadsheet.handlePointerDown(rowHeader + 40, 5, Modifiers.NONE);
		spreadsheet.handlePointerMove(rowHeader + 10, 5, Modifiers.NONE);
		spreadsheet.handlePointerUp(rowHeader + 10, 5, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 3
		assertThat(graphics.toString(), startsWith("col0,col1,col2,1"));
	}

	@Test
	public void testMultiColumnResize() {
		spreadsheet.setViewport(new Rectangle(0, 140, 0, 100));
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 3 columns
		assertThat(graphics.toString(), startsWith("col0,col1,col2,1"));
		spreadsheet.selectColumn(1, false, false);
		spreadsheet.selectColumn(2, true, false);
		spreadsheet.selectColumn(3, true, false);
		spreadsheet.selectColumn(4, true, false);
		spreadsheet.handlePointerDown(rowHeader + 80, 5, Modifiers.NONE);
		spreadsheet.handlePointerMove(rowHeader + 50, 5, Modifiers.NONE);
		spreadsheet.handlePointerUp(rowHeader + 50, 5, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 6
		assertThat(graphics.toString(), startsWith("col0,col1,col2,col3,col4,col5,1"));
	}

	@Test
	public void testSingleRowResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		spreadsheet.handlePointerDown(15, colHeader + 20, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, colHeader + 40, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, colHeader + 40, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 4
		assertThat(graphics.toString(), endsWith(",4"));
	}

	@Test
	public void testNoHeaderRowResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		assertThat(spreadsheet.getCursor(15, 23), equalTo(MouseCursor.DEFAULT));
		spreadsheet.handlePointerDown(15, 23, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, 23, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, 40, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// resizing header row should not work
		assertThat(graphics.toString(), endsWith(",5"));
	}

	@Test
	public void testMultiRowResize() {
		spreadsheet.setHeightForRows(20, 0, 5);
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		spreadsheet.selectRow(1, false, false);
		spreadsheet.selectRow(2, true, false);
		spreadsheet.selectRow(3, true, false);
		spreadsheet.selectRow(4, true, false);
		spreadsheet.handlePointerDown(15, colHeader + 20, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, colHeader + 45, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, colHeader + 45, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 3
		assertThat(graphics.toString(), endsWith(",3"));
	}

	@Test
	public void testViewportIsAdjustedRightwardsWithArrowKey() {
		spreadsheet.selectCell(1, 1, false, false);
		fakeRightArrowPress(viewport);
		assertNotEquals(0, viewport.getMinX(), 0);
	}

	@Test
	public void testViewportIsAdjustedRightwardsWithMouseClick() {
		spreadsheet.handlePointerDown(rowHeader + 90, colHeader + 10, Modifiers.NONE);
		assertNotEquals(0, viewport.getMinX(), 0);
	}

	@Test
	public void testViewportIsNotAdjustedRightwardsWithArrowKey() {
		spreadsheet.setViewport(new Rectangle(0, 500, 0, 500));
		spreadsheet.selectCell(2, 0, false, false);
		fakeRightArrowPress(viewport);
		assertEquals(0, viewport.getMinX(), 0);
	}

	@Test
	public void testViewportIsNotAdjustedRightwardsWithMouseClick() {
		spreadsheet.setViewport(new Rectangle(0, 140, 0, 100));
		spreadsheet.handlePointerDown(rowHeader + 60, colHeader + 5, Modifiers.NONE);
		assertEquals(0, viewport.getMinX(), 0);
	}

	@Test
	public void testViewportShouldNotBeAdjustedWhenMovingLeftAtLeftmostPositionWithArrowKey() {
		spreadsheet.selectCell(0, 0, false, false);
		fakeLeftArrowPress(viewport);
		assertEquals(0, viewport.getMinX(), 0);
	}

	@Test
	public void testViewportIsNotAdjustedHorizontallyWithArrowKey() {
		spreadsheet.setViewport(new Rectangle(0, 300, 0, 300));
		spreadsheet.selectCell(2, 0, false, false);
		fakeRightArrowPress(viewport);
		assertEquals(0, viewport.getMinX(), 0);
		fakeLeftArrowPress(viewport);
		assertEquals(0, viewport.getMinX(), 0);
	}

	@Test
	public void testViewportIsAdjustedDownwardsWithArrowKey() {
		spreadsheet.setViewport(new Rectangle(0, 300, 0, 100));
		spreadsheet.selectCell(1, 1, false, false);
		fakeDownArrowPress(viewport);
		assertNotEquals(0, viewport.getMinY(), 0);
	}

	@Test
	public void testViewportIsAdjustedDownwardsWithMouseClick() {
		spreadsheet.handlePointerDown(rowHeader + 10, colHeader + 80, Modifiers.NONE);
		assertNotEquals(0, viewport.getMinY(), 0);
	}

	@Test
	public void testViewportIsNotAdjustedDownwardsWithArrowKey() {
		spreadsheet.selectCell(0, 0, false, false);
		fakeDownArrowPress(viewport);
		assertEquals(0, viewport.getMinY(), 0);
	}

	@Test
	public void testViewportIsNotAdjustedDownwardsWithMouseClick() {
		spreadsheet.handlePointerDown(rowHeader + 10, colHeader + 30, Modifiers.NONE);
		assertEquals(0, viewport.getMinY(), 0);
	}

	@Test
	public void testViewportShouldNotBeAdjustedWhenMovingUpAtTopmostPositionWithArrowKey() {
		spreadsheet.selectCell(0, 2, false, false);
		fakeUpArrowPress(viewport);
		assertEquals(0, viewport.getMinY(), 0);
	}

	@Test
	public void testViewportIsNotAdjustedUpwardsWithArrowKey() {
		spreadsheet.selectCell(2, 1, false, false);
		fakeDownArrowPress(viewport);
		double verticalScrollPosition = viewport.getMinY();
		fakeUpArrowPress(viewport);
		assertEquals(verticalScrollPosition, viewport.getMinY(), 0);
	}

	private static class TestCellRenderableFactory implements CellRenderableFactory {
		@Override
		public SelfRenderable getRenderable(Object data, SpreadsheetStyle style,
				int row, int column) {
			return data == null ? null : new SelfRenderable(new StringRenderer(),
					GFont.PLAIN, CellFormat.ALIGN_LEFT, data);
		}
	}

	private void fakeLeftArrowPress(Rectangle viewport) {
		KeyEvent e = fakeKeyEvent(37);
		spreadsheet.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE);
	}

	private void fakeUpArrowPress(Rectangle viewport) {
		KeyEvent e = fakeKeyEvent(38);
		spreadsheet.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE);
	}

	private void fakeRightArrowPress(Rectangle viewport) {
		KeyEvent e = fakeKeyEvent(39);
		spreadsheet.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE);
	}

	private void fakeDownArrowPress(Rectangle viewport) {
		KeyEvent e = fakeKeyEvent(40);
		spreadsheet.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE);
	}

	private KeyEvent fakeKeyEvent(int keyCode) {
		KeyEvent event = mock(KeyEvent.class);
		when(event.getKeyCode()).thenReturn(keyCode);
		when(event.getKeyChar()).thenReturn(' ');
		return event;
	}

	private ViewportAdjustmentHandler getMockForScrollable() {
		ViewportAdjustmentHandler viewportAdjustmentHandler = mock(ViewportAdjustmentHandler.class);
		doAnswer(invocation -> {
			int position = invocation.getArgument(0);
			viewport = viewport.translatedBy(0, position);
			return null;
		}).when(viewportAdjustmentHandler).setVerticalScrollPosition(anyInt());
		doAnswer(invocation -> {
			int position = invocation.getArgument(0);
			viewport = viewport.translatedBy(position, 0);
			return null;
		}).when(viewportAdjustmentHandler).setHorizontalScrollPosition(anyInt());
		when(viewportAdjustmentHandler.getScrollBarWidth()).thenReturn(5);
		return viewportAdjustmentHandler;
	}
}