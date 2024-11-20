package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.TextFontSize;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.objects.TextFontSizeProperty;
import org.geogebra.web.full.euclidian.quickstylebar.components.BorderThicknessPanel;
import org.geogebra.web.full.euclidian.quickstylebar.components.SliderWithProperty;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

public class PropertyWidgetAdapter {
	private final AppW appW;
	private final boolean closePopupOnAction;
	private List<IconButton> enumeratedPropertyButtons;

	/**
	 * @param appW - application
	 * @param closePopupOnAction - weather popup should be closed on element click
	 */
	public PropertyWidgetAdapter(AppW appW, boolean closePopupOnAction) {
		this.appW = appW;
		this.closePopupOnAction = closePopupOnAction;
	}

	/**
	 * @param iconProperty - property
	 * @param uiUpdater - update dependent ui
	 * @return panel holding list of icon buttons based on property
	 */
	public FlowPanel getIconListPanel(IconsEnumeratedProperty<?> iconProperty,
			Consumer<Integer> uiUpdater) {
		enumeratedPropertyButtons = new ArrayList<>();
		FlowPanel buttonListComponent = new FlowPanel();
		buttonListComponent.addStyleName("buttonList");

		PropertyResource[] icons = iconProperty.getValueIcons();
		for (int i = 0; i < icons.length; i++) {
			int finalI = i;
			IconButton enumeratedPropertyIconButton = new IconButton(appW, null,
					PropertiesIconAdapter.getIcon(icons[i]), null);
			enumeratedPropertyIconButton.addFastClickHandler(source -> {
				iconProperty.setIndex(finalI);
				setIconButtonActive(enumeratedPropertyIconButton);
				if (uiUpdater != null) {
					uiUpdater.accept(finalI);
				}
				if (closePopupOnAction) {
					appW.closePopups();
				}
			});

			enumeratedPropertyIconButton.setActive(finalI == iconProperty.getIndex());
			buttonListComponent.add(enumeratedPropertyIconButton);
			enumeratedPropertyButtons.add(enumeratedPropertyIconButton);
		}

		return buttonListComponent;
	}

	private void setIconButtonActive(IconButton enumeratedPropertyIconButton) {
		enumeratedPropertyButtons.forEach(iconButton -> iconButton.setActive(false));
		enumeratedPropertyIconButton.setActive(true);
	}

	/**
	 * @param property - cell border thickness property
	 * @return panel for line thickness ui
	 */
	public FlowPanel getBorderThicknessWidget(RangePropertyCollection<?> property) {
		return new BorderThicknessPanel(property, appW);
	}

	/**
	 * @param property - range property
	 * @param geo - geo element
	 * @return slider based on range property
	 */
	public SliderWithProperty getSliderWidget(RangePropertyCollection<?> property,
			GeoElement geo) {
		return new SliderWithProperty(appW, property, geo.getLineType(),
				geo.getObjectColor());
	}

	/**
	 * @param property - text font size property
	 * @return menu based on text font size property
	 */
	public GPopupMenuW getMenuWidget(TextFontSizeProperty property) {
		GPopupMenuW fontSizeMenu = new GPopupMenuW(appW);
		int selectedFontIdx = property.getValue().ordinal();
		for (int i = 0; i < property.getValueNames().length; i++) {
			String menuItemText = property.getValueNames()[i];
			int finalI = i;
			AriaMenuItem item = new AriaMenuItem(menuItemText, null,
					() -> {
				property.setValue(TextFontSize.values()[finalI]);
				appW.closePopups();
					});
			if (selectedFontIdx == finalI) {
				item.addStyleName("selectedItem");
			}
			fontSizeMenu.addItem(item);
		}
		fontSizeMenu.setVisible(true);
		return fontSizeMenu;
	}
}
