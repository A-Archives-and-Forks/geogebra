package org.geogebra.web.full.euclidian.quickstylebar.components;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.web.full.javax.swing.LineThicknessCheckMarkItem;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.gwtproject.user.client.ui.FlowPanel;

public class BorderThicknessPanel extends FlowPanel {
	private final RangePropertyCollection<?> property;
	private List<LineThicknessCheckMarkItem> checkMarkItems;

	/**
	 * UI presenting cell border thickness
	 * @param property - cell border thickness property
	 */
	public BorderThicknessPanel(RangePropertyCollection<?> property) {
		this.property = property;
		buildGui();
	}

	private void buildGui() {
		add(BaseWidgetFactory.INSTANCE.newDivider(false));
		checkMarkItems = new ArrayList<>();

		addThicknessCheckMarkItem(property, "thin", 1);
		addThicknessCheckMarkItem(property, "thick", 3);
	}

	private void addThicknessCheckMarkItem(RangePropertyCollection<?> property,
			String style, int value) {
		LineThicknessCheckMarkItem checkMarkItem = new LineThicknessCheckMarkItem(style, value);
		add(checkMarkItem);
		checkMarkItem.setSelected(property.getValue() == value);
		checkMarkItems.add(checkMarkItem);

		ClickStartHandler.init(checkMarkItem,
				new ClickStartHandler(true, true) {
					@Override
					public void onClickStart(int x, int y, PointerEventType type) {
						checkMarkItems.forEach(item -> item.setSelected(false));
						checkMarkItem.setSelected(true);
						property.setValue(value);
					}
				});
	}
}
