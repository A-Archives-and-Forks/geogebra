package org.geogebra.web.full.euclidian;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.collections.FlagListPropertyCollection;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.javax.swing.GCheckMarkLabel;
import org.geogebra.web.full.javax.swing.GCheckMarkPanel;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.FlowPanel;

public class LabelSettingsPanel extends FlowPanel
		implements CloseHandler<GPopupPanel>, SetLabels {
	private final AppW appW;
	private final StringPropertyCollection<?> nameProperty;
	private FlagListPropertyCollection<?> labelStyleProperty;
	private final List<GeoElement> geos;

	private ComponentInputField tfName;
	private final List<GCheckMarkLabel> checkmarks = new ArrayList<>();

	/**
	 * Constructor
	 * @param appW - application
	 * @param nameProperty - name property
	 */
	public LabelSettingsPanel(AppW appW, StringPropertyCollection<?> nameProperty,
			List<GeoElement> geos) {
		super();
		this.appW = appW;
		this.nameProperty = nameProperty;
		this.geos = geos;

		createDialog();
	}

	/**
	 * @param labelStyleProperty - label style property
	 */
	public void setLabelStyleProperty(FlagListPropertyCollection<?> labelStyleProperty) {
		this.labelStyleProperty = labelStyleProperty;
		Command nameValueCmd = this::applyCheckboxes;
		for (String label: labelStyleProperty.getFlagNames()) {
			checkmarks.add(new GCheckMarkLabel(label, MaterialDesignResources.INSTANCE
					.check_black(), true, nameValueCmd));
		}
		checkmarks.forEach(this::add);
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
		boolean isSelectionMode = appW.getActiveEuclidianView().getEuclidianController()
				.getMode() == EuclidianConstants.MODE_SELECT;
		if (!isSelectionMode) {
			add(tfName);
		}

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
		if (labelStyleProperty != null) {
			List<String> flagNames = labelStyleProperty.getFlagNames();
			for (int i = 0; i < checkmarks.size(); i++) {
				checkmarks.get(i).setText(flagNames.get(i));
			}
		}
	}

	/**
	 * Apply settings to selected geo(s).
	 */
	void applyCheckboxes() {
		List<Boolean> values = checkmarks.stream().map(GCheckMarkPanel::isChecked).collect(
				Collectors.toList());
		setLabelStyle(values);
	}

	private void setLabelStyle(List<Boolean> values) {
		if (labelStyleProperty != null) {
			labelStyleProperty.setValue(values);
			updateUI();
		}
	}

	private void updateUI() {
		if (tfName != null) {
			tfName.setVisible(geos.size() == 1);
		}

		if (labelStyleProperty != null) {
			List<Boolean> labelStyle = labelStyleProperty.getValue();
			for (int i = 0; i < labelStyle.size(); i++) {
				checkmarks.get(i).setChecked(labelStyle.get(i));
			}
		}
	}

	private void init() {
		tfName.setInputText(nameProperty.getValue());
		tfName.focusDeferred();
	}

}
