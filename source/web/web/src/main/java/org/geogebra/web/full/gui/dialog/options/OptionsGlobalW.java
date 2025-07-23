package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LabelingProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.main.GeoGebraPreferencesW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * global settings tab
 */
public class OptionsGlobalW implements OptionPanelW, SetLabels {

	/**
	 * application
	 */
	AppW app;
	private final GlobalTab globalTab;
	/**
	 * tabs (for now only global)
	 */
	protected MultiRowsTabPanel tabPanel;

	/**
	 * tab for global settings
	 *
	 * @author csilla
	 *
	 */
	protected class GlobalTab extends FlowPanel implements SetLabels, PropertyValueObserver {
		private FlowPanel optionsPanel;
		private FormLabel lblRounding;
		private ComponentDropDown roundingDropDown;
		private FormLabel lblLabeling;
		private ComponentDropDown labelingDropDown;
		private FormLabel lblFontSize;
		private ComponentDropDown fontSizeDropDown;
		private FormLabel lblLanguage;
		private ComponentDropDown languageDropDown;
		private StandardButton saveSettingsBtn;
		private StandardButton restoreSettingsBtn;
		private FlowPanel saveRestoreRow;
		private final ExamController examController = GlobalScope.examController;

		/**
		 * constructor
		 */
		protected GlobalTab() {
			createGUI();
			updateGUI();
			setStyleName("propertiesTab");
			add(optionsPanel);
		}

		private void createGUI() {
			optionsPanel = new FlowPanel();
			addLabelsWithComboBox();
			addSaveSettingBtn();
			addRestoreSettingsBtn();
		}

		private void addLabelsWithComboBox() {
			addLanguageItem();
			addRoundingItem();
			addLabelingItem();
			addFontItem();
		}

		private void addRoundingItem() {
			NamedEnumeratedProperty<?> roundingProp =
					new RoundingIndexProperty(app, app.getLocalization());
			roundingDropDown = new ComponentDropDown(app, roundingProp);
			lblRounding = new FormLabel(
					app.getLocalization().getMenu("Rounding") + ":")
							.setFor(roundingDropDown);
			lblRounding.addStyleName("dropDownLabel");
			optionsPanel
					.add(LayoutUtilW.panelRow(lblRounding, roundingDropDown));
		}

		private void addLabelingItem() {
			LabelingProperty property;
			if (app.isUnbundledOrWhiteboard()) {
				property = new LabelingProperty(app.getLocalization(),
						app.getSettings().getLabelSettings());
			} else {
				property = new LabelingProperty(app.getLocalization(),
						app.getSettings().getLabelSettings(), LabelVisibility.Automatic,
						LabelVisibility.AlwaysOn, LabelVisibility.AlwaysOff,
						LabelVisibility.PointsOnly);
			}
			labelingDropDown = new ComponentDropDown(app, property);
			lblLabeling = new FormLabel(
					app.getLocalization().getMenu("Labeling") + ":")
							.setFor(labelingDropDown);
			lblLabeling.addStyleName("dropDownLabel");
			optionsPanel
					.add(LayoutUtilW.panelRow(lblLabeling, labelingDropDown));
		}

		private void addFontItem() {
			NamedEnumeratedProperty<?> fontSizeProperty = new FontSizeProperty(
					app.getLocalization(),
					app.getSettings().getFontSettings(),
					app.getFontSettingsUpdater());
			fontSizeDropDown = new ComponentDropDown(app, fontSizeProperty);
			lblFontSize = new FormLabel(
					app.getLocalization().getMenu("FontSize") + ":")
							.setFor(fontSizeDropDown);
			lblFontSize.addStyleName("dropDownLabel");
			optionsPanel
					.add(LayoutUtilW.panelRow(lblFontSize, fontSizeDropDown));
		}

