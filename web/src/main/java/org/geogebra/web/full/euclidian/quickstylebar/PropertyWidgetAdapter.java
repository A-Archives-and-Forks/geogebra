package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.web.full.euclidian.quickstylebar.components.SliderWithProperty;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

public class PropertyWidgetAdapter {
	private final AppW appW;
	private List<IconButton> enumeratedPropertyButtons;

	public PropertyWidgetAdapter(AppW appW) {
		this.appW = appW;
	}

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
			enumeratedPropertyIconButton.addFastClickHandler(source -> onPropertyUpdate(
					iconProperty, finalI, enumeratedPropertyIconButton, uiUpdater));

			enumeratedPropertyIconButton.setActive(finalI == iconProperty.getIndex());
			buttonListComponent.add(enumeratedPropertyIconButton);
			enumeratedPropertyButtons.add(enumeratedPropertyIconButton);
			}

		return buttonListComponent;
	}

	private void onPropertyUpdate(IconsEnumeratedProperty<?> iconProperty, int finalI, IconButton
			enumeratedPropertyIconButton, Consumer<Integer> uiUpdater) {
		iconProperty.setIndex(finalI);
		setIconButtonActive(enumeratedPropertyIconButton);
		if (uiUpdater != null) {
			uiUpdater.accept(finalI);
		}
		appW.storeUndoInfo();
	}

	private void setIconButtonActive(IconButton enumeratedPropertyIconButton) {
		enumeratedPropertyButtons.forEach(iconButton -> iconButton.setActive(false));
		enumeratedPropertyIconButton.setActive(true);
	}

	public SliderWithProperty getSliderWidget(RangePropertyCollection<?, ?> property,
			GeoElement geo) {
		return new SliderWithProperty(appW, property, geo);
	}
}
