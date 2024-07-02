package org.geogebra.web.full.gui.toolbar.mow;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.NotesToolbox;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.mow.header.NotesTopbar;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.ui.Widget;

public class NotesLayout implements SetLabels {
	private final AppW appW;
	private final @CheckForNull NotesToolbox toolbar;
	private final @CheckForNull NotesTopbar topbar;
	private StandardButton pageControlButton;
	private @CheckForNull PageListPanel pageControlPanel;

	/**
	 * @param appW application
	 */
	public NotesLayout(AppW appW) {
		this.appW = appW;
		this.toolbar = appW.showToolBar() ? new NotesToolbox(appW) : null;
		topbar = new NotesTopbar(appW);
		createPageControlButton();
		setLabels();
	}

	private void createPageControlButton() {
		pageControlButton = new StandardButton(
				MaterialDesignResources.INSTANCE.mow_page_control(), null, 24);
		new FocusableWidget(AccessibilityGroup.PAGE_LIST_OPEN, null, pageControlButton)
				.attachTo(appW);
		pageControlButton.setStyleName("mowFloatingButton");
		pageControlButton.addStyleName("floatingActionButton");
		showPageControlButton(true);

		pageControlButton.addBitlessDomHandler(event -> setTouchStyleForCards(),
				TouchStartEvent.getType());
		pageControlButton.addFastClickHandler(this::openPagePanel);
	}

	/**
	 * make sure style is touch also on whiteboard
	 */
	protected void setTouchStyleForCards() {
		getPageControlPanel().setIsTouch();
	}

	/**
	 * @return button to open/close the page side panel
	 */
	public StandardButton getPageControlButton() {
		return pageControlButton;
	}

	/**
	 * @param doShow
	 *            - true if page control button should be visible, false
	 *            otherwise
	 */
	public void showPageControlButton(boolean doShow) {
		if (pageControlButton == null) {
			return;
		}
		Dom.toggleClass(pageControlButton, "showMowFloatingButton",
				"hideMowFloatingButton", doShow);
	}

	/**
	 * Opens the page control panel
	 */
	public void openPagePanel(Widget trigger) {
		appW.hideMenu();
		EuclidianController ec = appW.getActiveEuclidianView().getEuclidianController();
		ec.widgetsToBackground();

		getPageControlPanel().open();
		appW.getPageController().updatePreviewImage();
		if (topbar != null) {
			topbar.deselectDragButton();
		}
	}

	private PageListPanel getPageControlPanel() {
		if (pageControlPanel == null) {
			pageControlPanel = ((AppWFull) appW).getAppletFrame()
					.getPageControlPanel();
		}
		return pageControlPanel;
	}

	@Override
	public void setLabels() {
		if (toolbar != null) {
			toolbar.setLabels();
		}
		if (topbar != null) {
			topbar.setLabels();
		}
		pageControlButton
				.setTitle(appW.getLocalization().getMenu("PageControl"));
	}

	/**
	 * update style of undo+redo buttons
	 */
	public void updateUndoRedoActions() {
		if (topbar != null) {
			topbar.updateUndoRedoActions(appW.getKernel());
		}
	}

	/**
	 * Select the correct icon in the toolbar
	 * @param mode selected tool
	 */
	public void setMode(int mode) {
		if (toolbar != null) {
			toolbar.setMode(mode);
		}
	}

	public Widget getToolbar() {
		return toolbar;
	}

	public Widget getTopbar() {
		return topbar;
	}
}
