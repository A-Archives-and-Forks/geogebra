package org.geogebra.web.full.euclidian.quickstylebar.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.web.full.gui.util.LineStylePreview;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class SliderWithProperty extends FlowPanel {
	private final AppW appW;
	private final RangePropertyCollection<?> property;
	private LineStylePreview preview;
	private Label unitLabel;
	private SliderPanelW sliderPanel;
	private int rangeValue;
	private int lineType;
	private GColor color;

	/**
	 * constructor
	 * @param appW - application
	 * @param property - range property
	 * @param lineType - line type
	 * @param rangeValue - range value
	 * @param color - line color
	 */
	public SliderWithProperty(AppW appW, RangePropertyCollection<?> property,
			int lineType, int rangeValue, GColor color) {
		this.appW = appW;
		this.property = property;
		this.rangeValue = rangeValue;
		this.lineType = lineType;
		this.color = color;

		addStyleName("sliderComponent");
		buildGui();
	}

	private void buildGui() {
		String sliderText  = getFirstProperty().getName();
		Label sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu(sliderText), "sliderLabel");

		FlowPanel labelPreviewHolder = new FlowPanel();
		labelPreviewHolder.addStyleName("labelPreviewHolder");
		labelPreviewHolder.add(sliderLabel);
		addPropertyBasedPreview(labelPreviewHolder);

		add(labelPreviewHolder);
		buildSlider();
		add(sliderPanel);
	}

	private void addPropertyBasedPreview(FlowPanel parent) {
		if (getFirstProperty() instanceof ThicknessProperty) {
			addStyleName("withMargin");
			preview = new LineStylePreview(30, 30);
			preview.addStyleName("preview");
			parent.add(preview);
		} else if (getFirstProperty() instanceof ImageOpacityProperty) {
			unitLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(((ImageOpacityProperty)
					getFirstProperty()).getValue() + "%", "sliderLabel");
			parent.add(unitLabel);
		}
	}

	private Property getFirstProperty() {
		return property.getFirstProperty();
	}

	private void buildSlider() {
		sliderPanel = new SliderPanelW(property.getMin().doubleValue(),
				property.getMax().doubleValue(), appW.getKernel(), false);
		sliderPanel.getSlider().addStyleName("slider");
		setInitialValue();
		sliderPanel.getSlider().addValueChangeHandler(event -> {
			onInputChange(sliderPanel.getSlider().getValue().intValue());
			appW.storeUndoInfo();
		});
		sliderPanel.getSlider().addInputHandler(()
				-> onInputChange(sliderPanel.getSlider().getValue().intValue()));
	}

	private void setInitialValue() {
		Integer val = property.getValue();
		sliderPanel.setValue(val.doubleValue());
		updatePreview(val, rangeValue, color);
	}

	private void onInputChange(int val) {
		property.setValue(val);

		setRangeValue(val);
	}

	private void updatePreview(int rangeValue, int lineType, GColor color) {
		if (preview != null) {
			preview.update(rangeValue, lineType, color);
		} else if (unitLabel != null) {
			unitLabel.setText(rangeValue + "%");
		}
	}

	/**
	 * @param rangeValue - line thickness or opacity
	 */
	public void setRangeValue(int rangeValue) {
		this.rangeValue = rangeValue;
		updatePreview(rangeValue, lineType, color);
	}

	/**
	 * @param lineType - line type
	 */
	public void setLineType(int lineType) {
		this.lineType = lineType;
		updatePreview(rangeValue, lineType, color);
	}

	/**
	 * @param color - line color
	 */
	public void setLineColor(GColor color) {
		this.color = color;
		updatePreview(rangeValue, lineType, color);
	}
}
