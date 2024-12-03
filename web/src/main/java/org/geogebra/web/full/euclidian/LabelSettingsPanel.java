package org.geogebra.web.full.euclidian;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.common.properties.impl.collections.ValuedPropertyCollection;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.javax.swing.GCheckMarkLabel;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.FlowPanel;

public class LabelSettingsPanel extends FlowPanel
		implements CloseHandler<GPopupPanel>, SetLabels {
	private final AppW appW;
	private final StringPropertyCollection<?> nameProperty;
	private ValuedPropertyCollection<?> labelStyleProperty;
	private final List<GeoElement> geos;

	private final LocalizationW loc;
	private ComponentInputField tfName;
	private GCheckMarkLabel cmName;
	private GCheckMarkLabel cmValue;

	/**
	 * Constructor
	 * @param appW - application
	 * @param nameProperty - name property
	 */
	public LabelSettingsPanel(AppW appW, StringPropertyCollection<?> nameProperty,
			List<GeoElement> geos) {
		super();
		this.appW = appW;
		loc = appW.getLocalization();
		this.nameProperty = nameProperty;
		this.geos = geos;

		createDialog();
	}

	/**
	 * @param labelStyleProperty - label style property
	 */
	public void setLabelStyleProperty(ValuedPropertyCollection<?> labelStyleProperty) {
		this.labelStyleProperty = labelStyleProperty;
		updateUI();
	}

	private void createDialog() {
		tfName = new ComponentInputField(appW, null, "Label", null, nameProperty.getValue(),
				-1, null, false);
		tfName.getTextField().getTextComponent().setAutoComplete(false);
		tfName.getTextField().getTextComponent().enableGGBKeyboard();

		tfName.getTextField().getTextComponent().addBlurHandler(event -> onEnter());
		tfName.getTextField().getTextComponent().addKeyHandler(e -> {
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

		boolean isSelectionMode = appW.getActiveEuclidianView().getEuclidianController()
				.getMode() == EuclidianConstants.MODE_SELECT;
		if (!isSelectionMode) {
			add(tfName);
		}
		add(cmName);
		add(cmValue);

		init();
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
		nameProperty.setValue(tfName.getText());
	}

	@Override
	public void setLabels() {
		tfName.setLabels();
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
			mode = isForceCaption() ? GeoElementND.LABEL_CAPTION
					: GeoElementND.LABEL_NAME;
		} else if (name && value) {
			mode = isForceCaption() ? GeoElementND.LABEL_CAPTION_VALUE
					: GeoElementND.LABEL_NAME_VALUE;
		} else if (!name && value) {
			mode = GeoElementND.LABEL_VALUE;
		}

		setLabelStyle(mode);
	}

	private void setLabelStyle(int mode) {
		if (labelStyleProperty != null) {
			labelStyleProperty.setValue(mode);
			updateUI();
		}
	}

	private void updateUI() {
		if (tfName != null) {
			tfName.setVisible(geos.size() == 1);
		}

		if (!geos.get(0).isLabelVisible()) {
			cmName.setChecked(false);
			cmValue.setChecked(false);
			return;
		}

		if (labelStyleProperty != null) {
			int labelStyle = labelStyleProperty.getValue();
			cmName.setChecked(labelStyle == GeoElementND.LABEL_NAME
					|| labelStyle == GeoElementND.LABEL_CAPTION_VALUE
					|| labelStyle == GeoElementND.LABEL_NAME_VALUE
					|| labelStyle == GeoElementND.LABEL_CAPTION);
			cmValue.setChecked(labelStyle == GeoElementND.LABEL_VALUE
							|| labelStyle == GeoElementND.LABEL_CAPTION_VALUE
							|| labelStyle == GeoElementND.LABEL_NAME_VALUE);
		}
	}

	private void init() {
		tfName.setInputText(nameProperty.getValue());
		tfName.focusDeferred();
	}

	private boolean isForceCaption() {
		return !geos.get(0)
				.getLabel(StringTemplate.defaultTemplate)
				.equals(geos.get(0).getCaption(StringTemplate.defaultTemplate));
	}
}
