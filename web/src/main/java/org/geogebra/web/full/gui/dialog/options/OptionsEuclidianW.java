package org.geogebra.web.full.gui.dialog.options;

import static org.geogebra.web.full.gui.util.NumberListBox.PI_HALF_STRING;

import java.util.Arrays;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.OptionsEuclidian;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.IEuclidianOptionsListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.common.properties.impl.graphics.GridStyleProperty;
import org.geogebra.common.properties.impl.graphics.PointCapturingProperty;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentCombobox;
import org.geogebra.web.full.gui.components.dropdown.grid.GridDropdown;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.images.PropertiesResources;
import org.geogebra.web.full.gui.util.LineStylePopup;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabBar;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.gwtproject.resources.client.ImageResource;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

public class OptionsEuclidianW extends OptionsEuclidian implements OptionPanelW,
	IEuclidianOptionsListener {

	protected AppW app;
	protected MultiRowsTabPanel tabPanel;
	protected EuclidianView view;
	public EuclidianOptionsModel model;
	protected BasicTab basicTab;
	AxisTab xAxisTab;
	AxisTab yAxisTab;
	private GridTab gridTab;
	private boolean isIniting;
	public Localization loc;

	protected static abstract class EuclidianTab extends FlowPanel
			implements SetLabels {
		
		protected EuclidianTab() {
			setStyleName("propertiesTab");
		}
		
		public void onResize(int height, int width) {
			this.setHeight(height + "px");
			this.setWidth(width + "px");
		}
	}

	protected class AxisTab extends EuclidianTab {
		private AxisPanel axisPanel;
			
		public AxisTab(int axis, boolean view3D) {
			super();
			axisPanel = new AxisPanel(app, view, axis, view3D);
			add(axisPanel);
		}
		
		public void updateView(EuclidianView view1) {
			axisPanel.updateView(view1);
		}

		public void setShowAxis(boolean value) {
			axisPanel.setShowAxis(value);
		}

		@Override
		public void setLabels() {
			axisPanel.setLabels();
		}
	}
		
	protected class GridTab extends EuclidianTab {
		ComponentCheckbox cbShowGrid;
		private FormLabel lbPointCapturing;
		private CompDropDown pointCapturingStyle;
		CompDropDown lbGridType;
		GridDropdown lbRulerType = null;
		ComponentCheckbox cbGridManualTick;
		ComponentCombobox ncbGridTickX;
		ComponentCombobox ncbGridTickY;
		ComponentCombobox cbGridTickAngle;
		private FormLabel gridLabel1;
		private FormLabel gridLabel2;
		private FormLabel gridLabel3;
		protected FormLabel lblGridType;
		private FormLabel lblRulerType;
		private Label lblGridStyle;
		LineStylePopup btnGridStyle;
		private Label lblColor;
		private ComponentCheckbox cbBoldGrid;
		private StandardButton btGridColor;
		private FlowPanel mainPanel;
		/**
		 * special grid types for mow (e.g. 3/4 or 1/2)
		 */
		protected boolean gridOptions = !app.isWhiteboardActive();
		private FlowPanel stylePanel;

		public GridTab() {
			super();
			mainPanel = new FlowPanel();
			if (gridOptions) {
				cbShowGrid = new ComponentCheckbox(app.getLocalization(), true, "ShowGrid",
				selected -> {
					enableGrid(selected);
					app.storeUndoInfo();
				});

				add(cbShowGrid);
				addPointCapturingStyle();
			}
			add(mainPanel);
			initGridTypePanel();
			initGridStylePanel();
			initRulerType(mainPanel);
		}
		
		/**
		 * update gui of grid tab
		 */
		public void updateGUI() {
			updatePointCapturingStyleList();
		}

		private void addPointCapturingStyle() {
			if (!gridOptions) {
				return;
			}

			EnumerableProperty pointCaptProperty = new PointCapturingProperty(app,
					app.getLocalization());
			pointCapturingStyle = new CompDropDown(app, pointCaptProperty);
			pointCapturingStyle.addChangeHandler(() -> {
				app.setUnsaved();
				app.storeUndoInfo();
			});
			lbPointCapturing = new FormLabel(
					loc.getMenu("PointCapturing") + ":")
							.setFor(pointCapturingStyle);
			lbPointCapturing.addStyleName("dropDownLabel");
			updatePointCapturingStyleList();
			mainPanel.add(LayoutUtilW.panelRowIndent(lbPointCapturing,
					pointCapturingStyle));
		}

		private void updatePointCapturingStyleList() {
			if (!gridOptions) {
				return;
			}
			pointCapturingStyle.setLabels();
		}

		void enableGrid(boolean value) {
			if (!gridOptions) {
				return;
			}

			model.showGrid(value);
			if (value) {
				mainPanel.removeStyleName("disabled");
			} else {
				mainPanel.setStyleName("disabled");
			}
			lbGridType.setDisabled(!value);
			cbGridManualTick.setDisabled(!value);
			btnGridStyle.setEnabled(value);
			cbBoldGrid.setDisabled(!value);
			btGridColor.setEnabled(value);
		}

		private void initGridTypePanel() {
			if (!gridOptions) {
				return;
			}
			EnumerableProperty gridTypeProperty = new GridStyleProperty(app.getLocalization(),
					view.getSettings());
			lbGridType = new CompDropDown(app, gridTypeProperty);
			lblGridType = new FormLabel("").setFor(lbGridType);
			mainPanel.add(lblGridType);
			lblGridType.setStyleName("panelTitle");
			
			lbGridType.addChangeHandler(() -> {
				int type = view.getSettings().getGridType();
				view.setGridType(type);
				if (type == EuclidianView.GRID_POLAR) {
					view.updateBounds(true, true);
				}
				updateView();
			});

			cbGridManualTick = new ComponentCheckbox(app.getLocalization(), false, "TickDistance",
				selected -> {
					model.applyGridManualTick(selected);
					updateView();
				});

			ncbGridTickX = new ComponentCombobox(app, "", Arrays.asList("1",
					Unicode.PI_STRING, PI_HALF_STRING));
			ncbGridTickX.addChangeHandler(() -> {
				model.applyGridTicks(ncbGridTickX.getSelectedText(), 0);
				updateView();
			});
	
			ncbGridTickY = new ComponentCombobox(app, "", Arrays.asList("1",
					Unicode.PI_STRING, PI_HALF_STRING));
			ncbGridTickY.addChangeHandler(() -> {
					model.applyGridTicks(ncbGridTickY.getSelectedText(), 1);
					updateView();
			});

			cbGridTickAngle =  new ComponentCombobox(app, "",
					Arrays.asList(Unicode.PI_STRING + "/12",
					Unicode.PI_STRING + "/6", Unicode.PI_STRING + "/4",
					Unicode.PI_STRING + "/3", Unicode.PI_STRING + "/2"));
			cbGridTickAngle.addChangeHandler(() -> {
					model.applyGridTickAngle(cbGridTickAngle.getSelectedText());
					updateView();
			});
			
			FlowPanel gridTickAnglePanel = new FlowPanel();
			gridTickAnglePanel.setStyleName("panelRow");
			addGridType(gridTickAnglePanel);

			// grid labels
			gridLabel1 = new FormLabel("x:").setFor(this.ncbGridTickX);
			gridLabel1.addStyleName("dropDownLabel");
			gridLabel2 = new FormLabel("y:").setFor(this.ncbGridTickY);
			gridLabel2.addStyleName("dropDownLabel");
			gridLabel3 = new FormLabel(Unicode.theta + ":")
					.setFor(cbGridTickAngle);
			gridLabel3.addStyleName("dropDownLabel");
			
			FlowPanel ncbGridTickXPanel = new FlowPanel();
			FlowPanel ncbGridTickYPanel = new FlowPanel();
			FlowPanel ncbGridTickAnglePanel = new FlowPanel();
			ncbGridTickXPanel.setStyleName("panelRowCell");
			ncbGridTickYPanel.setStyleName("panelRowCell");
			ncbGridTickAnglePanel.setStyleName("panelRowCell");
			ncbGridTickXPanel.add(gridLabel1);
			ncbGridTickXPanel.add(ncbGridTickX);
			ncbGridTickYPanel.add(gridLabel2);
			ncbGridTickYPanel.add(ncbGridTickY);
			ncbGridTickAnglePanel.add(gridLabel3);
			ncbGridTickAnglePanel.add(cbGridTickAngle);
		
			FlowPanel tickPanel = LayoutUtilW.panelRow(cbGridManualTick, ncbGridTickXPanel, 
					ncbGridTickYPanel, ncbGridTickAnglePanel);
			mainPanel.add(tickPanel);
			
			FlowPanel typePanel = new FlowPanel();
			typePanel.add(gridTickAnglePanel);
			typePanel.add(cbGridManualTick);
			typePanel.add(LayoutUtilW.panelRowIndent(
					ncbGridTickXPanel, ncbGridTickYPanel, ncbGridTickAnglePanel));
			typePanel.setStyleName("panelIndent");
			typePanel.addStyleName("tickPanel");
			mainPanel.add(typePanel);
		}

		private void initRulerType(FlowPanel panel) {
			if (gridOptions) {
				return;
			}

			lbRulerType = new GridDropdown(app);
			TestHarness.setAttr(lbRulerType, "rulingDropdown");
			lblRulerType = new FormLabel(loc.getMenu("Ruling"))
					.setFor(lbRulerType);
			lbRulerType.setListener((dropdown, index) -> {
				model.applyRulerType(BackgroundType.rulingOptions.get(index));
				updateView();
				((EuclidianViewW) view).doRepaint();
				app.storeUndoInfo();
			});
			panel.add(LayoutUtilW.panelRowIndent(lblRulerType, lbRulerType));
		}
		
		protected void addGridType(FlowPanel gridTickAnglePanel) {
			gridTickAnglePanel.add(lbGridType);
		}

		private void initGridStylePanel() {

			// line style
			btnGridStyle = LineStylePopup.create(app, false);
			
			lblGridStyle = new Label();
			addOnlyFor2D(lblGridStyle);
			lblGridStyle.setStyleName("panelTitle");
			btnGridStyle.addPopupHandler(actionButton -> {
				int style = EuclidianView.getLineType(btnGridStyle.getSelectedIndex());
				if (gridOptions) {
					model.applyGridStyle(style);
				} else {
					model.applyRulerStyle(style);
				}
			});
			btnGridStyle.setKeepVisible(false);

			// color
			lblColor = new Label();
			btGridColor = new StandardButton(24);
			btGridColor.addFastClickHandler(event -> {
				if (gridOptions && !cbShowGrid.isSelected()) {
					return;
				}
				getDialogManager().showColorChooserDialog(
						gridOptions ? model.getGridColor()
								: model.getRulerColor(),
						new ColorChangeHandler() {

							@Override
							public void onForegroundSelected() {
								// no background for grid
							}

							@Override
							public void onColorChange(GColor color) {
								if (gridOptions) {
									model.applyGridColor(color);
								} else {
									model.applyRulerColor(color);
								}
								updateGridColorButton(color);
							}

							@Override
							public void onClearBackground() {
								// no background for grid
							}

							@Override
							public void onBackgroundSelected() {
								// no background for grid
							}

							@Override
							public void onAlphaChange() {
								// no alpha for grid
							}

							@Override
							public void onBarSelected() {
								// no bars
							}
						});
				// Just for dummy.
				//
			});
			// bold
			cbBoldGrid = new ComponentCheckbox(app.getLocalization(), false, "Bold",
					selected -> {
						if (gridOptions) {
							model.applyBoldGrid(selected);
						} else {
							model.applyBoldRuler(selected);
						}
						updateView();
				});

			// style panel
			stylePanel = new FlowPanel();

			stylePanel.add(LayoutUtilW.panelRowIndent(btnGridStyle));
			stylePanel.add(LayoutUtilW.panelRowIndent(lblColor, btGridColor, cbBoldGrid));
			
			addOnlyFor2D(stylePanel);
		}

		protected void addOnlyFor2D(Widget w) {
			mainPanel.add(w);
		}

		@Override
		public void setLabels() {
			if (gridOptions) {
				cbShowGrid.setLabels();
				setTextColon(lbPointCapturing, "PointCapturing");
				updatePointCapturingStyleList();
				setGridTypeLabel();
				model.fillGridTypeCombo();
				lbGridType.setLabels();

				model.fillAngleOptions();
				cbGridManualTick.setLabels();
			}

			lblGridStyle.setText(loc.getMenu("LineStyle"));
			lblColor.setText(loc.getMenu("Color") + ":");
			cbBoldGrid.setLabels();

			if (!gridOptions) {
				lblRulerType.setText(loc.getMenu("Ruling") + ":");
				int idx1 = lbRulerType.getSelectedIndex();
				lbRulerType.clear();
				model.fillRulingCombo();
				lbRulerType.setSelectedIndex(idx1);
			}
		}
		
		protected void setGridTypeLabel() {
			if (!gridOptions) {
				return;
			}

			lblGridType.setText(loc.getMenu("GridType"));
		}

		public void update(GColor color, boolean isShown, boolean isBold) {
			if (!gridOptions) {
				return;
			}
			stylePanel.setVisible(true);
			enableGrid(isShown);
			cbShowGrid.setSelected(isShown);
			cbBoldGrid.setSelected(isBold);
			lbGridType.resetToDefault();
			lbGridType.setLabels();
			btGridColor.getElement().getStyle().setColor(StringUtil.toHtmlColor(color));
			updateGridColorButton(color);
		}
	
		/**
		 * @param isAutoGrid
		 *            true if auto
		 * @param gridTicks
		 *            grid ticks
		 * @param gridType
		 *            type of grid
		 */
		public void updateTicks(boolean isAutoGrid, double[] gridTicks,
				int gridType) {
			if (!gridOptions) {
				return;
			}

			if (gridType != EuclidianView.GRID_POLAR) {
				ncbGridTickY.setVisible(true);
				gridLabel2.setVisible(true);
				cbGridTickAngle.setVisible(false);
				gridLabel3.setVisible(false);
				ncbGridTickX.setValue(model.gridTickToString(gridTicks[0]));
				ncbGridTickY.setValue(model.gridTickToString(gridTicks[1]));
				gridLabel1.setText("x:");
			} else {
				ncbGridTickY.setVisible(false);
				gridLabel2.setVisible(false);
				cbGridTickAngle.setVisible(true);
				gridLabel3.setVisible(true);
				ncbGridTickX.setValue(model.gridTickToString(gridTicks[0]));
				cbGridTickAngle.setValue(model.gridAngleToString());
				gridLabel1.setText("r:");
			}

			ncbGridTickX.setDisabled(isAutoGrid);
			ncbGridTickY.setDisabled(isAutoGrid);
			cbGridTickAngle.setDisabled(isAutoGrid);
		}

		/**
		 * @param style
		 *            of grid lines
		 */
		public void selectGridStyle(int style) {
			if (!gridOptions) {
				return;
			}
			btnGridStyle.selectLineType(style);
		}

		private void selectRulerStyle(int style) {
			btnGridStyle.selectLineType(style);
		}

		/**
		 * @param color
		 *            of grid lines
		 */
		public void updateGridColorButton(GColor color) {
			ImageOrText content = new ImageOrText();
			content.setBgColor(color);
			btGridColor.setIcon(content);
		}

		/**
		 * @param item
		 *            add drop-down menu item with text
		 */
		public void addRulerTypeItem(String item, BackgroundType type) {
			if (gridOptions) {
				return;
			}
			ImageResource background = getResourceForBackgroundType(type);
			lbRulerType.addItem(item, background);
		}

		private ImageResource getResourceForBackgroundType(BackgroundType type) {
			switch (type) {
				case RULER:
					return PropertiesResources.INSTANCE.linedRuling();
				case SQUARE_SMALL:
					return PropertiesResources.INSTANCE.squared5Ruling();
				case SQUARE_BIG:
					return PropertiesResources.INSTANCE.squared1Ruling();
				case ELEMENTARY12_COLORED:
					return PropertiesResources.INSTANCE.coloredRuling();
				case ELEMENTARY12:
					return PropertiesResources.INSTANCE.elementary12Ruling();
				case ELEMENTARY12_HOUSE:
					return PropertiesResources.INSTANCE.houseRuling();
				case ELEMENTARY34:
					return PropertiesResources.INSTANCE.elementary34Ruling();
				case MUSIC:
					return PropertiesResources.INSTANCE.musicRuling();
				case ISOMETRIC:
					return PropertiesResources.INSTANCE.isometricRuling();
				case POLAR:
					return PropertiesResources.INSTANCE.polarRuling();
				default:
					return AppResources.INSTANCE.empty();
			}
		}

		/**
		 * @param idx
		 *            index of ruler type
		 */
		public void setRulerType(int idx) {
			if (gridOptions) {
				return;
			}
			lbRulerType.setSelectedIndex(idx);
		}

		/**
		 * Update ruler properties.
		 * 
		 * @param typeIdx
		 *            The type.
		 * @param color
		 *            to set.
		 * @param lineStyle
		 *            The line style.
		 * @param bold
		 *            true if the lines should be bold.
		 */
		public void updateRuler(int typeIdx, GColor color, int lineStyle, boolean bold) {
			if (gridOptions) {
				return;
			}
			BackgroundType bgType = BackgroundType.fromInt(typeIdx);
			setRulerType(BackgroundType.rulingOptions.indexOf(bgType));
			if (bgType == BackgroundType.NONE || bgType.isSVG()) {
				stylePanel.setVisible(false);
			} else {
				stylePanel.setVisible(true);
				updateGridColorButton(color);
				selectRulerStyle(lineStyle);
				cbBoldGrid.setSelected(bold);
			}
		}
	}
	
	/**
	 * @param app
	 *            application
	 * @param activeEuclidianView
	 *            view
	 */
	public OptionsEuclidianW(AppW app,
			EuclidianViewInterfaceCommon activeEuclidianView) {
		isIniting = true;
		this.app = app;
		this.loc = app.getLocalization();
		this.view = (EuclidianView) activeEuclidianView;
		model = new EuclidianOptionsModel(app, view, this);
		initGUI();
		view.setOptionPanel(this);
		isIniting = false;
	}

	/**
	 * update the view (also for model)
	 * 
	 * @param euclidianView
	 *            view
	 */
	public void updateView(EuclidianView euclidianView) {
		setView(euclidianView);
		euclidianView.setOptionPanel(this);
		model.setView(euclidianView);
		xAxisTab.updateView(euclidianView);
		yAxisTab.updateView(euclidianView);
	}

	private void initGUI() {
		tabPanel = new MultiRowsTabPanel();
		addTabs();
		updateGUI();
		tabPanel.selectTab(0);
		app.setDefaultCursor();
		tabPanel.addSelectionHandler(event -> updateGUI());
	}
	
	/**
	 * add tabs
	 */
	protected void addTabs() {
		addBasicTab();
		addAxesTabs();
		addGridTab();
	}
	
	/**
	 * add tabs for axes
	 */
	protected void addAxesTabs() {
		addXAxisTab();
		addYAxisTab();
	}

	private void addBasicTab() {
		basicTab = newBasicTab();
		tabPanel.add(basicTab, "basic");
	}
	
	protected BasicTab newBasicTab() {
		return new BasicTab(this);
	}
	
	private void addXAxisTab() {
		xAxisTab = newAxisTab(EuclidianOptionsModel.X_AXIS);
		tabPanel.add(xAxisTab, "x");
	}
	
	private void addYAxisTab() {
		yAxisTab = newAxisTab(EuclidianOptionsModel.Y_AXIS);
		tabPanel.add(yAxisTab, "y");
	}
	
	/**
	 * 
	 * @param axis axis id
	 * @return axis tab
	 */
	protected AxisTab newAxisTab(int axis) {
		return new AxisTab(axis, false);
	}
	
	private void addGridTab() {
		gridTab = newGridTab();
		tabPanel.add(gridTab, "grid");
	}
	
	/**
	 * 
	 * @return new grid tab
	 */
	protected GridTab newGridTab() {
		return new GridTab();
	}

	/**
	 * set labels
	 * @param tabBar tab bar
	 * @param gridIndex index for grid tab
	 */
	protected void setLabels(MultiRowsTabBar tabBar, int gridIndex) {
		tabBar.setTabText(0, loc.getMenu("Properties.Basic"));
		tabBar.setTabText(1, loc.getMenu("xAxis"));
		tabBar.setTabText(2, loc.getMenu("yAxis"));
		tabBar.setTabText(gridIndex, loc.getMenu("Grid"));

		basicTab.setLabels();
		xAxisTab.setLabels();
		yAxisTab.setLabels();
		gridTab.setLabels();
	}

	/**
	 * set labels
	 */
	public void setLabels() {
		setLabels(tabPanel.getTabBar(), 3);
	}

	/**
	 * Set & update UI
	 * 
	 * @param euclidianView1
	 *            graphics view
	 */
	public void setView(EuclidianView euclidianView1) {
		this.view = euclidianView1;
		if (!isIniting) {
			updateGUI();
		}
	}

	@Override
	public void updateGUI() {
		setLabels(); // resets all comboboxes: call *before* properties update
		model.updateProperties();
		getGridTab().updateGUI();
	}

	@Override
	public void updateBounds() {
		basicTab.updateBounds();
	}

	@Override
	public Widget getWrappedPanel() {
		return tabPanel;
	}
	
	protected AutoCompleteTextFieldW getTextField() {
		InputPanelW input = new InputPanelW(null, app, 1, -1, true);
		AutoCompleteTextFieldW tf = input.getTextComponent();
		tf.setStyleName("numberInput");
		return tf;
	}
	
	@Override
	public GColor getEuclidianBackground(int viewNumber) {
		return app.getSettings().getEuclidian(viewNumber).getBackground();
	}

	/**
	 * @return grid tab
	 */
	public GridTab getGridTab() {
		return gridTab;
	}

	@Override
	public void enableAxesRatio(boolean value) {
		basicTab.enableAxesRatio(value);
	}

	@Override
	public void setMinMaxText(String minX, String maxX, String minY, String maxY, String minZ,
			String maxZ) {
		basicTab.setMinMaxText(minX, maxX, minY, maxY, minZ, maxZ);
	}

	@Override
	public void updateAxes(GColor color, boolean isShown, boolean isBold) {
		basicTab.updateAxes(color);
	}

	@Override
	public void updateBackgroundColor(GColor color) {
		basicTab.updateBackgroundColorButton(color);
	}
	
	@Override
	public void selectTooltipType(int index) {
		basicTab.lbTooltips.setSelectedIndex(index);
	}

	@Override
	public void updateConsProtocolPanel(boolean isVisible) {
		basicTab.updateConsProtocolPanel(isVisible);
	}

	@Override
	public void updateGrid(GColor color, boolean isShown, boolean isBold,
			int gridType) {
		gridTab.update(color, isShown, isBold);
	}

	@Override
	public void showMouseCoords(boolean value) {
		basicTab.showMouseCoords(value);
	}

	@Override
	public void selectAxesStyle(int index) {
	    basicTab.selectAxesStyle(index);
	}

	@Override
	public void updateGridTicks(boolean isAutoGrid, double[] gridTicks,
			int gridType) {
		gridTab.updateTicks(isAutoGrid, gridTicks, gridType);
	}

	@Override
	public void enableLock(boolean value) {
		basicTab.enableLock(value);
	}

	@Override
	public void selectGridStyle(int style) {
		if (gridTab == null) {
			return;
		}
		gridTab.selectGridStyle(style);
	}

	@Override
	public void addAngleOptionItem(String item) {
		// nothing to do here
	}

	@Override
	public void addGridTypeItem(String item) {
		if (gridTab == null) {
			return;
		}
	}

	protected void updateView() {
		view.updateBackground();
		updateGUI();
	}

	@Override
	public void onResize(int height, int width) {
		for (int i = 0; i < tabPanel.getWidgetCount(); i++) {
			EuclidianTab tab = (EuclidianTab) tabPanel.getWidget(i);
			if (tab != null) {
				tab.onResize(height, width);
			}
		}
	}

	/**
	 * select the correct tab
	 * 
	 * @param index
	 *            index
	 */
	public void setSelectedTab(int index) {
		// tabbedPane.setSelectedIndex(index);
		Log.warn("======== OptionsEuclidianW.setSelectedTab() : TODO");
	}

	@Override
	public void updateAxisFontStyle(boolean serif, boolean isBold,
			boolean isItalic) {
		basicTab.cbAxisLabelSerif.setSelected(serif);
		basicTab.cbAxisLabelBold.setSelected(isBold);
		basicTab.cbAxisLabelItalic.setSelected(isItalic);
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		return tabPanel;
	}

	protected DialogManagerW getDialogManager() {
		return (DialogManagerW) app.getDialogManager();
	}

	public void setTextColon(FormLabel cb, String string) {
		cb.setText(loc.getMenu(string) + ":");
	}

	@Override
	public void addRulerTypeItem(String item, BackgroundType type) {
		if (gridTab == null) {
			return;
		}

		gridTab.addRulerTypeItem(item, type);
	}

	@Override
	public void updateRuler(int typeIdx, GColor color, int lineStyle, boolean bold) {
		gridTab.updateRuler(typeIdx, color, lineStyle, bold);
	}

	public EuclidianView getView() {
		return view;
	}
}

