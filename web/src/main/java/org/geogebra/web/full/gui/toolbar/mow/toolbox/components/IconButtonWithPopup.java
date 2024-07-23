package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class IconButtonWithPopup extends IconButton {
	private final AppW appW;
	private CategoryPopup categoryPopup;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - aria label
	 * @param tools - list of tools
	 * @param deselectButtons - deselect button callback
	 */
	public IconButtonWithPopup(AppW appW, SVGResource icon, String ariaLabel, List<Integer> tools,
			Runnable deselectButtons) {
		super(appW, icon, ariaLabel, ariaLabel, () -> {}, null);
		this.appW = appW;
		AriaHelper.setAriaHasPopup(this);

		addFastClickHandler(source -> {
			deselectButtons.run();
			initAndShowPopup(tools);
			setActive(true);

			categoryPopup.addCloseHandler((event) -> {
				AriaHelper.setAriaExpanded(this, false);
				deactivate();
			});
		});
	}

	private void initAndShowPopup(List<Integer> tools) {
		if (categoryPopup == null) {
			categoryPopup = new CategoryPopup(appW, tools, getUpdateButtonCallback());
			categoryPopup.setAutoHideEnabled(false);
		}

		showHidePopup();
		updateSelection();
	}

	private void showHidePopup() {
		if (categoryPopup.isShowing()) {
			categoryPopup.hide();
		} else {
			ToolboxPopupPositioner.showRelativeToToolbox(categoryPopup, this, appW);
		}
	}

	private void updateSelection() {
		AriaHelper.setAriaExpanded(this, categoryPopup.isShowing());
		appW.setMode(categoryPopup.getLastSelectedMode());
	}

	private Consumer<Integer> getUpdateButtonCallback() {
		return mode -> {
			SVGResource image =  (SVGResource) GGWToolBar.getImageURLNotMacro(
					ToolbarSvgResources.INSTANCE, mode, appW);
			updateImgAndTxt(image, mode, appW);
			setActive(true);
		};
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (categoryPopup != null) {
			categoryPopup.setLabels();
		}
	}
}
