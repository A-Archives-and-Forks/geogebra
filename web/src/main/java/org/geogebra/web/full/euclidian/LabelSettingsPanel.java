package org.geogebra.web.full.euclidian;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.NameValueModel;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.full.javax.swing.GCheckMarkLabel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class LabelSettingsPanel extends FlowPanel
		implements CloseHandler<GPopupPanel>, SetLabels, ShowLabelModel.IShowLabelListener {
	private final AppW appW;
	private final StringPropertyCollection<?> nameProperty;

	private final LocalizationW loc;
	private Label lblName;
	private AutoCompleteTextFieldW tfName;

	private GCheckMarkLabel cmName;
	private GCheckMarkLabel cmValue;
	private final NameValueModel model;
	private VirtualKeyboardGUI kbd;
	private FlowPanel namePanel;

	/**
	 * Constructor
	 * @param appW - application
	 * @param nameProperty - name property
	 */
	public LabelSettingsPanel(AppW appW, StringPropertyCollection<?> nameProperty) {
		super();
		this.appW = appW;
		loc = appW.getLocalization();
		this.nameProperty = nameProperty;
		createPopup();

		model = new NameValueModel(appW, null);
		init();
	}

	private void createPopup() {
		//getMyPopup().addCloseHandler(this);
		createDialog();
	}

	private void createDialog() {
		lblName = new Label();
		tfName = InputPanelW.newTextComponent(appW);
		tfName.setAutoComplete(false);
		tfName.enableGGBKeyboard();

		tfName.addBlurHandler(event -> {
			onEnter();
		});

		tfName.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				onEnter();
			}
		});

		Command nameValueCmd = this::applyCheckboxes;
		cmName = new GCheckMarkLabel("", MaterialDesignResources.INSTANCE
				.check_black(), true, nameValueCmd);

		cmValue = new GCheckMarkLabel("",
				MaterialDesignResources.INSTANCE.check_black(),
				true, nameValueCmd);

		boolean isSelectionMode = appW.getActiveEuclidianView()
				.getEuclidianController()
				.getMode() == EuclidianConstants.MODE_SELECT;
		if (!isSelectionMode) {
			namePanel = LayoutUtilW.panelRow(lblName, tfName);
			add(namePanel);
		}
		add(cmName);
		add(cmValue);
		//main.setStyleName("labelPopupPanel");
		kbd = ((AppWFull) appW).getKeyboardManager().getOnScreenKeyboard();
		//getMyPopup().addAutoHidePartner(kbd.getElement());
		setLabels();
	}

	/**
	 * Submit the change
	 */
	protected void onEnter() {
		nameProperty.setValue(tfName.getText());
		applyCheckboxes();
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		if (model.noLabelUpdateNeeded(tfName.getText())) {
			return;
		}

		nameProperty.setValue(tfName.getText());
	}

	@Override
	public void setLabels() {
		lblName.setText(loc.getMenu("Label") + ":");
		cmName.setText(loc.getMenu("ShowLabel"));
		cmValue.setText(loc.getMenu("ShowValue"));
	}

	/**
	 * Apply settings to selected geo(s).
	 */
	void applyCheckboxes() {
		boolean name = cmName.isChecked();
		boolean value = cmValue.isChecked();
		int mode = -1;
		if (name && !value) {
			mode = model.isForceCaption() ? GeoElementND.LABEL_CAPTION
					: GeoElementND.LABEL_NAME;
		} else if (name && value) {
			mode = model.isForceCaption() ? GeoElementND.LABEL_CAPTION_VALUE
					: GeoElementND.LABEL_NAME_VALUE;
		} else if (!name && value) {
			mode = GeoElementND.LABEL_VALUE;
		}
		// !name && !value: hide, nothing to do.

		// TODO PROPERTY APPLY model.applyModeChanges(mode, mode != -1);
	}

	@Override
	public Object updatePanel(Object[] geos2) {
		return null;
	}

	@Override
	public void update(boolean isEqualVal, boolean isEqualMode, int mode) {
		if (namePanel != null) {
			namePanel.setVisible(model.getGeosLength() == 1);
		}
		if (!model.isLabelVisible()) {
			cmName.setChecked(false);
			cmValue.setChecked(false);
			return;
		}
		cmName.setChecked(
				isEqualVal
						&& (mode == GeoElementND.LABEL_NAME
								|| mode == GeoElementND.LABEL_CAPTION_VALUE
								|| mode == GeoElementND.LABEL_NAME_VALUE
								|| mode == GeoElementND.LABEL_CAPTION));
		cmValue.setChecked(
				isEqualMode
						&& (mode == GeoElementND.LABEL_VALUE
								|| mode == GeoElementND.LABEL_CAPTION_VALUE
								|| mode == GeoElementND.LABEL_NAME_VALUE));
	}

	private void init() {
		kbd.selectTab(KeyboardType.ABC);
		tfName.setText(nameProperty.getValue());
		tfName.requestFocus();
	}
}
