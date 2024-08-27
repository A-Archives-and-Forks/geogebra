package org.geogebra.web.full.euclidian.quickstylebar.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
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

public class SliderWithProperty extends FlowPanel implements SetLabels {
	private final AppW appW;
	private final RangePropertyCollection<?, ?> property;
	private LineStylePreview preview;
	private SliderPanelW sliderPanel;
	private Label sliderLabel;
	private int lineThickness;
	private int lineType;
	private GColor color = GColor.BLACK;
	private GeoElement geo;

	/**
	 * constructor
	 * @param appW - application
	 * @param geo - geo element
	 */
	public SliderWithProperty(AppW appW, RangePropertyCollection<?, ?> property, GeoElement geo) {
		this.appW = appW;
		this.property = property;
		this.geo = geo;
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
		preview.update(val, geo.getLineType(), geo.getObjectColor());
	}

	private void onInputChange(int val) {
		for (RangeProperty<?> prop : property.getProperties()) {
			if (prop instanceof ThicknessProperty) {
				((ThicknessProperty) prop).setValue(val);
			}
		}
		preview.update(val, geo.getLineType(), geo.getObjectColor());
	}

	private void updatePreview(int lineThickness, int lineType, GColor color) {
		preview.update(lineThickness, lineType, color);
	}

	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
		updatePreview(lineThickness, lineType, color);
	}

	public void setLineType(int lineType) {
		this.lineType = lineType;
		updatePreview(geo.getLineThickness(), lineType, geo.getObjectColor());
	}

	public void setLineColor(GColor color) {
		this.color = color;
		updatePreview(lineThickness, lineType, color);
	}

	@Override
	public void setLabels() {
		sliderLabel.setText(appW.getLocalization().getMenu("Thickness"));
	}
}
