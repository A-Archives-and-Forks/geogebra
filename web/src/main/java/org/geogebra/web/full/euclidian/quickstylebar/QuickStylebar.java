package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.euclidian.quickstylebar.components.IconButtonWithProperty;
import org.geogebra.web.full.gui.ContextMenuGeoElementW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Quick style bar containing IconButtons with dynamic position
 */
public class QuickStylebar extends FlowPanel implements EuclidianStyleBar {
	private final EuclidianView ev;
	private final StylebarPositioner stylebarPositioner;
	private final List<IconButton> quickButtons = new ArrayList<>();
	public final static int POPUP_MENU_DISTANCE = 8;
	private ContextMenuGeoElementW contextMenu;

	/**
	 * @param ev - parent view
	 */
	public QuickStylebar(EuclidianView ev) {
		this.ev = ev;
		this.stylebarPositioner = new StylebarPositioner(ev.getApplication());

		addStyleName("quickStylebar");
		addHandlers();
		buildGUI();
	}

	private void buildGUI() {
		List<GeoElement> activeGeoList = stylebarPositioner.createActiveGeoList();
		if (activeGeoList.isEmpty()) {
			return;
		}

		Property imageOpacityProperty = GeoElementPropertiesFactory.createImageOpacityProperty(
				getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList.get(0), null, false, imageOpacityProperty);

		addCropButton();

		Property fillingStyleProperty = GeoElementPropertiesFactory
				.createFillingStyleProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList.get(0), null, false, fillingStyleProperty);

		PropertiesArray lineStyleProperty = GeoElementPropertiesFactory
				.createNotesLineStyleProperties(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList.get(0), null, false,
				lineStyleProperty.getProperties());

