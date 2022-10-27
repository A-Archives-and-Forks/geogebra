package org.geogebra.web.full.gui.components;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class CompDropDown extends FlowPanel implements SetLabels, IsWidget {
	private final AppW app;
	private Label label;
	private String labelKey;
	private Label selectedOption;
	private boolean isDisabled = false;
	private DropDownComboBoxController controller;
	private boolean fullWidth = false;

	/**
	 * Material drop-down component
	 * @param app - see {@link AppW}
	 * @param label - label of drop-down
	 * @param items - popup elements
	 */
	public CompDropDown(AppW app, String label, List<String> items) {
		this.app = app;
		labelKey = label;
		addStyleName("dropDown");

		buildGUI(label);
		addClickHandler();

		initController(items);
	}

	/**
	 * @param app - - see {@link AppW}
	 * @param label - label of drop-down
	 * @param items - popup elements
	 * @param defaultIdx - default index
	 */
	public CompDropDown(AppW app, String label, List<String> items, int defaultIdx) {
		this(app, label, items);
		controller.setSelectedOption(defaultIdx);
		updateSelectionText(controller.getSelectedText());
	}

	/**
	 * @param app - - see {@link AppW}
	 * @param label - label of drop-down
	 * @param property - property
	 */
	public CompDropDown(AppW app, String label, EnumerableProperty property) {
		this(app, label, Arrays.asList(property.getValues()));
		controller.setProperty(property);
		if (property.getIndex() > -1) {
			controller.setSelectedOption(property.getIndex());
		}
		updateSelectionText(controller.getSelectedText());
	}

	private void initController(List<String> items) {
		controller = new DropDownComboBoxController(app, this, selectedOption, items, null);
		controller.setChangeHandler(() -> updateSelectionText(controller.getSelectedText()));
		updateSelectionText(controller.getSelectedText());
	}

	private void buildGUI(String labelStr) {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");

		if (labelStr != null && !labelStr.isEmpty()) {
			label = new Label(app.getLocalization().getMenu(labelStr));
			label.addStyleName("label");
			optionHolder.add(label);
		} else {
			optionHolder.addStyleName("noLabel");
		}

		selectedOption = new Label();
		selectedOption.addStyleName("selectedOption");
		optionHolder.add(selectedOption);
		add(optionHolder);

		SimplePanel arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE
				.arrow_drop_down().getSVG());
		add(arrowIcon);
	}

	private void addClickHandler() {
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!isDisabled) {
					controller.toggleAsDropDown(fullWidth);
				}
			}
		});
	}

	public int getSelectedIndex() {
		return controller.getSelectedIndex();
	}

	/**
	 * Disable drop-down component
	 * @param disabled - true, if drop-down should be disabled
	 */
	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
		Dom.toggleClass(this, "disabled", disabled);
	}

	@Override
	public void setLabels() {
		if (label != null) {
			label.setText(app.getLocalization().getMenu(labelKey));
		}
		controller.setLabels();
		updateSelectionText(controller.getSelectedText());
	}

	private void updateSelectionText(String text) {
		selectedOption.setText(text);
	}

	public void setChangeHandler(Runnable changeHandler) {
		controller.setChangeHandler(changeHandler);
	}

	/**
	 * reset dropdown to default
	 */
	public void resetToDefault() {
		controller.resetToDefault();
		updateSelectionText(controller.getSelectedText());
	}

	public void setFullWidth(boolean isFullWidth) {
		fullWidth = isFullWidth;
	}

	public boolean isFullWidth() {
		return fullWidth;
	}
}