		private void addLanguageItem() {
			NamedEnumeratedProperty<?> languageProperty = new LanguageProperty(app,
					app.getLocalization());
			languageProperty.addValueObserver(this);
			//GlobalScope.propertiesRegistry.register(languageProperty);
			languageDropDown = new ComponentDropDown(app, languageProperty);
			lblLanguage = new FormLabel(
					app.getLocalization().getMenu("Language") + ":")
							.setFor(languageDropDown);
			lblLanguage.addStyleName("dropDownLabel");
			optionsPanel
					.add(LayoutUtilW.panelRow(lblLanguage, languageDropDown));
		}

		private void storeLanguage(String lang) {
			if (app.getLoginOperation() != null) {
				app.getLoginOperation().setUserLanguage(lang);
			}
			app.getLAF().storeLanguage(lang);
		}

		private void addRestoreSettingsBtn() {
			restoreSettingsBtn = new StandardButton(
					app.getLocalization().getMenu("RestoreSettings"));
			restoreSettingsBtn.setStyleName("settingsBtn");
			restoreSettingsBtn.addFastClickHandler(source -> {
				app.restoreSettings();
				fontSizeDropDown.resetFromModel();
				labelingDropDown.resetFromModel();
				roundingDropDown.resetFromModel();
			});
			saveRestoreRow = LayoutUtilW
					.panelRow(saveSettingsBtn, restoreSettingsBtn);
			saveRestoreRow.setVisible(examController.isIdle());
			optionsPanel.add(saveRestoreRow);
		}

		private void addSaveSettingBtn() {
			saveSettingsBtn = new StandardButton(
					app.getLocalization().getMenu("Settings.Save"));
			saveSettingsBtn.setStyleName("settingsBtn");
			saveSettingsBtn.addFastClickHandler(
					source -> GeoGebraPreferencesW.saveXMLPreferences(app));
			optionsPanel.add(saveSettingsBtn);
		}

		/**
		 * update gui
		 */
		public void updateGUI() {
			labelingDropDown.resetFromModel();
			fontSizeDropDown.resetFromModel();
			languageDropDown.resetFromModel();
			lblLanguage.setVisible(examController.isIdle());
			languageDropDown.setVisible(examController.isIdle());
			saveRestoreRow.setVisible(examController.isIdle());
		}

		/**
		 * @param height
		 *            - height
		 * @param width
		 *            - width
		 */
		public void onResize(int height, int width) {
			this.setHeight(height + "px");
			this.setWidth(width + "px");
		}

		@Override
		public void setLabels() {
			lblRounding
					.setText(app.getLocalization().getMenu("Rounding") + ":");
			roundingDropDown.setLabels();
			lblLabeling.setText(app.getLocalization().getMenu("Labeling") + ":");
			labelingDropDown.setLabels();
			lblFontSize.setText(app.getLocalization().getMenu("FontSize") + ":");
			fontSizeDropDown.setLabels();
			lblLanguage
					.setText(app.getLocalization().getMenu("Language") + ":");
			languageDropDown.setLabels();
			saveSettingsBtn
					.setText(app.getLocalization().getMenu("Settings.Save"));
			restoreSettingsBtn
					.setText(app.getLocalization().getMenu("RestoreSettings"));
		}

		// PropertyValueObserver

		@Override
		public void onDidSetValue(ValuedProperty property) {
			if (property instanceof LanguageProperty) {
				storeLanguage(((LanguageProperty) property).getValue());
			}
		}
	}

	/**
	 * @param app
	 *            - application
	 */
	public OptionsGlobalW(AppW app) {
		this.app = app;
		tabPanel = new MultiRowsTabPanel();
		globalTab = new GlobalTab();
		tabPanel.add(globalTab, app.getLocalization().getMenu("Global"));
		updateGUI();
		tabPanel.selectTab(0);
		app.setDefaultCursor();
	}

	@Override
	public void updateGUI() {
		globalTab.updateGUI();
	}

	@Override
	public Widget getWrappedPanel() {
		return tabPanel;
	}

	@Override
	public void onResize(int height, int width) {
		globalTab.onResize(height, width);
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		return null;
	}

	@Override
	public void setLabels() {
		tabPanel.getTabBar().setTabText(0,
				app.getLocalization().getMenu("Global"));
		globalTab.setLabels();
	}
}