		Property segmentStartProperty = GeoElementPropertiesFactory
				.createSegmentStartProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList.get(0), "segmentStyle", true, segmentStartProperty);

		Property segmentEndProperty = GeoElementPropertiesFactory
				.createSegmentEndProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList.get(0), "segmentStyle", true, segmentEndProperty);

		Property boldProperty = GeoElementPropertiesFactory.createBoldProperty(
				getApp().getLocalization(), activeGeoList);
		addPropertyButton(activeGeoList.get(0), boldProperty);

		Property horizontalAlignmentProperty = GeoElementPropertiesFactory
				.createHorizontalAlignmentProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList.get(0), null, true, horizontalAlignmentProperty);

		Property verticalAlignmentProperty = GeoElementPropertiesFactory
				.createVerticalAlignmentProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList.get(0), null, true, verticalAlignmentProperty);

		addDivider();

		addDeleteButton();
		addContextMenuButton();
	}

	private void addPropertyButton(GeoElement geo, Property... properties) {
		if (properties.length == 0 || properties[0] == null) {
			return;
		}
		Property firstProperty = properties[0];

		IconButton toggleButton = new IconButton(getApp(), null,
				PropertiesIconAdapter.getIcon(firstProperty), firstProperty.getName());
		toggleButton.setActive(((HasTextFormatter) geo).getFormatter().getFormat("bold", false));
		toggleButton.addFastClickHandler((source -> {
			boolean isSelected = toggleButton.isActive();
			((BooleanProperty) firstProperty).setValue(!isSelected);
			toggleButton.setActive(!isSelected);
		}));
		styleAndRegisterButton(toggleButton);
	}

	private void addPropertyPopupButton(GeoElement geo, String className,
			boolean closePopupOnAction, Property... properties) {
		if (properties.length == 0 || properties[0] == null) {
			return;
		}
		Property firstProperty = properties[0];

		IconButton button = new IconButtonWithProperty(getApp(), className,
				PropertiesIconAdapter.getIcon(firstProperty), firstProperty.getName(), geo,
				closePopupOnAction, properties);
		styleAndRegisterButton(button);
	}

	private void addDivider() {
		add(BaseWidgetFactory.INSTANCE.newDivider(true));
	}

	private void addDeleteButton() {
		IconButton deleteButton = new IconButton(getApp(),
				() -> {
					getApp().closePopups();
					getApp().splitAndDeleteSelectedObjects();
				},
				MaterialDesignResources.INSTANCE.delete_black(), "Delete");
		styleAndRegisterButton(deleteButton);
	}

	private void addCropButton() {
		if (!(isImageGeoSelected()
				&& ev.getMode() != EuclidianConstants.MODE_SELECT)) {
			return;
		}

		IconButton cropButton = new IconButton(getApp(), null,
				MaterialDesignResources.INSTANCE.crop_black(), "stylebar.Crop");
		cropButton.setActive(ev.getBoundingBox() != null && ev.getBoundingBox().isCropBox());
		cropButton.addFastClickHandler((source) -> {
			getApp().closePopups();
			cropButton.setActive(!cropButton.isActive());
			ev.getEuclidianController().updateBoundingBoxFromSelection(cropButton.isActive());
			ev.repaintView();
		});
		styleAndRegisterButton(cropButton);
	}

	private void addContextMenuButton() {
		IconButton contextMenuBtn = new IconButton(getApp(), null,
				MaterialDesignResources.INSTANCE.more_vert_black(), "More");

		contextMenu = createContextMenu(contextMenuBtn);
		contextMenuBtn.addFastClickHandler((event) -> {
			getApp().closePopups();
			GPopupMenuW popupMenu = contextMenu.getWrappedPopup();
			if (popupMenu.isMenuShown()) {
				popupMenu.hideMenu();
			} else {
				popupMenu.show(contextMenuBtn, 0, getOffsetHeight() + POPUP_MENU_DISTANCE);
				getApp().registerPopup(popupMenu.getPopupPanel());
			}

			contextMenuBtn.setActive(popupMenu.isMenuShown());
			getApp().hideKeyboard();
		});

		styleAndRegisterButton(contextMenuBtn);
	}

	private ContextMenuGeoElementW createContextMenu(IconButton contextMenuBtn) {
		ContextMenuGeoElementW contextMenu = ((GuiManagerW) getApp().getGuiManager())
				.getPopupMenu(ev.getEuclidianController().getAppSelectedGeos());
		GPopupPanel popupPanel = contextMenu.getWrappedPopup().getPopupPanel();
		popupPanel.addAutoHidePartner(getElement());
		popupPanel.addCloseHandler(closeEvent -> {
			contextMenuBtn.deactivate();
			contextMenu.getWrappedPopup().hideMenu();

		});

		return contextMenu;
	}

	private void styleAndRegisterButton(IconButton button) {
		button.addStyleName("small");
		quickButtons.add(button);
		add(button);
	}

	private AppW getApp() {
		return (AppW) ev.getApplication();
	}

	private void addHandlers() {
		ev.getApplication().getSelectionManager()
				.addSelectionListener((geo, addToSelection) -> {
					if (addToSelection) {
						return;
					}
					updateStyleBar();
				});

		EventUtil.stopPointer(getElement());
		ClickStartHandler.initDefaults(asWidget(), false, true);
	}

	@Override
	public void setMode(int mode) {
		// nothing for now
	}

	@Override
	public void setLabels() {
		quickButtons.forEach(SetLabels::setLabels);
		if (contextMenu != null) {
			contextMenu.update();
		}
	}

	@Override
	public void restoreDefaultGeo() {
		// nothing for now
	}

	@Override
	public void updateStyleBar() {
		if (!isVisible()) {
			return;
		}

		clear();
		buildGUI();
		GPoint position = stylebarPositioner.getPositionForStyleBar(getOffsetWidth(),
				getOffsetHeight());
		if (position != null) {
			getElement().getStyle().setLeft(position.x, Unit.PX);
			getElement().getStyle().setTop(position.y, Unit.PX);
		} else {
			setVisible(false);
		}
	}

	@Override
	public void updateButtonPointCapture(int mode) {
		// nothing for now
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// nothing for now
	}

	@Override
	public int getPointCaptureSelectedIndex() {
		return 0;
	}

	@Override
	public void updateGUI() {
		// nothing for now
	}

	@Override
	public void hidePopups() {
		// nothing for now
	}

	@Override
	public void resetFirstPaint() {
		// nothing for now
	}

	@Override
	public void reinit() {
		// nothing for now
	}

	private boolean isImageGeoSelected() {
		return ev.getEuclidianController().getAppSelectedGeos().size() == 1
				&& ev.getEuclidianController().getAppSelectedGeos().get(0).isGeoImage();
	}
}
