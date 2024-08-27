package org.geogebra.web.full.euclidian.quickstylebar.components;

import static org.geogebra.web.full.euclidian.quickstylebar.QuickStylebar.POPUP_MENU_DISTANCE;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

public class IconButtonWithProperties extends IconButton {
	private final AppW appW;
	private GPopupPanel propertyPopup;
	private final ArrayList<IconButton> buttons = new ArrayList<>();
	private SliderWithProperty lineThicknessSlider;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - svg resource of button
	 * @param properties - array of applicable properties
	 * @param ariaLabel - aria label
	 */
	public IconButtonWithProperties(AppW appW, SVGResource icon, PropertiesArray properties,
			String ariaLabel, GeoElement geo) {
		super(appW, icon, ariaLabel, ariaLabel, () -> {}, null);
		this.appW = appW;
		AriaHelper.setAriaHasPopup(this);

		buildGUI(properties, geo);

		addFastClickHandler((source) -> {
			appW.closePopups();
			propertyPopup.show();
			propertyPopup.setPopupPosition((int) (getAbsoluteLeft() - appW.getAbsLeft()),
					(int) (getAbsoluteTop() + getOffsetHeight() - appW.getAbsTop())
							+ 2 * POPUP_MENU_DISTANCE);
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
				processIconEnumeratedProperty((IconsEnumeratedProperty<?>) property,
						propertyPanel);
			}
			if (property instanceof RangeProperty) {
				lineThicknessSlider = new SliderWithProperty(appW,
						(RangePropertyCollection<?, ?>) property, geo);
				propertyPanel.add(lineThicknessSlider);
			}
		}

		propertyPopup.add(propertyPanel);
	}

	private void processIconEnumeratedProperty(IconsEnumeratedProperty<?> iconProperty,
			FlowPanel parent) {
		FlowPanel buttonListComponent = new FlowPanel();
		buttonListComponent.addStyleName("buttonList");

		PropertyResource[] icons = iconProperty.getValueIcons();
		for (int i = 0; i < icons.length; i++) {
			int finalI = i;
			IconButton button = new IconButton(appW, null,
					PropertiesIconAdapter.getIcon(icons[i]), null);
			button.addFastClickHandler(source -> {
				iconProperty.setIndex(finalI);
				buttons.forEach(iconButton -> iconButton.setActive(false));
				button.setActive(true);
				setIcon(button.getIcon());
				lineThicknessSlider.setLineType(finalI);
				appW.storeUndoInfo();
			});
			button.setActive(finalI == iconProperty.getIndex());
			buttonListComponent.add(button);
			buttons.add(button);
		}

		parent.add(buttonListComponent);
	}

	private void initPropertyPopup() {
		if (propertyPopup == null) {
			propertyPopup = new GPopupPanel(true, appW.getAppletFrame(), appW);
			propertyPopup.setStyleName("quickStyleBarPopup");
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
		lineThicknessSlider.setLabels();
	}
}
