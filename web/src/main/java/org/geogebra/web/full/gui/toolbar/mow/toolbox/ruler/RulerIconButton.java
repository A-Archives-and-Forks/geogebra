package org.geogebra.web.full.gui.toolbar.mow.toolbox.ruler;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxMow.TOOLBOX_PADDING;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class RulerIconButton extends IconButton {
	private final EuclidianController ec;
	private RulerPopup rulerPopup;
	private final AppW appW;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - title
	 * @param dataTest - ui test id
	 */
	public RulerIconButton(AppW appW, SVGResource icon, String ariaLabel, String dataTitle,
			String dataTest) {
		super(appW, icon, ariaLabel, dataTitle, dataTest, null);
		this.appW = appW;
		ec = appW.getActiveEuclidianView().getEuclidianController();
		addFastClickHandler((event) -> {
			setActive(!isActive());
			initRulerTypePopup();
			if (!isActive()) {
				rulerPopup.hide();
			} else {
				showRulerTypePopup();
			}
			handleRuler();
		});
	}

	private void initRulerTypePopup() {
		if (rulerPopup == null) {
			rulerPopup = new RulerPopup(appW, this);
		}
	}

	private void showRulerTypePopup() {
		rulerPopup.showPopup(
				(int) (getAbsoluteLeft() + getOffsetWidth() + TOOLBOX_PADDING - appW.getAbsLeft()),
				(int) (getAbsoluteTop() - appW.getAbsTop()));
	}

	/**
	 * set active ruler, or remove it in switch ruler off
	 */
	public void handleRuler() {
		if (isActive()) {
			appW.setMode(rulerPopup.getActiveRulerType());
		} else {
			removeTool();
			appW.setMode(MODE_MOVE);
		}
	}

	/**
	 * remove measurement tool from construction
	 */
	public void removeTool() {
		ec.removeMeasurementTool(rulerPopup.getActiveRulerType());
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (rulerPopup != null) {
			rulerPopup.setLabels();
		}
	}
}
