package org.geogebra.web.full.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.CellFormatInterface;
import org.geogebra.common.gui.view.spreadsheet.CellRange;
import org.geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.MyTableInterface;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetController;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetModeProcessor;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.util.Resizer;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.AbstractNativeScrollbar;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CSSProperties;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLElement;

/**
 * HTML5 implementation of spreadsheet table
 */
@SuppressWarnings("javadoc")
public class MyTableW implements /* FocusListener, */MyTable {

	private int tableMode = MyTable.TABLE_MODE_STANDARD;
	/** dot for rectangle selection */
	public static final int DOT_SIZE = 6;

	/** Selection rectangle color */
	public static final GColor SELECTED_RECTANGLE_COLOR = GColor.BLUE;

	// final static double dash1[] = { 2.0 };
	// final static GBasicStrokeW dashed = new GBasicStrokeW(3.0,
	// DefaultBasicStroke.CAP_BUTT, DefaultBasicStroke.JOIN_MITER, 10.0,
	// dash1);

	protected Kernel kernel;
	/** application */
	private AppW app;
	private MyCellEditorW editor;

	/** copy/paste utility */
	private final CopyPasteCut copyPasteCut;

	// protected SpreadsheetColumnControllerW.ColumnHeaderRenderer
	// columnHeaderRenderer;
	// protected SpreadsheetRowHeaderW.RowHeaderRenderer rowHeaderRenderer;
	/** view */
	protected SpreadsheetViewW view;
	/** table model */
	protected SpreadsheetTableModel tableModel;
	/** cell processor */
	private CellRangeProcessor crProcessor;
	// private MyTableColumnModelListener columnModelListener;
	/** Cell renderer */
	MyCellRendererW defaultTableCellRenderer;

	/**
	 * All currently selected cell ranges are held in this list. Cell ranges are
	 * added when selecting with ctrl-down. The first element is the most
	 * recently selected cell range.
	 */
	private final ArrayList<CellRange> selectedCellRanges;

	// These keep track of internal selection using actual ranges and do not
	// use -1 flags for row and column.
	// Note: selectedCellRanges.get(0) gives the same selection but uses -1
	// flags
	// the following are in Grid coordinates (TableModel coordinates+1)
	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;
	protected int minSelectionColumn = -1;
	protected int maxSelectionColumn = -1;

	// for emulating the JTable's changeSelection method, in TableModel
	// coordinates
	protected int anchorSelectionRow = -1;
	protected int anchorSelectionColumn = -1;
	protected int leadSelectionRow = -1;
	protected int leadSelectionColumn = -1;

	// Used for rendering headers with ctrl-select
	protected HashSet<Integer> selectedColumnSet = new HashSet<>();
	protected HashSet<Integer> selectedRowSet = new HashSet<>();

	private int selectionType;

	private GColor selectionRectangleColor = SELECTED_RECTANGLE_COLOR;

	// Dragging vars
	protected int draggingToRow = -1;
	protected int draggingToColumn = -1;
	protected boolean isDragging = false;

	protected boolean showRowHeader = true;
	protected boolean showColumnHeader = true;

	protected boolean editing = false;

	boolean repaintAll = false; // sometimes only the repainting of
								// borders/background is needed

	// Cells to be resized on next repaint are put in these HashSets.
	// A cell is added to a set when editing is done. The cells are removed
	// after a repaint in MyTable.
	private final HashSet<GPoint> cellResizeHeightSet;
	private final HashSet<GPoint> cellResizeWidthSet;

	private final ArrayList<GPoint> adjustedRowHeights = new ArrayList<>();
	private boolean doRecordRowHeights = true;

	private int preferredColumnWidth = SpreadsheetSettings.TABLE_CELL_WIDTH;

	// there should be place left for the textfield
	protected int minimumRowHeight = SpreadsheetSettings.TABLE_CELL_HEIGHT + 4;

	private HashMap<GPoint, GeoElement> oneClickEditMap = new HashMap<>();
	private boolean allowEditing = false;

	private SpreadsheetModeProcessor spredsheetModeProcessor;

	protected Grid ssGrid;

	protected TableScroller scroller;

	private FlowPanel tableWrapper;
	protected SpreadsheetRowHeaderW rowHeader;
	protected SpreadsheetColumnHeaderW columnHeader;

	private FlowPanel rowHeaderContainer;

	// special panels for editing and selection
	private SimplePanel selectionFrame;
	private SimplePanel dragFrame;
	private SimplePanel blueDot;
	private SimplePanel editorPanel;

	private AbsolutePanel gridPanel;

	private Grid upperLeftCorner;

	private FlowPanel headerRow;

	private FlowPanel ssGridContainer;

	private FlowPanel columnHeaderContainer;

	private FlowPanel cornerContainerUpperLeft;

	private FlowPanel cornerContainerLowerLeft;

	private FlowPanel cornerContainerUpperRight;

	protected Grid dummyTable;

	private boolean autoScrolls = true;

	private boolean isSelectAll = false;

	private SpreadsheetController controller;

	/*******************************************************************
	 * Construct table
	 */
	public MyTableW(SpreadsheetViewW view, SpreadsheetTableModel tableModel) {

		app = view.getApplication();
		kernel = app.getKernel();
		this.tableModel = tableModel;
		this.view = view;

		createFloatingElements();
		createGUI();

		cellResizeHeightSet = new HashSet<>();
		cellResizeWidthSet = new HashSet<>();

		for (int i = 0; i < getColumnCount(); ++i) {
			// TODO//getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			Resizer.setPixelWidth(ssGrid.getColumnFormatter().getElement(i),
					preferredColumnWidth);
		}

		// add cell renderer & editors
		defaultTableCellRenderer = new MyCellRendererW(app, view, this,
		        (CellFormat) getCellFormatHandler());

		// this needs defaultTableCellRenderer now
		((SpreadsheetTableModelSimple) tableModel).attachMyTable(this);

		// :NEXT:Grid.setCellFormatter
		editor = new MyCellEditorW(kernel, editorPanel,
				getEditorController());
		Dom.addEventListener(tableWrapper.getElement(), "focusout", evt -> onFocusOut());
		// setDefaultEditor(Object.class, editor);

		// initialize selection fields
		selectedCellRanges = new ArrayList<>();
		selectedCellRanges.add(new CellRange(app));

		selectionType = MyTableInterface.CELL_SELECT;

		// add table model listener
		((SpreadsheetTableModelSimple) tableModel)
		        .setChangeListener(new MyTableModelListener());

		copyPasteCut = new CopyPasteCutW(app);

		ssGrid.setCellPadding(0);
		ssGrid.setCellSpacing(0);
		ssGrid.getElement().classList.add("geogebraweb-table-spreadsheet");

