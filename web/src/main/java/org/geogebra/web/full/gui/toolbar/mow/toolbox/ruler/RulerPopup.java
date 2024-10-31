package org.geogebra.web.full.gui.toolbar.mow.toolbox.ruler;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SVGResourcePrototype;

public class RulerPopup extends GPopupMenuW implements SetLabels {
	private RulerIconButton rulerButton;
	private int activeRulerMode = MODE_RULER;

	/**
	 * Constructor
	 * @param app - application
	 */
	public RulerPopup(AppW app, RulerIconButton rulerButton) {
		super(app);
		this.rulerButton = rulerButton;
		buildGui();
	}

	private void buildGui() {
		addItem(getApp().getLocalization().getMenu("Ruler"), MODE_RULER);
		for (int mode: getApp().getVendorSettings().getProtractorTools(
				getApp().getLocalization().getLanguage())) {
			addItem(getApp().getLocalization().getMenu(EuclidianConstants.getModeText(mode)),
					mode);
		}
		popupMenu.selectItem(activeRulerMode == MODE_RULER ? 0 : 1);
	}

	private void addItem(String text, int mode) {
		AriaMenuItem item = MainMenu.getMenuBarItem(
				SVGResourcePrototype.EMPTY, text, () -> {});
		GGWToolBar.getImageResource(mode, getApp(), item);
		item.setScheduledCommand(() -> {
			activeRulerMode = mode;
			updateRulerButton(mode);
			setHighlight(item);
		});
		addItem(item);
	}

	private void updateRulerButton(int mode) {
		GGWToolBar.getImageResource(mode, getApp(), image -> {
			String fillColor = rulerButton.isActive()
					? getApp().getGeoGebraElement().getDarkColor(getApp().getFrameElement())
					: GColor.BLACK.toString();
			rulerButton.removeTool();
			rulerButton.updateImgAndTxt(new ImageIconSpec(((SVGResource) image)
					.withFill(fillColor)), mode, getApp());
			rulerButton.handleRuler();
		});
	}

	private void setHighlight(AriaMenuItem highlighted) {
		popupMenu.unselect();
		popupMenu.selectItem(highlighted);
	}

	public int getActiveRulerType() {
		return activeRulerMode;
	}

	/**
	 * Updates selection highlighting in the popup menu
	 */
	public void updatePopupSelection() {
		Dom.toggleClass(popupMenu.getSelectedItem(), "selectedItem", rulerButton.isActive());
	}

	/**
	 * Rebuilds the GUI (e.g. language changes)
	 */
	@Override
	public void setLabels() {
		clearItems();
		boolean triangleSupported = getApp().getVendorSettings().getProtractorTools(
				getApp().getLocalization().getLanguage()).contains(MODE_TRIANGLE_PROTRACTOR);
		if (activeRulerMode == MODE_TRIANGLE_PROTRACTOR && !triangleSupported) {
			activeRulerMode = MODE_PROTRACTOR;
			updateRulerButton(activeRulerMode);
		}
		buildGui();
	}
}