package org.geogebra.web.shared.mow.header;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;

import java.util.function.Consumer;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.full.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.gui.zoompanel.ZoomController;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.topbar.TopBarIcon;
import org.geogebra.web.html5.main.topbar.TopBarIconResource;

public class TopBarController {
	private final AppW appW;
	private final ZoomController zoomController;
	private final EuclidianView view;
	private ContextMenuGraphicsWindowW settingsContextMenu;
	private @CheckForNull PageListPanel pageControlPanel;
	private final TopBarIconResource topBarIconResource;

	/**
	 * Controller
	 * @param appW - application
	 * @param topBarIconResource - top bar resources
	 */
	public TopBarController(AppW appW, TopBarIconResource topBarIconResource) {
		this.appW = appW;
		this.view = appW.getActiveEuclidianView();
		this.topBarIconResource = topBarIconResource;
		zoomController = new ZoomController(appW, view);
	}

	/**
	 * on menu pressed
	 */
	public void onMenuToggle() {
		appW.closePopups();
		appW.hideKeyboard();
		appW.toggleMenu();
	}

	/**
	 * on undo pressed
	 */
	public void onUndo() {
		appW.closePopups();
		appW.getGuiManager().undo();
	}

	/**
	 * on redo pressed
	 */
	public void onRedo() {
		appW.closePopups();
		appW.getGuiManager().redo();
	}

	/**
	 * on zoom in press
	 */
	public void onZoomIn() {
		appW.closePopups();
		setSelectMode();
		zoomController.onZoomInPressed();
	}

	/**
	 * on zoom out press
	 */
	public void onZoomOut() {
		appW.closePopups();
		setSelectMode();
		zoomController.onZoomOutPressed();
	}

	private void setSelectMode() {
		if (appW.getMode() != MODE_SELECT_MOW) {
			appW.setMode(MODE_SELECT_MOW);
		}
	}

	/**
	 * on home press
	 */
	public void onHome() {
		appW.closePopups();
		zoomController.onHomePressed();
	}

	/**
	 * on drag button press
	 * @param isDragButtonActive whether drag is on or off
	 */
	public void onDrag(boolean isDragButtonActive) {
		if (isDragButtonActive) {
			appW.setMode(EuclidianConstants.MODE_SELECT_MOW);
		} else {
			appW.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
			appW.hideMenu();
			appW.closePopups();
		}
	}

	public AppW getApp() {
		return appW;
	}

	/**
	 * update home button state based on whether view is standard view
	 * @param homeBtn - home button
	 */
	public void updateHomeButtonVisibility(IconButton homeBtn) {
		if (view == null) {
			return;
		}
		if (view.isCoordSystemTranslatedByAnimation()) {
			return;
		}
		if (homeBtn != null) {
			homeBtn.setDisabled(view.isStandardView());
		}
	}

	/**
	 * @return whether fullscreen button is allowed or not
	 */
	public boolean needsFullscreenButton() {
		return ZoomController.needsFullscreenButton(appW);
	}

	/**
	 * on fullscreen press
	 * @param fullscreenBtn - fullscreen button
	 */
	public void onFullscreenOn(IconButton fullscreenBtn) {
		appW.closePopups();
		zoomController.onFullscreenPressed(null, getFullscreenBtnSelectCB(fullscreenBtn));
	}

	/**
	 * on fullscreen exit
	 * @param fullscreenBtn - fullscreen button
	 */
	public void onFullscreenExit(IconButton fullscreenBtn) {
		appW.closePopups();
		zoomController.onExitFullscreen(null, getFullscreenBtnSelectCB(fullscreenBtn));
	}

	private Consumer<Boolean> getFullscreenBtnSelectCB(final IconButton fullscreenBtn) {
		return fullScreenActive -> {
			if (fullscreenBtn != null) {
				fullscreenBtn.setIcon(fullScreenActive
						? topBarIconResource.getImageResource(TopBarIcon.FULLSCREEN_OFF)
						: topBarIconResource.getImageResource(TopBarIcon.FULLSCREEN_ON));
			}
		};
	}

	/**
	 * on settings press
	 * @param anchor - settings button
	 */
	public void onSettingsOpen(IconButton anchor, FocusableWidget focusableAnchor) {
		appW.getAccessibilityManager().setAnchor(focusableAnchor);
		initSettingsContextMenu(anchor);
		toggleSettingsContextMenu(anchor);
	}

	private void initSettingsContextMenu(IconButton anchor) {
		settingsContextMenu = new ContextMenuGraphicsWindowW(appW, 0, 0, false);
		getSettingsContextMenu().setAutoHideEnabled(false);
		getSettingsContextMenu().addCloseHandler(event -> anchor.setActive(false));
	}

	private void toggleSettingsContextMenu(IconButton anchor) {
		boolean settingsShowing  = getSettingsContextMenu().isShowing();
		if (settingsShowing) {
			settingsContextMenu.getWrappedPopup().hide();
		} else {
			appW.closePopups();
			showAndFocusMenuRelativeTo(anchor);
			appW.registerPopup(getSettingsContextMenu());
		}
		anchor.setActive(!settingsShowing);
	}

	private void showAndFocusMenuRelativeTo(IconButton anchor) {
		settingsContextMenu.getWrappedPopup().showAtPoint((int) (anchor.getAbsoluteLeft()
				- appW.getAbsLeft()), (int) (anchor.getAbsoluteTop()
				+ anchor.getOffsetHeight() - appW.getAbsTop()));
		settingsContextMenu.getWrappedPopup().getPopupMenu().getItemAt(0).getElement().focus();
	}

	private GPopupPanel getSettingsContextMenu() {
		return settingsContextMenu.getWrappedPopup().getPopupPanel();
	}

	/**
	 * Toggle the page control panel
	 */
	public void togglePagePanel() {
		if (getPageControlPanel().isVisible()) {
			getPageControlPanel().close();
		} else {
			openPagePanel();
		}
	}

	private void openPagePanel() {
		appW.hideMenu();
		appW.closePopups();
		EuclidianController ec = appW.getActiveEuclidianView().getEuclidianController();
		ec.widgetsToBackground();

		getPageControlPanel().open();
		appW.getPageController().updatePreviewImage();
		appW.setMode(MODE_SELECT_MOW);
	}

	private PageListPanel getPageControlPanel() {
		if (pageControlPanel == null) {
			pageControlPanel = ((AppWFull) appW).getAppletFrame()
					.getPageControlPanel();
		}
		return pageControlPanel;
	}

	/**
	 * Set touch style for cards.
	 */
	public void setTouchStyleForPagePreviewCards() {
		getPageControlPanel().setIsTouch();
	}

	/**
	 * register focusable widget
	 * @param button - focusable widget
	 * @param group - accessibility group
	 */
	public void registerFocusable(IconButton button, AccessibilityGroup group) {
		new FocusableWidget(group, null, button).attachTo(appW);
	}

	/**
	 * register focusable widget
	 * @param group - accessibility group
	 * @param button - focusable widget
	 * @return focusable widget
	 */
	public FocusableWidget getRegisteredFocusable(AccessibilityGroup group,
			IconButton button) {
		FocusableWidget focusableWidget = new FocusableWidget(group, null, button);
		focusableWidget.attachTo(appW);
		return focusableWidget;
	}
}
