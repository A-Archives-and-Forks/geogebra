package org.geogebra.web.full.euclidian.quickstylebar.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
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
	private Label sliderLabel;

	/**
	 * constructor
	 * @param appW - application
	 */
	public SliderWithProperty(AppW appW, RangePropertyCollection<?, ?> property) {
		this.appW = appW;
		this.property = property;
		addStyleName("sliderComponent");
		buildGui();
	}

	private void buildGui() {
		sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu("Thickness"), "sliderLabel");
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
		sliderPanel.getSlider().addInputHandler(() ->
				onInputChange(sliderPanel.getValue().intValue()));
	}

	private void setInitialValue() {
		Integer val = ((ThicknessProperty) property.getProperties()[0]).getValue();
		sliderPanel.setValue(val.doubleValue());
		preview.update(val, 0, GColor.BLACK);
	}

	private void onInputChange(int val) {
		for (RangeProperty<?> prop : property.getProperties()) {
			if (prop instanceof ThicknessProperty) {
				((ThicknessProperty) prop).setValue(val);
			}
		}
		preview.update(val, 0, GColor.BLACK);
	}
}
