package org.geogebra.web.full.euclidian.quickstylebar.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.objects.NotesThicknessProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.web.full.gui.util.LineStylePreview;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class SliderWithProperty extends FlowPanel {
	private final AppW appW;
	private final RangePropertyCollection<?, ?> property;
	private LineStylePreview preview;
	private SliderPanelW sliderPanel;
	private int lineThickness;
	private int lineType;
	private GColor color;

	/**
	 * constructor
	 * @param appW - application
	 * @param property - range property
	 * @param sliderLabel - label of slider
	 * @param lineType - line type
	 * @param lineThickness - line thickness
	 * @param color - line color
	 */
	public SliderWithProperty(AppW appW, RangePropertyCollection<?, ?> property,
			String sliderLabel, int lineType, int lineThickness, GColor color) {
		this.appW = appW;
		this.property = property;
		this.lineThickness = lineThickness;
		this.lineType = lineType;
		this.color = color;

		addStyleName("sliderComponent");
		buildGui(sliderLabel);
	}

	private void buildGui(String sliderText) {
		Label sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu(sliderText), "sliderLabel");
		preview = new LineStylePreview(30, 30);
		preview.addStyleName("preview");

		FlowPanel labelPreviewHolder = new FlowPanel();
		labelPreviewHolder.addStyleName("labelPreviewHolder");
		labelPreviewHolder.add(sliderLabel);
		labelPreviewHolder.add(preview);

		add(labelPreviewHolder);
		buildSlider();
		add(sliderPanel);
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
		Integer val = ((NotesThicknessProperty) property.getProperties()[0]).getValue();
		sliderPanel.setValue(val.doubleValue());
		preview.update(val, lineThickness, color);
	}

	private void onInputChange(int val) {
		for (RangeProperty<?> prop : property.getProperties()) {
			if (prop instanceof ThicknessProperty) {
				((ThicknessProperty) prop).setValue(val);
			}
		}

		setLineThickness(val);
	}

	private void updatePreview(int lineThickness, int lineType, GColor color) {
		preview.update(lineThickness, lineType, color);
	}

	/**
	 * @param lineThickness - line thickness
	 */
	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
		updatePreview(lineThickness, lineType, color);
	}

	/**
	 * @param lineType - line type
	 */
	public void setLineType(int lineType) {
		this.lineType = lineType;
		updatePreview(lineThickness, lineType, color);
	}

	/**
	 * @param color - line color
	 */
	public void setLineColor(GColor color) {
		this.color = color;
		updatePreview(lineThickness, lineType, color);
	}
}
