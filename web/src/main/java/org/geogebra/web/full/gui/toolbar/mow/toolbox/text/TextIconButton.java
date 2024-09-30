package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MEDIA_TEXT;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.toolbox.ToolboxIcon;
import org.geogebra.web.html5.main.toolbox.ToolboxIconResource;

public class TextIconButton extends IconButton {
	private final AppW appW;
	private TextCategoryPopup textCategoryPopup;

	/**
	 * Constructor
	 * @param appW - application
	 * @param deselectButtons - deselect buttons callback
	 */
	public TextIconButton(AppW appW, Runnable deselectButtons,
			ToolboxIconResource toolboxIconResource) {
		super(appW, toolboxIconResource.getImageResource(ToolboxIcon.TEXTS), "Text.Tool",
				"Text.Tool", "", null);
		this.appW = appW;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			deselectButtons.run();
			initPopupAndShow();
			setActive(true);

			appW.setMode(textCategoryPopup.getLastSelectedMode());
			textCategoryPopup.getPopupPanel().addCloseHandler(e ->
					AriaHelper.setAriaExpanded(this, false));
		});
	}

	private void initPopupAndShow() {
		if (textCategoryPopup == null) {
			textCategoryPopup = new TextCategoryPopup(appW, this);
			textCategoryPopup.getPopupPanel().setAutoHideEnabled(false);
		}

		showHidePopup();
	}

	private void showHidePopup() {
		if (getPopup().isShowing()) {
			getPopup().hide();
		} else {
			ToolboxPopupPositioner.showRelativeToToolbox(getPopup(),
					this, appW);
		}

		AriaHelper.setAriaExpanded(this, getPopup().isShowing());
	}

	@Override
	public int getMode() {
		return textCategoryPopup != null ? textCategoryPopup.getLastSelectedMode()
				: MODE_MEDIA_TEXT;
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (textCategoryPopup != null) {
			textCategoryPopup.setLabels();
		}
	}

	private GPopupPanel getPopup() {
		return textCategoryPopup.getPopupPanel();
	}
}
