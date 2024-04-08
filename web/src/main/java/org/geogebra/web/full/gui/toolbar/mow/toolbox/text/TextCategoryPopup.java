package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EQUATION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MEDIA_TEXT;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class TextCategoryPopup extends GPopupMenuW implements SetLabels {
	private IconButton textButton;

	public TextCategoryPopup(AppW app, IconButton textButton) {
		super(app);
		this.textButton = textButton;
		buildGui();
	}

	private void buildGui() {
		addItem(MODE_MEDIA_TEXT);
		addItem(MODE_EQUATION);

		popupMenu.selectItem(0);
	}

	private void addItem(int mode) {
		SVGResource image = (SVGResource) GGWToolBar.getImageURLNotMacro(
				ToolbarSvgResources.INSTANCE, mode, getApp());
		String text = getApp().getToolName(mode);

		AriaMenuItem item = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
				image.getSafeUri().asString(), text), true, () -> {});
		item.setScheduledCommand(() -> {
			getApp().setMode(mode);
			String fillColor = textButton.isActive()
					? getApp().getGeoGebraElement().getDarkColor(getApp().getFrameElement())
					: GColor.BLACK.toString();
			textButton.updateImgAndTxt(image.withFill(fillColor), mode, getApp());
			popupMenu.unselect();
			popupMenu.selectItem(item);
		});
		addItem(item);
	}

	@Override
	public void setLabels() {
		clearItems();
		buildGui();
	}
}
