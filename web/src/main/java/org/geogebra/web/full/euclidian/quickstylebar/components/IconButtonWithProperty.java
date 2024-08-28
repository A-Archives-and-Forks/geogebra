package org.geogebra.web.full.euclidian.quickstylebar.components;

import static org.geogebra.web.full.euclidian.quickstylebar.QuickStylebar.POPUP_MENU_DISTANCE;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.euclidian.quickstylebar.PropertyWidgetAdapter;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

public class IconButtonWithProperty extends IconButton {
	private final AppW appW;
	private GPopupPanel propertyPopup;
	private SliderWithProperty lineThicknessSlider;
	private final PropertyWidgetAdapter widgetAdapter;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - svg resource of button
	 * @param properties - array of applicable properties
	 * @param ariaLabel - aria label
	 */
	public IconButtonWithProperty(AppW appW, SVGResource icon, PropertiesArray properties,
			String ariaLabel, GeoElement geo) {
		super(appW, icon, ariaLabel, ariaLabel, () -> {}, null);
		this.appW = appW;
		widgetAdapter = new PropertyWidgetAdapter(appW);
		AriaHelper.setAriaHasPopup(this);

		buildGUI(properties, geo);
		addHandlers();
	}

	private void addHandlers() {
		addFastClickHandler((source) -> {
			if (propertyPopup.isShowing()) {
				propertyPopup.hide();
			} else {
				showPropertyPopup();
			}
			AriaHelper.setAriaExpanded(this, true);
		});

		propertyPopup.addCloseHandler((event) -> {
			setActive(false);
			AriaHelper.setAriaExpanded(this, false);
		});
	}

	private void buildGUI(PropertiesArray properties, GeoElement geo) {
		initPropertyPopup();
		FlowPanel propertyPanel = new FlowPanel();

		for (Property property : properties.getProperties()) {
			if (property instanceof IconsEnumeratedProperty) {
				FlowPanel enumeratedPropertyButtonPanel = widgetAdapter.getIconListPanel(
						(IconsEnumeratedProperty<?>) property, (index) -> {
							lineThicknessSlider.setLineType(index);
							setIcon(PropertiesIconAdapter.getIcon(((IconsEnumeratedProperty<?>)
									property).getValueIcons()[index]));
						});
				propertyPanel.add(enumeratedPropertyButtonPanel);
			}

			if (property instanceof RangeProperty) {
				lineThicknessSlider = widgetAdapter.getSliderWidget(
						(RangePropertyCollection<?, ?>) property, geo, "Thickness");
				propertyPanel.add(lineThicknessSlider);
			}
		}

		propertyPopup.add(propertyPanel);
	}

	private void initPropertyPopup() {
		if (propertyPopup == null) {
			propertyPopup = new GPopupPanel(false, appW.getAppletFrame(), appW);
			propertyPopup.setStyleName("quickStyleBarPopup");
		}
	}

	private void showPropertyPopup() {
		propertyPopup.show();
		propertyPopup.setPopupPosition((int) (getAbsoluteLeft() - appW.getAbsLeft()),
				(int) (getAbsoluteTop() + getOffsetHeight() - appW.getAbsTop())
						+ 2 * POPUP_MENU_DISTANCE);
		appW.registerPopup(propertyPopup);
	}
}
