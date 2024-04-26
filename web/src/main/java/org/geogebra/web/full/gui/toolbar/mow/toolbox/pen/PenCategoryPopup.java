package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.CategoryPopup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentSlider;

public class PenCategoryPopup extends CategoryPopup {
	private PenCategoryController controller;
	private ColorChooserPanel colorChooser;

	/**
	 * Constructor
	 * @param app - application
	 * @param tools - list of tools
	 * @param updateParentCallback - callback to update anchor
	 */
	public PenCategoryPopup(AppW app, List<Integer> tools,
			Consumer<Integer> updateParentCallback) {
		super(app, tools, updateParentCallback, true);

		controller = new PenCategoryController(app);

		addStyleName("penCategory");
		buildGui();
	}

	private void buildGui() {
		colorChooser = new ColorChooserPanel((AppW) getApplication(), (color) -> {
			int mode = getLastSelectedMode();
			if (mode == MODE_PEN) {
				controller.setLastPenColor(color);
			} else if (mode == MODE_HIGHLIGHTER) {
				controller.setLastHighlighterColor(color);
			}
			controller.updatePenColor(color);
		});
		addContent(colorChooser);

		ComponentSlider slider = new ComponentSlider((AppW) app);
		addContent(slider);
	}

	/**
	 * update color palette
	 */
	public void update() {
		int mode = getLastSelectedMode();
		if (mode == MODE_PEN) {
			colorChooser.updateColorSelection(controller.getLastPenColor());
		} else if (mode == MODE_HIGHLIGHTER) {
			colorChooser.updateColorSelection(controller.getLastHighlighterColor());
		}
		colorChooser.setDisabled(mode == MODE_ERASER);
	}
}
