package org.geogebra.web.full.gui.components;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

public class ComponentDropDown extends FlowPanel implements SetLabels {
	private final AppW app;
	private Label label;
	private final String labelKey;
	private Label selectedOption;
	private SimplePanel arrowIcon;
	private boolean isDisabled = false;
	private DropDownComboBoxController controller;
	private boolean fullWidth = false;

	/**
	 * Material drop-down component
	 * @param app see {@link AppW}
	 * @param label of drop-down
	 * @param items popup elements
	 */
	private ComponentDropDown(AppW app, String label, List<String> items) {
		this.app = app;
		labelKey = label;
		addStyleName("dropDown");

		buildGUI(label);
		addClickHandler();

		initController(items);
	}

	/**
	 * @param app see {@link AppW}
	 * @param label label of drop-down
	 * @param items popup elements
	 * @param defaultIdx selected index by default
	 */
	public ComponentDropDown(AppW app, String label, List<String> items, int defaultIdx) {
		this(app, label, items);
		controller.setSelectedOption(defaultIdx);
		updateSelectionText();
	}

	/**
	 * @param app see {@link AppW}
	 * @param label label of drop-down
	 * @param property property
	 */
	public ComponentDropDown(AppW app, String label, NamedEnumeratedProperty<?> property) {
		this(app, label, Arrays.asList(property.getValueNames()));
		controller.setProperty(property);
		if (property.getIndex() > -1) {
			controller.setSelectedOption(property.getIndex());
		}
		updateSelectionText();
	}

	/**
	 * @param app see {@link AppW}
	 * @param property property
	 */
	public ComponentDropDown(AppW app, NamedEnumeratedProperty<?> property) {
		this(app, null, property);
	}

	private void initController(List<String> items) {
		controller = new DropDownComboBoxController(app, this, items, () -> {
			removeStyleName("active");
			arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE
					.arrow_drop_down().getSVG());
		});
		controller.addChangeHandler(this::updateSelectionText);
		updateSelectionText();
	}

	private void buildGUI(String labelStr) {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");

		if (labelStr != null && !labelStr.isEmpty()) {
			label = BaseWidgetFactory.INSTANCE.newSecondaryText(
					app.getLocalization().getMenu(labelStr), "label");
			optionHolder.add(label);
		}

		selectedOption = BaseWidgetFactory.INSTANCE.newPrimaryText("", "selectedOption");
		optionHolder.add(selectedOption);
		add(optionHolder);

		arrowIcon = new SimplePanel();
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
					updateIcon();
				}
			}
		});
	}

	private void updateIcon() {
		String svg = controller.isOpened() ? MaterialDesignResources.INSTANCE
				.arrow_drop_up().getSVG() : MaterialDesignResources.INSTANCE
				.arrow_drop_down().getSVG();
		arrowIcon.getElement().setInnerHTML(svg);
	}

	public int getSelectedIndex() {
		return controller.getSelectedIndex();
	}

	/**
	 * Disable drop-down component
	 * @param disabled true, if drop-down should be disabled
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
		updateSelectionText();
	}

	/**
	 * This should be called automatically when an item is selected (by user or programmatically)
	 */
	private void updateSelectionText() {
		selectedOption.setText(controller.getSelectedText());
	}

	/**
	 * Add a change handler.
	 * @param changeHandler change handler
	 */
	public void addChangeHandler(Runnable changeHandler) {
		controller.addChangeHandler(changeHandler);
	}

	/**
	 * reset dropdown to the model (property) value
	 */
	public void resetFromModel() {
		controller.resetFromModel();
		updateSelectionText();
	}

	public void setFullWidth(boolean isFullWidth) {
		fullWidth = isFullWidth;
	}

	/**
	 * @param dropdownIndex selected index
	 */
	public void setSelectedIndex(int dropdownIndex) {
		controller.setSelectedOption(dropdownIndex);
		updateSelectionText();
	}

	/**
	 * @return text of selected item
	 */
	public String getSelectedText() {
		return controller.getSelectedText();
	}

	/**
	 * @param property update property
	 */
	public void setProperty(NamedEnumeratedProperty<?> property) {
		controller.setProperty(property);
	}
}
