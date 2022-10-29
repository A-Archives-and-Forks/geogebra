package org.geogebra.web.full.gui.dialog.options;

import javax.annotation.Nullable;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.common.properties.impl.algebra.AlgebraDescriptionProperty;
import org.geogebra.common.properties.impl.algebra.SortByProperty;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class OptionsAlgebraW
		implements OptionPanelW, SetLabels, SettingListener {

	private AppW app;
	private final AlgebraTab algebraTab;
	/**
	 * tabs (for now only algebra)
	 */
	protected MultiRowsTabPanel tabPanel;

	protected class AlgebraTab extends FlowPanel {
		private FlowPanel optionsPanel;
		private Label lblShow;
		private ComponentCheckbox showAuxiliaryObjects;
		private CompDropDown sortMode;
		private CompDropDown description;
		private FormLabel lblCoordStyle;
		private CompDropDown coordStyle;
		private FormLabel lblSortMode;
		private FormLabel lblDescriptionMode;

		@Nullable
		private CompDropDown angleUnit;
		@Nullable
		private FormLabel lblAngleUnit;
		@Nullable
		private FlowPanel angleUnitRow;

		/**
		 * algebra tab in algebra settings panel
		 */
		public AlgebraTab() {
			createGUI();
			updateGUI();
			setStyleName("propertiesTab");
			add(optionsPanel);
		}

		private void createGUI() {
			optionsPanel = new FlowPanel();
			lblShow = new Label();
			lblShow.addStyleName("panelTitle");
			showAuxiliaryObjects = new ComponentCheckbox(app.getLocalization(),
					false, "AuxiliaryObjects",
					getApp()::setShowAuxiliaryObjects);

			buildSortByUI();
			buildDescriptionUI();
			buildCoordStyleUI();
			rebuildAngleUnit();

			optionsPanel.add(lblShow);
			optionsPanel.add(LayoutUtilW.panelRowIndent(showAuxiliaryObjects));
			optionsPanel.add(lblSortMode);
			optionsPanel.add(LayoutUtilW.panelRowIndent(sortMode));
			optionsPanel.add(lblDescriptionMode);
			optionsPanel.add(LayoutUtilW.panelRowIndent(description));

			optionsPanel.add(LayoutUtilW.panelRowIndent(lblCoordStyle, coordStyle));
			if (angleUnitRow != null && angleUnit != null) {
				optionsPanel.add(angleUnitRow);
			}
			setLabels();
		}

		private void buildSortByUI() {
			EnumerableProperty sortProperty = new SortByProperty(app.getGuiManager()
					.getAlgebraView(), app.getLocalization());
			sortMode = new CompDropDown(app, sortProperty);
			lblSortMode = new FormLabel().setFor(sortMode);
			lblSortMode.addStyleName("panelTitle");
			lblDescriptionMode = new FormLabel().setFor(sortMode);
			lblDescriptionMode.addStyleName("panelTitle");
		}

		private void buildDescriptionUI() {
			EnumerableProperty descriptionProperty = new AlgebraDescriptionProperty(
					app.getKernel(), app.getLocalization());
			description = new CompDropDown(app, descriptionProperty);
		}

		private void buildCoordStyleUI() {
			EnumerableProperty coordProperty = new CoordinatesProperty(app.getKernel(),
					app.getLocalization());
			coordStyle = new CompDropDown(app, coordProperty);
			lblCoordStyle = new FormLabel(
					getApp().getLocalization().getMenu("Coordinates") + ":")
					.setFor(coordStyle);
			lblCoordStyle.addStyleName("dropDownLabel");
		}

		private void rebuildAngleUnit() {
			if (app.getConfig().isAngleUnitSettingEnabled() && angleUnitRow == null) {
				EnumerableProperty angleProperty = new AngleUnitProperty(app.getKernel(),
						app.getLocalization());
				angleUnit = new CompDropDown(app, angleProperty);
				String labelText = getApp().getLocalization().getMenu("AngleUnit") + ":";
				lblAngleUnit = new FormLabel(labelText).setFor(angleUnit);
				lblAngleUnit.addStyleName("dropDownLabel");
				angleUnitRow = LayoutUtilW.panelRowIndent(lblAngleUnit, angleUnit);
				optionsPanel.add(angleUnitRow);
			} else if (!app.getConfig().isAngleUnitSettingEnabled() && angleUnitRow != null) {
				angleUnitRow.removeFromParent();
				angleUnitRow = null;
				angleUnit = null;
				lblAngleUnit = null;
			}
		}

		/**
		 * update sort mode combo box
		 */
		public void updateSortMode() {
			sortMode.setLabels();
			sortMode.resetToDefault();
		}

		/**
		 * update coord style combo box content
		 */
		public void updateCoordStyle() {
			lblCoordStyle
					.setText(getApp().getLocalization().getMenu("Coordinates")
							+ ":");
			coordStyle.setLabels();
			coordStyle.resetToDefault();
		}

		/**
		 * update angle unit
		 */
		public void updateAngleUnit() {
			if (angleUnit == null || lblAngleUnit == null) {
				return;
			}

			lblAngleUnit
					.setText(getApp().getLocalization().getMenu("AngleUnit") + ":");
			angleUnit.setLabels();
			angleUnit.resetToDefault();
		}

		/**
		 * update description dropdown
		 */
		public void updateDescription() {
			description.setLabels();
			description.resetToDefault();
		}

		/**
		 * update content GUI
		 */
		public void updateGUI() {
			rebuildAngleUnit();
			showAuxiliaryObjects.setSelected(getApp().showAuxiliaryObjects);
			updateSortMode();
			updateDescription();
			updateCoordStyle();
		}

		/**
		 * set text of labels
		 */
		public void setLabels() {
			lblShow.setText(getApp().getLocalization().getMenu("Show"));
			showAuxiliaryObjects.setLabels();
			lblSortMode.setText(getApp().getLocalization().getMenu("SortBy"));
			lblDescriptionMode.setText(
					getApp().getLocalization().getMenu("AlgebraDescriptions"));
		}
	}

	/**
	 * @param app
	 *            application
	 */
	public OptionsAlgebraW(AppW app) {
		this.app = app;
		tabPanel = new MultiRowsTabPanel();
		algebraTab = new AlgebraTab();
		tabPanel.add(algebraTab, app.getLocalization().getMenu("Algebra"));
		tabPanel.selectTab(0);
		app.getSettings().getAlgebra().addListener(this);
	}

	@Override
	public Widget getWrappedPanel() {
		return tabPanel;
    }

	@Override
    public void onResize(int height, int width) {
		// TO DO
    }

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @param app
	 *            application
	 */
	public void setApp(AppW app) {
		this.app = app;
	}

	/**
	 * @return algebra tab
	 */
	public AlgebraTab getAlgebraTab() {
		return algebraTab;
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		updateGUI();
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		return null;
	}

	@Override
	public void setLabels() {
		algebraTab.setLabels();
		algebraTab.updateSortMode();
		algebraTab.updateDescription();
		algebraTab.updateCoordStyle();
		algebraTab.updateAngleUnit();
	}

	@Override
	public void updateGUI() {
		algebraTab.updateGUI();
	}
}