		registerListeners();
		repaintAll();
	}

	private void onFocusOut() {
		editor.stopCellEditingAndProcess();
		finishEditing(true);
	}

	@Override
	public ArrayList<CellRange> getSelectedCellRanges() {
		return selectedCellRanges;
	}

	/**
	 * @return grid
	 */
	public Grid getGrid() {
		return ssGrid;
	}

	/**
	 * @return wrapping widget
	 */
	public Widget getContainer() {
		return tableWrapper;
	}

	/**
	 * @return editor
	 */
	public MyCellEditorW getEditor() {
		return editor;
	}

	/**
	 * @return whether editor is active
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 * @return Collection of cells that contain geos that can be edited with one
	 *         click, e.g. booleans, buttons, lists
	 */
	public HashMap<GPoint, GeoElement> getOneClickEditMap() {
		return oneClickEditMap;
	}

	/**
	 * @param oneClickEditMap
	 *            fast editable geos, see {@link #getOneClickEditMap()}
	 */
	public void setOneClickEditMap(HashMap<GPoint, GeoElement> oneClickEditMap) {
		this.oneClickEditMap = oneClickEditMap;
	}

	private void registerListeners() {
		SpreadsheetMouseListenerW ml = new SpreadsheetMouseListenerW(app, this);
		gridPanel.addDomHandler(ml, MouseDownEvent.getType());
		gridPanel.addDomHandler(ml, MouseUpEvent.getType());
		gridPanel.addDomHandler(ml, MouseMoveEvent.getType());
		gridPanel.addDomHandler(ml, DoubleClickEvent.getType());
		gridPanel.addBitlessDomHandler(ml, TouchStartEvent.getType());
		gridPanel.addBitlessDomHandler(ml, TouchMoveEvent.getType());
		gridPanel.addBitlessDomHandler(ml, TouchEndEvent.getType());
		gridPanel.addDomHandler(ml, MouseOutEvent.getType());

		upperLeftCorner.addBitlessDomHandler(event -> selectAll(), ClickEvent.getType());

		ClickStartHandler.init(gridPanel, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				CancelEventTimer.keyboardSetVisible();
			}
		});

	}

	/**
	 * End table constructor
	 ******************************************************************/

	private void createFloatingElements() {

		editorPanel = new SimplePanel();
		editorPanel.addStyleName("editorPanel");
		editorPanel.getElement().style.zIndex = CSSProperties.ZIndexUnionType.of(6);
		editorPanel.setVisible(false);

		selectionFrame = new SimplePanel();
		selectionFrame.addStyleName("geogebraweb-selectionframe-spreadsheet");
		selectionFrame.setVisible(false);

		dragFrame = new SimplePanel();
		dragFrame.getElement().style.zIndex = CSSProperties.ZIndexUnionType.of(5);
		dragFrame.getElement().style
		        .borderStyle = "solid";
		dragFrame.getElement().style.borderWidth = CSSProperties.BorderWidthUnionType.of("2px");
		dragFrame.getElement().style
		        .borderColor = GColor.GRAY.toString();
		dragFrame.setVisible(false);

		blueDot = new SimplePanel();
		blueDot.setVisible(false);
		blueDot.setPixelSize(MyTableW.DOT_SIZE, MyTableW.DOT_SIZE);
		blueDot.setStyleName("spreadsheetDot cursor_default");

		dummyTable = new Grid(1, 1);
		dummyTable.getElement().style
		        .visibility = "hidden";
		dummyTable.setText(0, 0, "x");
		dummyTable.getElement().style.position = "absolute";
		dummyTable.getElement().style.top = "0";
		dummyTable.getElement().style.left = "0";
		dummyTable.getElement().classList.add("geogebraweb-table-spreadsheet");
	}

	private void createGUI() {
		// ------ upper left corner
		upperLeftCorner = new Grid(1, 1);
		upperLeftCorner.getElement().classList.add(
		        "geogebraweb-table-spreadsheet");
		upperLeftCorner.getCellFormatter().getElement(0, 0)
		        .classList.add("SVheader");
		upperLeftCorner.setText(0, 0, "9999");
		upperLeftCorner.setCellPadding(0);
		upperLeftCorner.setCellSpacing(0);
		CSSStyleDeclaration s = upperLeftCorner.getElement().style;
		
		upperLeftCorner.addStyleName("upperCorner");
		int leftCornerWidth = SpreadsheetViewW.ROW_HEADER_WIDTH;
		Resizer.setPixelWidth(upperLeftCorner.getElement(), leftCornerWidth);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());
		//s.setColor(BACKGROUND_COLOR_HEADER.toString());
		s.position = "absolute";
		s.top = "0";
		s.left = "0";

		// ----- upper right corner
		Grid upperRightCorner = new Grid(1, 1);
		upperRightCorner.setText(0, 0, "xxx");
		upperRightCorner.getElement().classList.add(
		        "geogebraweb-table-spreadsheet");
		upperRightCorner.addStyleName("upperCorner");
		upperRightCorner.getCellFormatter().getElement(0, 0)
		        .classList.add("SVheader");
		s = upperRightCorner.getElement().style;
		int rightCornerWidth = AbstractNativeScrollbar
				.getNativeScrollbarWidth();
		Resizer.setPixelWidth(upperRightCorner.getElement(), rightCornerWidth);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());
		//s.setColor(BACKGROUND_COLOR_HEADER.toString());
		s.position = "absolute";
		s.top = "0";
		s.right = "0";

		// ----- lower left corner
		Grid lowerLeftCorner = new Grid(1, 1);
		upperRightCorner.setText(0, 0, "9999");
		lowerLeftCorner.getElement().classList.add(
		        "geogebraweb-table-spreadsheet-lowerLeftCorner");
		s = lowerLeftCorner.getElement().style;
		Resizer.setPixelWidth(lowerLeftCorner.getElement(), leftCornerWidth - 1);
		int lowerLeftCornerHeight = AbstractNativeScrollbar
				.getNativeScrollbarHeight();
		Resizer.setPixelWidth(lowerLeftCorner.getElement(), lowerLeftCornerHeight - 2);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());

		s.position = "absolute";
		s.left = "0";
		s.bottom = "0";

		// ---- corner containers
		cornerContainerUpperLeft = new FlowPanel();
		cornerContainerUpperLeft.getElement().style
		        .display = "block";
		cornerContainerUpperLeft.add(upperLeftCorner);
		cornerContainerLowerLeft = new FlowPanel();
		cornerContainerLowerLeft.getElement().style
		        .display = "block";
		cornerContainerLowerLeft.add(lowerLeftCorner);
		cornerContainerUpperRight = new FlowPanel();
		cornerContainerUpperRight.getElement().style
		        .display = "block";
		cornerContainerUpperRight.add(upperRightCorner);

		// ---- column header
		columnHeader = new SpreadsheetColumnHeaderW(app, this);
		s = columnHeader.getContainer().getElement().style;
		s.position = "relative";

		columnHeaderContainer = new FlowPanel();
		s = columnHeaderContainer.getElement().style;
		s.display = "block";
		s.overflow = "hidden";
		s.marginLeft = CSSProperties.MarginLeftUnionType.of(leftCornerWidth + "px");
		s.marginRight = CSSProperties.MarginRightUnionType.of(rightCornerWidth + "px");
		columnHeaderContainer.add(columnHeader.getContainer());

		// ------ row header
		rowHeader = new SpreadsheetRowHeaderW(app, this);
		s = rowHeader.getContainer().getElement().style;
		s.position = "relative";

		rowHeaderContainer = new FlowPanel();
		s = rowHeaderContainer.getElement().style;
		s.display = "block";
		s.marginBottom = CSSProperties.MarginBottomUnionType.of(lowerLeftCornerHeight + "px");
		s.overflow = "hidden";
		s.position = "absolute";
		// s.top = "0";
		s.left = "0";

		rowHeaderContainer.add(rowHeader.getContainer());
		rowHeaderContainer.add(cornerContainerLowerLeft);

		// spreadsheet table
		ssGrid = new Grid(tableModel.getRowCount(), tableModel.getColumnCount());
		gridPanel = new AbsolutePanel();
		gridPanel.getElement().style.position = "absolute";
		gridPanel.add(ssGrid);
		gridPanel.add(selectionFrame);
		gridPanel.add(dragFrame);
		gridPanel.add(blueDot);
		gridPanel.add(editorPanel);
		gridPanel.add(dummyTable);

		scroller = new TableScroller(this, rowHeader, columnHeader);

		ssGridContainer = new FlowPanel();
		s = ssGridContainer.getElement().style;
		s.verticalAlign= "top";
		s.display = "inline-block";
		s.marginLeft= CSSProperties.MarginLeftUnionType.of(leftCornerWidth + "px");
		ssGridContainer.add(scroller);

		// create table header row
		headerRow = new FlowPanel();
		headerRow.getElement().style
		        .whiteSpace = "nowrap";
		headerRow.add(cornerContainerUpperLeft);
		headerRow.add(columnHeaderContainer);
		headerRow.add(cornerContainerUpperRight);

		// create table row
		FlowPanel tableRow = new FlowPanel();
		tableRow.getElement().style.whiteSpace = "nowrap";
		tableRow.getElement().style.verticalAlign = "top";
		tableRow.add(rowHeaderContainer);
		tableRow.add(ssGridContainer);

		// put rows together to complete the GUI
		tableWrapper = new FlowPanel();
		tableWrapper.add(headerRow);
		tableWrapper.add(tableRow);

	}

	/**
	 * @return wrapping panel
	 */
	public AbsolutePanel getGridPanel() {
		return gridPanel;
	}

	private void updateTableLayout() {

		int leftCornerWidth = SpreadsheetViewW.ROW_HEADER_WIDTH;

		if (showColumnHeader) {
			headerRow.getElement().style.display = "block";
		} else {
			headerRow.getElement().style.display = "none";
		}

		if (showRowHeader) {
			cornerContainerUpperLeft.getElement().style
			        .display = "block";
			cornerContainerLowerLeft.getElement().style
			        .display = "block";
			rowHeaderContainer.getElement().style
			        .display = "block";
			ssGridContainer.getElement().style
			        .marginLeft = CSSProperties.MarginLeftUnionType.of(leftCornerWidth + "px");
			columnHeaderContainer.getElement().style
			        .marginLeft = CSSProperties.MarginLeftUnionType.of(leftCornerWidth + "px");

		} else {
			cornerContainerUpperLeft.getElement().style
			        .display = "none";
			cornerContainerLowerLeft.getElement().style
			        .display = "none";
			rowHeaderContainer.getElement().style
			        .display = "none";
			ssGridContainer.getElement().style.marginLeft =
					CSSProperties.MarginLeftUnionType.of("0");
			columnHeaderContainer.getElement().style.marginLeft =
					CSSProperties.MarginLeftUnionType.of("0");
		}
	}

	/**
	 * Returns parent SpreadsheetView for this table
	 * 
	 * @return SpreadsheetView
	 */
	@Override
	public SpreadsheetViewInterface getView() {
		return view;
	}

	/**
	 * Simple getter method
	 * 
	 * @return App
	 */
	@Override
	public App getApplication() {
		return app;
	}

	/**
	 * Simple getter method
	 * 
	 * @return Kernel
	 */
	@Override
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * @return table model
	 */
	public SpreadsheetTableModel getModel() {
		return tableModel;
	}

	@Override
	public CopyPasteCut getCopyPasteCut() {
		return copyPasteCut;
	}

	/**
	 * Returns CellRangeProcessor for this table. If none exists, a new one is
	 * created.
	 */
	@Override
	public CellRangeProcessor getCellRangeProcessor() {
		if (crProcessor == null) {
			crProcessor = new CellRangeProcessor(this, app);
		}
		return crProcessor;
	}

	/**
	 * Returns CellFormat helper class for this table. If none exists, a new one
	 * is created.
	 */
	@Override
	public CellFormatInterface getCellFormatHandler() {
		return app.getSpreadsheetTableModel().getCellFormat(this);
	}

	/**
	 * Get element type of given cell
	 * 
	 * @param row
	 *            row
	 * @param column
	 *            column
	 * @return element type
	 */
	public GeoClass getCellEditorType(int row, int column) {
		GPoint p = new GPoint(column, row);
		if (view.allowSpecialEditor() && oneClickEditMap.containsKey(p)
				&& kernel.getAlgebraStyleSpreadsheet() == Kernel.ALGEBRA_STYLE_VALUE) {
			switch (oneClickEditMap.get(p).getGeoClassType()) {
			case BOOLEAN:
				return GeoClass.BOOLEAN;
			case BUTTON:
				return GeoClass.BUTTON;
			case LIST:
				return GeoClass.LIST;
			}
		}
		return GeoClass.DEFAULT;
	}

	public BaseCellEditor getCellEditor() {
		return editor;
	}

	public int getRowHeight(int row) {
		return ssGrid.getRowFormatter().getElement(row).offsetHeight;
	}

	/**
	 * @param column
	 *            column index
	 * @return width in pixels
	 */
	public int getColumnWidth(int column) {
		// columnFormatter returns 0 (in Chrome at least)
		// so cellFormatter used instead
		return ssGrid.getCellFormatter().getElement(0, column).offsetWidth;
	}

	/**
	 * @return Row where selection ended (start = anchor row)
	 */
	public int getLeadSelectionRow() {
		if (leadSelectionRow < 0) {
			return getSelectedRow();
		}
		return leadSelectionRow;
	}

	/**
	 * @return Column where selection ended (start = anchor row)
	 */
	public int getLeadSelectionColumn() {
		if (leadSelectionColumn < 0) {
			return getSelectedColumn();
		}
		return leadSelectionColumn;
	}

	/**
	 * sets requirement that commands entered into cells must start with "="
	 */
	public void setEqualsRequired(boolean isEqualsRequired) {
		editor.setEqualsRequired(isEqualsRequired);
	}

	/**
	 * gets flag for requirement that commands entered into cells must start
	 * with "="
	 * 
	 * @return whether = is required to interpret content as command
	 */
	public boolean isEqualsRequired() {
		return view.isEqualsRequired();
	}

	public void setLabels() {
		editor.setLabels();
	}

	public int preferredColumnWidth() {
		return preferredColumnWidth;
	}

	public void setPreferredColumnWidth(int preferredColumnWidth) {
		this.preferredColumnWidth = preferredColumnWidth;
	}

	public class MyTableModelListener implements
	        SpreadsheetTableModelSimple.ChangeListener {

		@Override
		public void dimensionChange() {
			// TODO: comment them out to imitate the Desktop behaviour
			// TODO//getView().updateRowHeader();

			updateColumnCount();
			updateRowCount();
			repaintAll();
		}

		@Override
		public void valueChange() {
			if (isEditing()) {
				updateCopiableSelection();
			}
		}
	}

	/**
	 * Update the content of the copiable textarea.
	 */
	public void updateCopiableSelection() {
		// TODO: can this be made more efficient?
		if (view != null && view.spreadsheetWrapper != null) {
			String cs = copyString();
			view.spreadsheetWrapper.setSelectedContent(cs);
			if (rowHeader != null
					&& selectionType == MyTableInterface.ROW_SELECT) {
				rowHeader.focusPanel.setSelectedContent(cs);
			}
		} else if (rowHeader != null
				&& selectionType == MyTableInterface.ROW_SELECT) {
			rowHeader.focusPanel.setSelectedContent(copyString());
		}
	}

	/**
	 * Update row heights and count in the header.
	 */
	void updateRowCount() {
		if (ssGrid.getRowCount() >= tableModel.getRowCount()) {
			return;
		}

		int oldRowCount = ssGrid.getRowCount();
		ssGrid.resizeRows(tableModel.getRowCount());

		for (int row = oldRowCount; row < tableModel.getRowCount(); ++row) {
			setRowHeight(row, app.getSettings().getSpreadsheet()
					.preferredRowHeight());
		}

		rowHeader.updateRowCount();
	}

	/**
	 * Appends columns to the table if table model column count is larger than
	 * current number of table columns.
	 */
	protected void updateColumnCount() {

		if (ssGrid.getColumnCount() >= tableModel.getColumnCount()) {
			return;
		}

		int oldColumnCount = ssGrid.getColumnCount();
		ssGrid.resizeColumns(tableModel.getColumnCount());

		for (int col = oldColumnCount; col < tableModel.getColumnCount(); ++col) {
			Resizer.setPixelWidth(ssGrid.getColumnFormatter().getElement(col),
			        preferredColumnWidth);
		}

		columnHeader.updateColumnCount();

		// addColumn destroys custom row heights, so we must reset them
		// resetRowHeights();

	}

	// ===============================================================
	// Selection
	// ===============================================================
	/**
	 * @param point
	 *            y coordinate is row, x coordinate is column
	 */
	public void changeSelection(GPoint point, boolean extend) {
		changeSelection(point.getY(), point.getX(), extend);
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean extend) {
		// force column selection
		if (view.isColumnSelect()) {
			setColumnSelectionInterval(columnIndex, columnIndex);
		}

		if (extend) {
			leadSelectionColumn = columnIndex;
			leadSelectionRow = rowIndex;
			if (anchorSelectionColumn == -1) {
				anchorSelectionColumn = leadSelectionColumn;
			}
			if (anchorSelectionRow == -1) {
				anchorSelectionRow = leadSelectionRow;
			}
		} else {
			anchorSelectionColumn = columnIndex;
			anchorSelectionRow = rowIndex;
			leadSelectionColumn = columnIndex;
			leadSelectionRow = rowIndex;
		}

		// let selectionChanged know about a change in single cell selection
		selectionChanged();

		if (autoScrolls) {
			GRectangle cellRect = getCellRect(rowIndex, columnIndex, true);
			if (cellRect != null) {
				scroller.scrollRectToVisible(columnIndex, rowIndex);
			}
		}
	}

	/**
	 * Select all cells
	 */
	public void selectAll() {

		setSelectionType(MyTableInterface.CELL_SELECT);
		setAutoscrolls(false);

		// select the upper left corner cell
		changeSelection(0, 0, false);

		// extend the selection to the current lower right corner cell
		changeSelection(getRowCount() - 1, getColumnCount() - 1, true);

		setSelectAll(true);
		setAutoscrolls(true);
		scrollRectToVisible(0, 0);

		// setRowSelectionInterval(0, getRowCount()-1);
		// getColumnModel().getSelectionModel().setSelectionInterval(0,
		// getColumnCount()-1);
		// selectionChanged();
		// this.getSelectAll();

	}

	private void setAutoscrolls(boolean autoScrolls) {
		this.autoScrolls = autoScrolls;
	}

	protected void scrollRectToVisible(int x, int y) {
		scroller.scrollRectToVisible(x, y);
	}

	/**
	 * This handles all selection changes for the table.
	 */
	@Override
	public void selectionChanged() {

		// create a cell range object to store
		// the current table selection

		CellRange newSelection = new CellRange(app);

		/*
		 * TODO if (view.isTraceDialogVisible()) {
		 * 
		 * newSelection = view.getTraceSelectionRange(getColumnModel()
		 * .getSelectionModel().getAnchorSelectionIndex(),
		 * getSelectionModel().getAnchorSelectionIndex());
		 * 
		 * scrollRectToVisible(getCellRect(newSelection.getMinRow(),
		 * newSelection.getMaxColumn(), true));
		 * 
		 * } else {
		 */

		switch (selectionType) {

		case MyTableInterface.CELL_SELECT:
			newSelection.setCellRange(anchorSelectionColumn,
			        anchorSelectionRow, leadSelectionColumn, leadSelectionRow);
			break;

		case MyTableInterface.ROW_SELECT:
			newSelection.setCellRange(-1, anchorSelectionRow, -1,
			        leadSelectionRow);
			break;

		case MyTableInterface.COLUMN_SELECT:
			newSelection.setCellRange(anchorSelectionColumn, -1,
			        leadSelectionColumn, -1);
			break;
		}
		/*
		 * }
		 */
		// newSelection.debug();
		/*
		 * // return if it is not really a new cell
		 * if(selectedCellRanges.size()>0 &&
		 * newSelection.equals(selectedCellRanges.get(0))) return;
		 */

		// update the selection list

		if (!GlobalKeyDispatcherW.getControlDown()) {
			selectedCellRanges.clear();
			selectedColumnSet.clear();
			selectedRowSet.clear();
			selectedCellRanges.add(0, newSelection);
		} else { // ctrl-select
			// handle dragging
			if (selectedCellRanges.get(0).hasSameAnchor(newSelection)) {
				selectedCellRanges.remove(0);
			}

			// add the selection to the list
			selectedCellRanges.add(0, newSelection);
		}

		// update sets of selected rows/columns (used for rendering in the
		// headers)
		if (selectionType == MyTableInterface.COLUMN_SELECT) {
			for (int i = newSelection.getMinColumn(); i <= newSelection
			        .getMaxColumn(); i++) {
				selectedColumnSet.add(i);
			}
		}

		if (selectionType == MyTableInterface.ROW_SELECT) {
			for (int i = newSelection.getMinRow(); i <= newSelection
			        .getMaxRow(); i++) {
				selectedRowSet.add(i);
			}
		}

		// update internal selection variables
		newSelection.setActualRange();
		minSelectionRow = newSelection.getMinRow();
		minSelectionColumn = newSelection.getMinColumn();
		maxSelectionColumn = newSelection.getMaxColumn();
		maxSelectionRow = newSelection.getMaxRow();

		// update the geo selection list
		ArrayList<GeoElement> list = new ArrayList<>();
		for (int i = 0; i < selectedCellRanges.size(); i++) {
			list.addAll(0, (selectedCellRanges.get(i)).toGeoList());
		}

		// if the geo selection has changed, update selected geos
		boolean changed = !list.equals(app.getSelectionManager()
		        .getSelectedGeos());
		if (changed) {

			if (getTableMode() == MyTable.TABLE_MODE_AUTOFUNCTION) {
				getSpreadsheetModeProcessor().updateAutoFunction();
			}

			app.getSelectionManager().setSelectedGeos(list, false);
			if (list.size() > 0) {
				app.updateSelection(true);
			} else {
				// don't update properties view for objects, but for spreadsheet
				app.updateSelection(false);
				app.setPropertiesViewPanel(OptionType.SPREADSHEET);
			}
		}
		
		if (view.isVisibleStyleBar()) {
			view.getSpreadsheetStyleBar().updateStyleBar();
		}

		// if the selection has changed or an empty cell has been clicked,
		// repaint
		if (changed || list.isEmpty()) {
			repaint();
			columnHeader.renderSelection();
			rowHeader.renderSelection();
			// ?//if (this.getTableHeader() != null)
			// ?// getTableHeader().repaint();
		}

		updateCopiableSelection();
	}

	/**
	 * Sets the initial selection parameters to a single cell. Does this without
	 * calling changeSelection, so it should only be used at startup.
	 */
	public void setInitialCellSelection(int row0, int column0) {

		setSelectionType(MyTableInterface.CELL_SELECT);
		int row = row0;
		int column = column0;
		if (column == -1) {
			column = 0;
		}
		if (row == -1) {
			row = 0;
		}
		minSelectionColumn = column;
		maxSelectionColumn = column;
		minSelectionRow = row;
		maxSelectionRow = row;

		renderSelectionDeferred();
		columnHeader.renderSelection();
		rowHeader.renderSelection();
	}

	@Override
	public boolean setSelection(int c, int r) {
		CellRange cr = new CellRange(app, c, r, c, r);
		return setSelection(cr);
	}

	/**
	 * 
	 * @param c1
	 *            min column
	 * @param r1
	 *            min row
	 * @param c2
	 *            max column
	 * @param r2
	 *            max row
	 */
	public void setSelection(int c1, int r1, int c2, int r2) {
		CellRange cr = new CellRange(app, c1, r1, c2, r2);
		if (cr.isValid()) {
			setSelection(cr);
		}
	}

	@Override
	public boolean setSelection(CellRange cr) {
		if (cr != null && !cr.isValid()) {
			return false;
		}

		try {
			if (cr == null || cr.isEmptyRange()) {
				minSelectionColumn = -1;
				minSelectionRow = -1;
				maxSelectionColumn = -1;
				maxSelectionRow = -1;
				anchorSelectionColumn = -1;
				anchorSelectionRow = -1;
				leadSelectionColumn = -1;
				leadSelectionRow = -1;

			} else {

				setAutoscrolls(false);

				// row selection
				if (cr.isRow()) {
					setRowSelectionInterval(cr.getMinRow(), cr.getMaxRow());

					// column selection
				} else if (cr.isColumn()) {
					setColumnSelectionInterval(cr.getMinColumn(),
					        cr.getMaxColumn());

					// cell block selection
				} else {
					setSelectionType(MyTableInterface.CELL_SELECT);
					changeSelection(cr.getMinRow(), cr.getMinColumn(), false);
					changeSelection(cr.getMaxRow(), cr.getMaxColumn(), true);
				}

				selectionChanged();

				// scroll to upper left corner of rectangle
				setAutoscrolls(true);
				scrollRectToVisible(cr.getMinColumn(), cr.getMinRow());
				repaint();
			}
		} catch (Exception e) {
			Log.debug(e);
			return false;
		}

		return true;
	}

	// TODO Handle selection for a list of cell ranges

	/*
	 * public void setSelection(ArrayList<CellRange> selection){
	 * 
	 * selectionRectangleColor = (color == null) ? SELECTED_RECTANGLE_COLOR :
	 * color;
	 * 
	 * // rectangle not drawn correctly without handle ... needs fix
	 * this.doShowDragHandle = true; // doShowDragHandle;
	 * 
	 * if (selection == null) {
	 * 
	 * setSelectionType(COLUMN_SELECT);
	 * 
	 * // clear the selection visuals and the deselect geos from here //TODO:
	 * this should be handled by the changeSelection() method
	 * selectedColumnSet.clear(); selectedRowSet.clear();
	 * this.minSelectionColumn = -1; this.minSelectionRow = -1;
	 * this.maxSelectionColumn = -1; this.maxSelectionRow = -1;
	 * app.setSelectedGeos(null); //setSelectionType(COLUMN_SELECT);
	 * view.repaint(); setSelectionType(CELL_SELECT);
	 * 
	 * } else {
	 * 
	 * for (CellRange cr : selection) {
	 * 
	 * this.setAutoscrolls(false);
	 * 
	 * if (cr.isRow()) { setRowSelectionInterval(cr.getMinRow(),
	 * cr.getMaxRow()); } else if (cr.isColumn()) {
	 * setColumnSelectionInterval(cr.getMinColumn(), cr .getMaxColumn()); } else
	 * { changeSelection(cr.getMinRow(), cr.getMinColumn(), false, false);
	 * changeSelection(cr.getMaxRow(), cr.getMaxColumn(), false, true); }
	 * 
	 * // scroll to upper left corner of rectangle
	 * 
	 * this.setAutoscrolls(true);
	 * scrollRectToVisible(getCellRect(cr.getMinRow(), cr.getMinColumn(),
	 * true)); }
	 * 
	 * 
	 * }
	 * 
	 * }
	 */

	/**
	 * Switch between column / row / range seletction.
	 * 
	 * @param selType
	 *            MyTableInterface.*_SELECT
	 */
	public void setSelectionType(int selType) {

		if (view.isColumnSelect()) {
			this.selectionType = MyTableInterface.COLUMN_SELECT;
		} else {

			// in web, selectionType should do what setSelectionMode do too
			this.selectionType = selType;
		}

	}

	@Override
	public int getSelectionType() {
		return selectionType;
	}

	/**
	 * @param row0
	 *            anchor row
	 * @param row1
	 *            lead row
	 */
	public void setRowSelectionInterval(int row0, int row1) {
		setSelectionType(MyTableInterface.ROW_SELECT);
		anchorSelectionRow = row0;
		leadSelectionRow = row1;
		selectionChanged();
	}

	/**
	 * @param col0
	 *            anchor column
	 * @param col1
	 *            lead column
	 */
	public void setColumnSelectionInterval(int col0, int col1) {
		setSelectionType(MyTableInterface.COLUMN_SELECT);
		anchorSelectionColumn = col0;
		leadSelectionColumn = col1;
		selectionChanged();
	}

	@Override
	public boolean isSelectAll() {
		return isSelectAll;
	}

	/**
	 * @param isSelectAll
	 *            whether all cells are selected
	 */
	public void setSelectAll(boolean isSelectAll) {
		this.isSelectAll = isSelectAll;
	}

	/**
	 * @return selected column indices
	 */
	public ArrayList<Integer> getSelectedColumnsList() {

		ArrayList<Integer> columns = new ArrayList<>();

		for (CellRange cr : this.selectedCellRanges) {
			for (int c = cr.getMinColumn(); c <= cr.getMaxColumn(); ++c) {
				if (!columns.contains(c)) {
					columns.add(c);
				}
			}
		}
		return columns;
	}

	// ===============================================================
	// Selection Utilities
	// ===============================================================

	public GColor getSelectionRectangleColor() {
		return selectionRectangleColor;
	}

	public void setSelectionRectangleColor(GColor color) {
		selectionRectangleColor = color;
	}

	protected GPoint getPixel(int column, int row, boolean min) {
		return getPixel(column, row, min, true);
	}

	protected GPoint getPixel(int column, int row, boolean min, boolean scaleOffset) {

		if (column < 0 || row < 0 || column >= this.getColumnCount()
				|| row >= this.getRowCount()) {
			return null;
		}

		HTMLElement wt = ssGrid.getCellFormatter().getElement(row, column);
		int offx = ssGrid.getAbsoluteLeft();
		int offy = ssGrid.getAbsoluteTop();
		int left, top;
		if (scaleOffset) {
			left = (int) ((DOM.getAbsoluteLeft(wt) - offx)
					/ getScale()) + offx;
			top = (int) ((DOM.getAbsoluteTop(wt) - offy)
					/ getScale()) + offy;
		} else {
			left = (int) (DOM.getAbsoluteLeft(wt)
					/ getScale());
			top = (int) (DOM.getAbsoluteTop(wt)
					/ getScale());
		}

		if (min) {
			return new GPoint(left, top);
		}
		return new GPoint(left + wt.offsetWidth, top
		        + wt.offsetHeight);
	}

	private double getScale() {
		// based on GPopupPanel.getScale. Assumes that x and y scale are the same
		return Browser.isSafariByVendor() ? 1 : app.getGeoGebraElement().getScaleX();
	}

	protected GPoint getPixelRelative(int column, int row) {
		HTMLElement wt = ssGrid.getCellFormatter().getElement(
				(int) MyMath.clamp(row, 0, getRowCount() - 1),
				(int) MyMath.clamp(column, 0, getColumnCount() - 1));
		int offx = column == getColumnCount() ? wt.offsetWidth : 0;
		int offy = row == getRowCount() ? wt.offsetHeight : 0;
		return new GPoint(wt.offsetLeft + offx, wt.offsetTop + offy);
	}

	protected GPoint getMinSelectionPixel() {
		return getPixel(minSelectionColumn, minSelectionRow, true);
	}

	protected GPoint getMaxSelectionPixel(boolean scaleOffset) {
		return getPixel(maxSelectionColumn, maxSelectionRow, false,
				scaleOffset);
	}

	/**
	 * Returns Point(columnIndex, rowIndex), cell indices for the given pixel
	 * location
	 *
	 * @return spreadsheet coordinates from pixel
	 */
	public GPoint getIndexFromPixel(int x, int y) {
		return getIndexFromPixel(x, y, 0);
	}

	/**
	 * Returns Point(columnIndex, rowIndex), cell indices for the given pixel
	 * location
	 * 
	 * @return spreadsheet coordinates from pixel
	 */
	public GPoint getIndexFromPixel(int x, int y, int diff) {
		if (x < 0 || y < 0) {
			return null;
		}

		int columnFrom = 0;
		int rowFrom = 0;

		int indexX = -1;
		int indexY = -1;
		for (int i = columnFrom; i < getColumnCount(); ++i) {
			GPoint point = getPixel(i, rowFrom, false, false);
			if (x + diff < point.getX()) {
				indexX = i;
				break;
			}
		}
		if (indexX == -1) {
			return null;
		}
		for (int i = rowFrom; i < getRowCount(); ++i) {
			GPoint point = getPixel(columnFrom, i, false, false);
			if (y + diff < point.getY()) {
				indexY = i;
				break;
			}
		}
		if (indexY == -1) {
			return null;
		}
		return new GPoint(indexX, indexY);
	}

	/**
	 * @param x x-offset with respect to the whole grid
	 * @return cell x-coordinate or -1 if not found
	 */
	public int getIndexFromPixelRelativeX(int x) {
		if (x < 0) {
			return -1;
		}

		int rowFrom = 0;

		int indexX = -1;
		for (int i = 0; i < getColumnCount(); ++i) {
			HTMLElement point = ssGrid.getCellFormatter().getElement(rowFrom, i);
			if (x <= point.offsetLeft) {
				indexX = i;
				break;
			}
		}
		return indexX;
	}

	/**
	 * @param y y-offset with respect to the whole grid
	 * @return cell y-coordinates or -1 if not found
	 */
	public int getIndexFromPixelRelativeY(int y) {
		if (y < 0) {
			return -1;
		}

		int columnFrom = 0;

		int indexY = -1;

		for (int i = 0; i < getRowCount(); ++i) {
			HTMLElement point = ssGrid.getCellFormatter().getElement(i, columnFrom);
			if (y <= point.offsetTop) {
				indexY = i;
				break;
			}
		}
		return indexY;
	}

	/**
	 * @return rectangle (with screen coordinates)
	 */
	public GRectangle getCellRect(int row, int column,
			boolean offset) {
		GPoint min = getPixel(column, row, true, offset);
		if (min == null) {
			return null;
		}
		GPoint max = getPixel(column, row, false, offset);
		if (max == null) {
			return null;
		}
		return new Rectangle(min.x, min.y, max.x - min.x, max.y - min.y);
	}

	/**
	 * @param point
	 *            x column, y row
	 * @return true on success
	 */
	public boolean editCellAt(GPoint point) {
		return editCellAt(point.getY(), point.getX());
	}

	/**
	 * Starts in-cell editing for cells with short editing strings. For strings
	 * longer than MAX_CELL_EDIT_STRING_LENGTH, the redefine dialog is shown.
	 * Also prevents fixed cells from being edited.
	 */
	@Override
	public boolean editCellAt(final int row, final int col) {

		if (row < 0 || col < 0) {
			return false;
		}

		Object ob = tableModel.getValueAt(row, col);

		// prepare editor to handle equals
		editor.setEqualsRequired(app.getSettings().getSpreadsheet()
		        .equalsRequired());
		if (ob instanceof GeoElement) {
			GeoElement geo = (GeoElement) ob;
			if (geo.isGeoButton() || geo.isGeoImage()) {
				ArrayList<GeoElement> sel = new ArrayList<>();
				sel.add(geo);
				app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS,
						sel);
				return true;
			}
			if (!view.getShowFormulaBar()) {
				if (getEditorController().redefineIfNeeded(geo)) {
					return true;
				}
			}
		}
		// STANDARD case: in cell editing
		if (isCellEditable(row, col) && !editing) {
			switch (getCellEditorType(row, col)) {
			case DEFAULT:
				editing = true;

				AutoCompleteTextFieldW textField = ((MyCellEditorW) getCellEditor())
						.getTableCellEditorWidget(this, ob, false,
				        row, col);
				// w.getElement().setAttribute("display", "none");

				if (view.isKeyboardEnabled()) {
					KeyboardManagerInterface keyboardManager = app.getKeyboardManager();
					if (keyboardManager != null) {
						app.showKeyboard(textField,
								!keyboardManager.isKeyboardClosedByUser());
					} else {
						app.showKeyboard(textField, true);
					}
					Scheduler.get().scheduleDeferred(() -> scrollRectToVisible(col, row));

					if (NavigatorUtil.isMobile()) {
						textField.prepareShowSymbolButton(false);
						textField.enableGGBKeyboard();
						textField.addDummyCursor();
						textField.setCursorPos(textField.getText().length());
					}
				} else if (!app.isWhiteboardActive()) {
					// if keyboard isn't enabled, inserts openkeyboard button
					// if there is no in the SV yet
					app.showKeyboard(textField, false);
				}

				// set position of the editor
				positionEditorPanel(true, row, col);

				// give it the focus
				textField.requestFocus();
				renderSelection();

				return true;

			case BOOLEAN:
			case BUTTON:
			case LIST:
				// instead of editing the checkbox, do not go into editing mode
				// at all,
				// because we don't know when to stop editing

				editing = false;
				positionEditorPanel(false, 0, 0);

				renderSelection();
				return true;
			}
		}

		BaseCellEditor mce = getCellEditor();
		if (mce != null) {
			mce.cancelCellEditing();
		}
		return false;
	}

	private SpreadsheetController getEditorController() {
		if (controller == null) {
			controller = new SpreadsheetController(app);
		}
		return controller;
	}

	public void setAllowEditing(boolean allowEditing) {
		this.allowEditing = allowEditing;
	}

	/**
	 * we need to return false for this normally, otherwise we can't detect
	 * double-clicks
	 * 
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @return whether given cell is editable
	 */
	public boolean isCellEditable(int row, int column) {
		if (view.isColumnSelect()) {
			return false;
		}

		// allow use of special editors for e.g. buttons, lists
		if (view.allowSpecialEditor()
		        && oneClickEditMap.containsKey(new GPoint(column, row))) {
			return true;
		}

		// normal case: return false so we can handle double click in our //
		// mouseReleased
		if (!allowEditing) {
			return false;
		}

		// prevent editing fixed geos when allowEditing == true
		GeoElement geo = (GeoElement) getModel().getValueAt(row, column);
		return geo == null || !geo.isProtected(EventType.UPDATE);
	}

	/**
	 * Finish editing current cell.
	 * 
	 * @param editNext
	 *            whether to go to next cell after
	 */
	public void finishEditing(boolean editNext) {
		editing = false;

		// hide the editor
		positionEditorPanel(false, 0, 0);
		if (!editNext) {
			view.requestFocus();
		}

		// setRepaintAll();//TODO: don't call renderCells, just change the
		// edited cell
		repaint();
	}

	public void sendEditorKeyPressEvent(KeyPressEvent e) {
		editor.sendKeyPressEvent(e);
	}

	public void sendEditorKeyDownEvent(KeyDownEvent e) {
		editor.sendKeyDownEvent(e);
	}

	/**
	 * Keep column widths of table and column header in sync.
	 * 
	 * @param column
	 *            column index
	 * @param width
	 *            column width
	 */
	public void setColumnWidth(int column, int width) {
		setColumnWidthSilent(column, width);
		this.view.settings().getWidthMap().put(column, width);

	}

	void setColumnWidthSilent(int column, int width) {
		int width2 = width;

		// TODO : check if this minimum width is valid,
		// if so create constant field

		// there is a minimal width in the Desktop version,
		// Web version should imitate this; this is visually looking
		// like 5px, but in the code, it seems that it is 15px
		if (width2 < 15) {
			width2 = 15;
		}

		if (column >= 0) {
			Resizer.setPixelWidth(ssGrid.getColumnFormatter().getElement(column),
			        width2);
			if (showColumnHeader) {
				columnHeader.setColumnWidth(column, width2);
			}
		}
	}

	/**
	 * Change width of all columns.
	 * 
	 * @param width
	 *            width in pixels
	 */
	public void setColumnWidth(final int width) {

		Scheduler.get().scheduleDeferred(() -> {

			minimumRowHeight = dummyTable.getCellFormatter()
					.getElement(0, 0).offsetHeight;

			int width2 = Math.max(width, minimumRowHeight);

			for (int col = 0; col < getColumnCount(); col++) {
				Resizer.setPixelWidth(ssGrid.getColumnFormatter().getElement(col), width2);
				if (showColumnHeader) {
					columnHeader.setColumnWidth(col, width2);
				}
			}
		});
	}

	// Keep row heights of table and row header in sync
	public void setRowHeight(final int row, final int rowHeight) {
		setRowHeight(row, rowHeight, true);
	}

	protected void setRowHeight(final int row, final int rowHeight,
			final boolean updateSettings) {
		Scheduler.get().scheduleDeferred(() -> {
			minimumRowHeight = dummyTable.getCellFormatter()
					.getElement(0, 0).offsetHeight;
			int rowHeight2 = Math.max(rowHeight, minimumRowHeight);
			setRowHeightCallback(row, rowHeight);
			if (updateSettings) {
				view.settings().getHeightMap().put(row, rowHeight2);
			}
		});
	}

	protected void setRowHeightCallback(int row, int rowHeight2) {
		if (row >= 0) {
			Resizer.setPixelHeight(ssGrid.getRowFormatter().getElement(row),
					rowHeight2);

			if (showRowHeader) {
				syncRowHeaderHeight(row);
			}
		}
		if (view != null) {
			if (doRecordRowHeights) {
				adjustedRowHeights.add(new GPoint(row, rowHeight2));
			}
		}
	}

	/**
	 * Keep table and row header heights in sync
	 * 
	 * @param row
	 *            row to sync
	 */
	public void syncRowHeaderHeight(final int row) {

		Scheduler.get().scheduleDeferred(() -> {
			int rowHeight = ssGrid.getRowFormatter().getElement(row)
					.offsetHeight;
			rowHeader.setRowHeight(row, rowHeight);
		});
	}
	
	/* 
	 * Fits the content of spreadsheet for its header on the left.
	 */
	public void syncTableTop() {
		scroller.syncTableTop();
	}

	/**
	 * Change row height of all rows
	 * 
	 * @param rowHeight
	 *            row height in pixels
	 * @param updateSettings
	 *            whether to change settings too
	 */
	public void setRowHeight(final int rowHeight,
			final boolean updateSettings) {

		Scheduler.get().scheduleDeferred(() -> {

			minimumRowHeight = dummyTable.getCellFormatter()
					.getElement(0, 0).offsetHeight;

			int rowHeight2 = Math.max(rowHeight, minimumRowHeight);

			for (int row = 0; row < getRowCount(); row++) {
				Resizer.setPixelHeight(ssGrid.getRowFormatter().getElement(row),
						rowHeight2);
				if (showRowHeader) {
					rowHeader.setRowHeight(row, rowHeight2);
				}
			}

			if (view != null && updateSettings) {
				view.updatePreferredRowHeight(rowHeight2);
			}

		});
	}

	/**
	 * Reset the row heights --- used after addColumn destroys the row heights.
	 */
	public void resetRowHeights() {
		doRecordRowHeights = false;
		for (GPoint p : adjustedRowHeights) {
			setRowHeight(p.x, p.y);
		}
		doRecordRowHeights = true;
	}

	// ==================================================
	// Table row and column size adjustment methods
	// ==================================================

	/**
	 * Enlarge the row and/or column of all marked cells. A cell is marked by
	 * placing it in one of two hashSets: cellResizeHeightSet or
	 * cellResizeWidthSet. Currently, this is only done after a geo is added to
	 * a cell and the row needs to be widened to fit the LaTeX image.
	 * 
	 */
	public void resizeMarkedCells() {

		if (!cellResizeHeightSet.isEmpty()) {
			for (GPoint cellPoint : cellResizeHeightSet) {
				setPreferredCellSize(cellPoint.getY(), cellPoint.getX(), false,
				        true);
			}
			cellResizeHeightSet.clear();
		}

		if (!cellResizeWidthSet.isEmpty()) {
			for (GPoint cellPoint : cellResizeWidthSet) {
				setPreferredCellSize(cellPoint.getY(), cellPoint.getX(), true,
				        false);
			}
			cellResizeWidthSet.clear();
		}
	}

	/**
	 * Enlarge the row and/or column of a cell to fit the cell's preferred size.
	 * Note: this is just temporary (e.g. dynamic LaTeX), do NOT update settings
	 */
	private void setPreferredCellSize(int row, int col, boolean adjustWidth,
	        boolean adjustHeight) {

		// in table model coordinates

		HTMLElement prefElement = ssGrid.getCellFormatter().getElement(row, col);

		if (adjustWidth) {

			HTMLElement tableColumn = ssGrid.getColumnFormatter().getElement(col);

			int resultWidth = Math.max(tableColumn.offsetWidth,
			        prefElement.offsetWidth);
			// TODO this. getIntercellSpacing ().width
			Resizer.setPixelWidth(tableColumn, resultWidth);

			columnHeader.setColumnWidth(col, resultWidth);
		}

		if (adjustHeight) {
			int resultHeight = Math.max(ssGrid.getRowFormatter()
			        .getElement(row).offsetHeight,
			        prefElement.offsetHeight);
			setRowHeight(row, resultHeight, false);
		}
	}

	/**
	 * Adjust the width of a column to fit the maximum preferred width of its
	 * cell contents.
	 */
	public void fitColumn(int column) {

		// in grid coordinates

		HTMLElement tableColumn = ssGrid.getColumnFormatter().getElement(column);

		int prefWidth = 0;
		int tempWidth = -1;
		for (int row = 0; row < getRowCount(); row++) {
			if (column >= 0 && tableModel.getValueAt(row, column) != null) {
				tempWidth = ssGrid.getCellFormatter().getElement(row, column)
				        .offsetWidth;
				prefWidth = Math.max(prefWidth, tempWidth);
			}
		}

		// set the new column width
		if (tempWidth == -1) {
			// column is empty
			prefWidth = preferredColumnWidth /*
											 * TODO getIntercellSpacing().width
											 */;
		} else {
			// There was "15" here, but in Desktop, it should visually look
			// like if there was "5"...
			prefWidth = Math.max(prefWidth, 15 /*
												 * TODO
												 * tableColumn.getMinWidth()
												 */);
		}
		// note: the table might have its header set to null,
		// so we get the actual header from view
		// TODO//view.getTableHeader().setResizingColumn(tableColumn);
		// TODO getIntercellSpacing().width
		Resizer.setPixelWidth(tableColumn, prefWidth);

		columnHeader.setColumnWidth(column, prefWidth);
	}

	/**
	 * Adjust the height of a row to fit the maximum preferred height of the its
	 * cell contents.
	 */
	public void fitRow(int row) {

		// in grid coordinates

		int prefHeight = ssGrid.getRowFormatter().getElement(row)
		        .offsetHeight;
		// int prefHeight = this.getRowHeight();
		int tempHeight = 0;
		for (int column = 0; column < this.getColumnCount(); column++) {
			tempHeight = ssGrid.getCellFormatter().getElement(row, column)
			        .offsetHeight;
			prefHeight = Math.max(prefHeight, tempHeight);
		}

		// set the new row height
		this.setRowHeight(row, prefHeight);
	}

	/**
	 * Adjust all rows/columns to fit the maximum preferred height/width of
	 * their cell contents.
	 * 
	 */
	public void fitAll(boolean doRows, boolean doColumns) {
		if (doRows) {
			for (int row = 0; row < getRowCount(); row++) {
				fitRow(row);
			}
		}
		if (doColumns) {
			for (int column = 0; column < getColumnCount(); column++) {
				// ?//fitRow(column);
				fitColumn(column);
			}
		}
	}

	// ==================================================
	// Table mode change
	// ==================================================

	@Override
	public int getTableMode() {
		return tableMode;
	}

	/**
	 * Sets the table mode
	 * 
	 * @param tableMode
	 *            mode from the MyTable.TABLE_ values
	 */
	@Override
	public void setTableMode(int tableMode) {
		if (tableMode == MyTable.TABLE_MODE_AUTOFUNCTION) {
			if (!initAutoFunction()) {
				return;
			}
		}

		else if (tableMode == MyTable.TABLE_MODE_DROP) {
			// nothing to do (yet)
		}

		else {
			// color is standard
			this.setSelectionRectangleColor(GColor.BLUE);
		}

		this.tableMode = tableMode;
		repaint();
	}

	// ==================================================
	// Autofunction handlers
	// ==================================================

	/**
	 * Initializes the autoFunction feature. The targetCell is prepared and the
	 * GUI is adjusted to handle selection drag with an autoFunction
	 */
	protected boolean initAutoFunction() {
		// Selection is a single cell.
		// The selected cell is the target cell. Allow the user to drag a new
		// selection for the
		// autoFunction. The autoFunction values are previewed in the targetCell
		// while dragging.
		if (selectedCellRanges.size() == 1
		        && selectedCellRanges.get(0).isSingleCell()) {

			// Clear the target cell, exit if this is not possible
			if (RelativeCopy.getValue(app, minSelectionColumn, minSelectionRow) != null) {
				boolean isOK = copyPasteCut.delete(minSelectionColumn,
				        minSelectionRow, minSelectionColumn, minSelectionRow);
				if (!isOK) {
					return false;
				}
			}

			// Set targetCell as a GeoNumeric that can be used to preview the
			// autofunction result
			// (later it will be set as a GeoList)
			getSpreadsheetModeProcessor().initTargetCell(minSelectionColumn,
					minSelectionRow);

			// Change the selection frame color to gray
			// and clear the current selection
			setSelectionRectangleColor(GColor.GRAY);
			minSelectionColumn = -1;
			maxSelectionColumn = -1;
			minSelectionRow = -1;
			maxSelectionRow = -1;
			app.getSelectionManager().clearSelectedGeos();
		}

		// try to create autoFunction cell(s) adjacent to the selection
		else if (selectedCellRanges.size() == 1) {

			try {
				getSpreadsheetModeProcessor()
						.performAutoFunctionCreation(selectedCellRanges.get(0),
								GlobalKeyDispatcherW.getShiftDown());
			} catch (Exception e) {
				Log.debug(e);
			}

			// Don't stay in this mode, we're done
			return false;
		} else {
			return false;
		}

		return true;
	}

	// ===========================================
	// copy/paste/cut/delete methods
	//
	// this is temporary code while cleaning up
	// ===========================================
	/**
	 * Copy & return string from selected cells
	 * 
	 * @return the content of selected cells
	 */
	public String copyString() {
		return ((CopyPasteCutW) copyPasteCut).copyString(getSelectedColumn(),
				getSelectedRow(), getMaxSelectedColumn(), getMaxSelectedRow());
	}

	/**
	 * Copy selected cells to (virtual) clipboard.
	 * 
	 * @param altDown
	 *            whether alt is pressed
	 */
	public void copy(boolean altDown) {
		copyPasteCut.copy(getSelectedColumn(), getSelectedRow(),
				getMaxSelectedColumn(), getMaxSelectedRow(), altDown);
	}

	/**
	 * Copy string from selected cells
	 * 
	 * @param altDown
	 *            whether alt is pressed
	 * @param nat
	 *            whether this is native event
	 */
	public void copy(boolean altDown, boolean nat) {
		((CopyPasteCutW) copyPasteCut).copy(getSelectedColumn(),
				getSelectedRow(), getMaxSelectedColumn(), getMaxSelectedRow(),
				altDown, nat);
	}

	/**
	 * Paste (virtual) clipboard into spreadsheet
	 * 
	 * @return success
	 */
	public boolean paste() {
		return copyPasteCut.paste(getSelectedColumn(), getSelectedRow(),
				getMaxSelectedColumn(), getMaxSelectedRow());
	}

	/**
	 * Paste string to selected cells
	 * 
	 * @param content
	 *            cell content
	 * @return success
	 */
	public boolean paste(String content) {
		return ((CopyPasteCutW) copyPasteCut).paste(getSelectedColumn(),
				getSelectedRow(), getMaxSelectedColumn(), getMaxSelectedRow(),
				content);
	}

	/**
	 * Cut string from selected cells
	 * 
	 * @return if at least one object was deleted
	 */
	public boolean cut() {
		return copyPasteCut.cut(getSelectedColumn(), getSelectedRow(),
				getMaxSelectedColumn(), getMaxSelectedRow());
	}

	/**
	 * Cut string from selected cells
	 * 
	 * @param nat
	 *            whether this is native event
	 * @return success
	 */
	public boolean cut(boolean nat) {
		return ((CopyPasteCutW) copyPasteCut).cut(getSelectedColumn(),
				getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow(), nat);
	}

	/**
	 * Delete content of selected cells.
	 * 
	 * @return success
	 */
	public boolean delete() {
		return copyPasteCut.delete(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow());
	}

	/**
	 * Updates all cell formats and the current selection.
	 */
	@Override
	public void repaintAll() {
		repaintAll = true;
		repaint();
	}

	/**
	 * Updates only the current selection. For efficiency the cell formats are
	 * not updated.
	 */
	@Override
	public void repaint() {
		if (repaintAll) {
			updateAllCellFormats();
			repaintAll = false;
		}

		renderSelection();
	}

	/**
	 * @param row
	 *            row
	 * @param column
	 *            column
	 */
	public void updateCellFormat(int row, int column) {
		GeoElement geo = (GeoElement) tableModel.getValueAt(row, column);
		defaultTableCellRenderer.updateCellFormat(geo, row, column);
	}

	/**
	 * Update format of all cells.
	 */
	public void updateAllCellFormats() {
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				defaultTableCellRenderer.clearBorder(row, column);
				updateCellFormat(row, column);
			}
		}
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				defaultTableCellRenderer.updateCellBorder(row, column);
			}
		}
	}

	/**
	 * Update format of given cells.
	 * 
	 * @param cellRangeList
	 *            cells to update
	 */
	public void updateCellFormat(ArrayList<CellRange> cellRangeList) {
		for (int i = 0; i < cellRangeList.size(); i++) {
			CellRange cr = cellRangeList.get(i);
			for (int row = cr.getMinRow(); row <= cr.getMaxRow(); row++) {
				for (int column = cr.getMinColumn(); column <= cr
				        .getMaxColumn(); column++) {
					updateCellFormat(row, column);
				}
			}
		}
	}

	@Override
	public void updateTableCellValue(Object value, int row, int column) {
		if (defaultTableCellRenderer != null) {
			defaultTableCellRenderer.updateTableCellValue(ssGrid, value, row,
			        column);
		}
	}

	/**
	 * Check we can show blue dot: not editing, selection has no fixed cells.
	 * 
	 * @return whether to show blue dot
	 */
	public boolean showCanDragBlueDot() {
		boolean showBlueDot = !isEditing();

		if (minSelectionRow != -1 && maxSelectionRow != -1
		        && minSelectionColumn != -1 && maxSelectionColumn != -1) {

			if (showBlueDot) {
				for (int i = minSelectionRow; i <= maxSelectionRow; i++) {
					for (int j = minSelectionColumn; j <= maxSelectionColumn; j++) {
						if (tableModel.getValueAt(i, j) instanceof GeoElement) {
							showBlueDot &= !((GeoElement) tableModel
									.getValueAt(i, j))
											.isProtected(EventType.UPDATE);
						}
					}
				}
			}

			return showBlueDot;
		}
		return false;
	}

	/**
	 * Render selection with a delay.
	 */
	public void renderSelectionDeferred() {

		Scheduler.get().scheduleDeferred(this::renderSelection);
	}

	void renderSelection() {
		// TODO implement other features from the old paint method

		// draw dragging frame

		GPoint point1 = new GPoint(0, 0);
		GPoint point2 = new GPoint(0, 0);

		if (draggingToRow != -1 && draggingToColumn != -1) {

			// -|1|-
			// 2|-|3
			// -|4|-
			boolean visible = true;
			if (draggingToColumn < minSelectionColumn) { // 2
				point1 = getPixel(draggingToColumn, minSelectionRow, true);
				point2 = getPixel(minSelectionColumn - 1, maxSelectionRow,
				        false);

			} else if (draggingToRow > maxSelectionRow) { // 4
				point1 = getPixel(minSelectionColumn, maxSelectionRow + 1, true);
				point2 = getPixel(maxSelectionColumn, draggingToRow, false);

			} else if (draggingToRow < minSelectionRow) { // 1
				point1 = getPixel(minSelectionColumn, draggingToRow, true);
				point2 = getPixel(maxSelectionColumn, minSelectionRow - 1,
				        false);

			} else if (draggingToColumn > maxSelectionColumn) { // 3
				point1 = getPixel(maxSelectionColumn + 1, minSelectionRow, true);
				point2 = getPixel(draggingToColumn, maxSelectionRow, false);
			} else {
				visible = false;
			}

			updateDragFrame(visible, point1, point2);

		} else {

			updateDragFrame(false, point1, point2);
		}

		GPoint min = this.getMinSelectionPixel();
		GPoint max = this.getMaxSelectionPixel(true);

		if (minSelectionRow != -1 && maxSelectionRow != -1
		        && minSelectionColumn != -1 && maxSelectionColumn != -1) {
			updateSelectionFrame(true, showCanDragBlueDot(), min, max);
		} else {
			updateSelectionFrame(false, false, min, max);
		}

		// cells
		GeoElement geo;
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();
		for (int col = maxColumn; col >= 0; col--) {
			for (int row = maxRow; row >= 0; row--) {
				geo = (GeoElement) tableModel.getValueAt(row, col);
				defaultTableCellRenderer.updateCellBackground(geo, row, col);
			}
		}

		// After rendering the LaTeX image for a geo, update the row height
		// with the preferred size set by the renderer.
		resizeMarkedCells();
	}

	/**
	 * @return min selected row
	 */
	public int getSelectedRow() {
		if (minSelectionRow < 0) {
			return -1;
		}
		return minSelectionRow;
	}

	/**
	 * @return min selected ccolumn
	 */
	public int getSelectedColumn() {
		if (minSelectionColumn < 0) {
			return -1;
		}
		return minSelectionColumn;
	}

	/**
	 * @return max selected row
	 */
	public int getMaxSelectedRow() {
		if (maxSelectionRow < 0) {
			return -1;
		}
		return maxSelectionRow;
	}

	/**
	 * @return max selected column
	 */
	public int getMaxSelectedColumn() {
		if (maxSelectionColumn < 0) {
			return -1;
		}
		return maxSelectionColumn;
	}

	/**
	 * Change row and column header visibility
	 * 
	 * @param showRow whether to show row header
	 * @param showCol whether to show column header
	 */
	public void setShowHeader(boolean showRow, boolean showCol) {
		showRowHeader = showRow;
		showColumnHeader = showCol;
		updateTableLayout();
	}

	@Override
	public void updateCellFormat(String cellFormatString) {
		view.updateCellFormat(cellFormatString);
	}

	@Override
	public boolean allowSpecialEditor() {
		return view.allowSpecialEditor();
	}

	@Override
	public int getColumnCount() {
		return tableModel.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return tableModel.getRowCount();
	}

	public int getOffsetWidth() {
		return tableWrapper.getOffsetWidth();
	}

	public int getOffsetHeight() {
		return tableWrapper.getOffsetHeight();
	}

	/**
	 * @param width
	 *            table width
	 * @param height
	 *            table height
	 */
	public void setSize(int width, int height) {
		tableWrapper.setPixelSize(width, height);
		scroller.setPixelSize(width - rowHeader.getOffsetWidth(), height
		        - columnHeader.getOffsetHeight());
	}

	/**
	 * 
	 * @param visible
	 *            whether selection is visible
	 * @param showDragHandle
	 *            whether to show blue dot
	 * @param corner1
	 *            top left corner
	 * @param corner2
	 *            bottom right corner
	 */
	public void updateSelectionFrame(boolean visible, boolean showDragHandle,
	        GPoint corner1, GPoint corner2) {
		if (selectionFrame == null) {
			return;
		}
		selectionFrame.setVisible(false);
		if (!visible) {
			blueDot.setVisible(false);
		} else if (corner1 != null && corner2 != null) {
			showSelectionFrame(corner1, corner2, showDragHandle);
		}
	}

	private void showSelectionFrame(GPoint corner1, GPoint corner2, boolean showDragHandle) {
		int borderWidth = 2;
		int x1 = Math.min(corner1.x, corner2.x);
		int x2 = Math.max(corner1.x, corner2.x);
		int y1 = Math.min(corner1.y, corner2.y);
		int y2 = Math.max(corner1.y, corner2.y);
		int h = y2 - y1 - 2 * borderWidth;
		int w = x2 - x1 - 2 * borderWidth;
		if (w >= 0 && h >= 0) {
			int ssTop = gridPanel.getAbsoluteTop();
			int ssLeft = gridPanel.getAbsoluteLeft();

			selectionFrame.setWidth(w + "px");
			selectionFrame.setHeight(h + "px");

			gridPanel.setWidgetPosition(selectionFrame, x1 - ssLeft,
					y1 - ssTop);

			blueDot.setVisible(showDragHandle);
			if (showDragHandle) {
				gridPanel.setWidgetPosition(blueDot,
						x2 - ssLeft - MyTableW.DOT_SIZE / 2 - 1,
						y2 - ssTop - MyTableW.DOT_SIZE / 2 - 1);
			}
			selectionFrame.setVisible(true);
		}
	}

	private void updateDragFrame(boolean visible, GPoint corner1,
			GPoint corner2) {
		if (dragFrame == null) {
			return;
		}
		int borderWidth = 1;
		dragFrame.setVisible(visible);

		if (visible) {
			int x1 = Math.min(corner1.x, corner2.x);
			int x2 = Math.max(corner1.x, corner2.x);
			int y1 = Math.min(corner1.y, corner2.y);
			int y2 = Math.max(corner1.y, corner2.y);
			int h = y2 - y1 - 2 * borderWidth - 1;
			int w = x2 - x1 - 2 * borderWidth - 1;

			gridPanel.setWidgetPosition(dragFrame,
			        x1 - gridPanel.getAbsoluteLeft(),
			        y1 - gridPanel.getAbsoluteTop());

			dragFrame.setWidth(w + "px");
			dragFrame.setHeight(h + "px");
		}
	}

	private void positionEditorPanel(boolean visible, int row, int column) {
		if (editorPanel == null) {
			return;
		}
		editorPanel.setVisible(visible);
		if (visible) {
			GPoint p = getPixel(column, row, true);
			GPoint m = getPixel(column, row, false, true);

			int ssTop = gridPanel.getAbsoluteTop();
			int ssLeft = gridPanel.getAbsoluteLeft();
			
			gridPanel.setWidgetPosition(editorPanel, p.x - ssLeft, p.y - ssTop);
			editorPanel.setVisible(true);
			int w = m.x - p.x;
			int h = m.y - p.y;
			Resizer.setPixelWidth(editorPanel.getElement(), w);
			Resizer.setPixelHeight(editorPanel.getElement(), h);
		}
	}

	public void setVerticalScrollPosition(int scrollPosition) {
		scroller.setVerticalScrollPosition(scrollPosition);
	}

	public void setHorizontalScrollPosition(int scrollPosition) {
		scroller.setHorizontalScrollPosition(scrollPosition);
	}

	public int getHorizontalScrollPosition() {
		return scroller.getHorizontalScrollPosition();
	}

	public int getVerticalScrollPosition() {
		return scroller.getVerticalScrollPosition();
	}

	/**
	 * Update font sizes.
	 */
	public void updateFonts() {
		setRowHeight(0, false);
		resetRowHeights();
		renderSelection();
	}

	public void setShowVScrollBar(boolean showVScrollBar) {
		scroller.setShowVScrollBar(showVScrollBar);
	}

	public void setShowHScrollBar(boolean showHScrollBar) {
		scroller.setShowHScrollBar(showHScrollBar);
	}

	public void setEnableAutoComplete(boolean enableAutoComplete) {
		editor.setEnableAutoComplete(enableAutoComplete);
	}

	/**
	 * Update spreadsheet when zoom level changes
	 * 
	 * @param ratio
	 *            CSS pixel ratio
	 */
	public void setPixelRatio(double ratio) {
		if (editor != null) {
			editor.stopCellEditing();
		}
		view.setRowHeightsFromSettings();
		for (int row = 0; row < this.getRowCount(); row++) {
			for (int column = 0; column < this.getColumnCount(); column++) {
				if (ssGrid.getWidget(row, column) instanceof Canvas) {
					this.updateTableCellValue(app.getSpreadsheetTableModel()
							.getValueAt(row, column), row, column);
				}
			}
		}
	}

	/**
	 * Allow automatic edits.
	 */
	public void setAllowAutoEdit() {
		if (editor != null) {
			editor.setAllowAutoEdit();
		}
	}

	/**
	 * 
	 * @return spreadsheet mode processor
	 */
	public SpreadsheetModeProcessor getSpreadsheetModeProcessor() {
		if (this.spredsheetModeProcessor == null) {
			this.spredsheetModeProcessor = new SpreadsheetModeProcessor(app,
					this);
		}
		return this.spredsheetModeProcessor;
	}

	public void addResizeHeight(GPoint gPoint) {
		cellResizeHeightSet.add(gPoint);
	}
}
