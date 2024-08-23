package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.ArrayList;

import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

public class IconButtonWithProperties extends IconButton {
	private static final int CONTEXT_MENU_DISTANCE = 8;
	private final AppW appW;
	private GPopupPanel propertyPopup;
	private final ArrayList<IconButton> buttons = new ArrayList<>();

	public IconButtonWithProperties(AppW appW, SVGResource icon,
			AbstractEnumeratedProperty<?> property) {
		super(appW, icon, "stylebar.LineStyle", "stylebar.LineStyle", () -> {}, null);
		this.appW = appW;
		AriaHelper.setAriaHasPopup(this);

		buildGUI(property);

		addFastClickHandler((source) -> {
			propertyPopup.show();
			propertyPopup.setPopupPosition(getAbsoluteLeft(), getElement().getAbsoluteTop() + CONTEXT_MENU_DISTANCE);
		});
		propertyPopup.addCloseHandler((event) -> setActive(false));
	}

	private void buildGUI(AbstractEnumeratedProperty<?> property) {
		initPropertyPopup();
		FlowPanel propertyPanel = new FlowPanel();

		if (property instanceof IconsEnumeratedProperty) {
			PropertyResource[] icons = ((IconsEnumeratedProperty<?>) property).getValueIcons();
			for (int i = 0; i < icons.length; i++) {
				int finalI = i;
				IconButton button = new IconButton(appW, null, PropertiesIconAdapter.getIcon(icons[i]), null);
				button.addFastClickHandler(source -> {
					property.setIndex(finalI);
					buttons.forEach(iconButton -> iconButton.setActive(false));
					button.setActive(true);
					setIcon(button.getIcon());
				});
				button.setActive(finalI == property.getIndex());
				propertyPanel.add(button);
				buttons.add(button);
			}
		}

		propertyPopup.add(propertyPanel);
	}

	private void initPropertyPopup() {
		if (propertyPopup == null) {
			propertyPopup = new GPopupPanel(true, appW.getAppletFrame(), appW);
			propertyPopup.setStyleName("quickStylebar");
		}
	}
}
