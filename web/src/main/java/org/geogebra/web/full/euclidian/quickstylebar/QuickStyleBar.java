package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertySupplier;
import org.geogebra.common.properties.ValuedProperty;
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

import com.google.gwt.core.client.Scheduler;

/**
 * Quick style bar containing IconButtons with dynamic position
 */
public class QuickStyleBar extends FlowPanel implements EuclidianStyleBar {
	private final EuclidianView ev;
	private final StylebarPositioner stylebarPositioner;
	private final List<IconButton> quickButtons = new ArrayList<>();
	public final static int POPUP_MENU_DISTANCE = 8;
	private ContextMenuGeoElementW contextMenu;

	/**
	 * @param ev - parent view
	 */
	public QuickStyleBar(EuclidianView ev) {
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
		GeoElementPropertiesFactory geoElementPropertiesFactory =
				GlobalScope.geoElementPropertiesFactory;
		Property imageOpacityProperty = geoElementPropertiesFactory.createImageOpacityProperty(
				getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, null, false, imageOpacityProperty);

		addCropButton();

		PropertiesArray colorWithOpacityProperty = geoElementPropertiesFactory
				.createNotesColorWithOpacityProperties(getApp().getLocalization(), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE,
				colorWithOpacityProperty.getProperties());

		PropertySupplier colorProperty = withStrokeSplitting(
				(geos) -> geoElementPropertiesFactory.createNotesColorProperty(
				getApp().getLocalization(), geos), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE, colorProperty);

		Property inlineBackgroundColorProperty = geoElementPropertiesFactory
				.createInlineBackgroundColorProperty(getApp().getLocalization(), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE_OR_TABLE_CONTENT,
				inlineBackgroundColorProperty);

		PropertiesArray pointStyleProperty = geoElementPropertiesFactory
				.createPointStyleExtendedProperties(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, "pointStyle", true,
				pointStyleProperty.getProperties());

		Property fillingStyleProperty = geoElementPropertiesFactory
				.createFillingStyleProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, null, false, fillingStyleProperty);

		List<PropertySupplier> lineStylePropertyWithSplit = new ArrayList<>();
		lineStylePropertyWithSplit.add(withStrokeSplitting(geos ->
				geoElementPropertiesFactory.createLineStyleProperty(
						getApp().getLocalization(), geos), activeGeoList));
		lineStylePropertyWithSplit.add(withStrokeSplitting(geos ->
				geoElementPropertiesFactory.createNotesThicknessProperty(
						getApp().getLocalization(), geos), activeGeoList));

		addPropertyPopupButton(activeGeoList, null, false,
				lineStylePropertyWithSplit.stream()
						.filter(Objects::nonNull).toArray(PropertySupplier[]::new));

		Property segmentStartProperty = geoElementPropertiesFactory
				.createSegmentStartProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, "segmentStyle", true, segmentStartProperty);

		Property segmentEndProperty = geoElementPropertiesFactory
				.createSegmentEndProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, "segmentStyle", true, segmentEndProperty);

		PropertiesArray cellBorderProperty = geoElementPropertiesFactory
				.createCellBorderStyleProperties(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, "cellBorderStyle", true,
				UndoActionType.STYLE_OR_TABLE_CONTENT, cellBorderProperty.getProperties());

		PropertiesArray objectBorderProperty = geoElementPropertiesFactory
				.createObjectBorderProperties(getApp().getLocalization(), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE,
				objectBorderProperty.getProperties());

		addDivider();

		Property fontColorProperty = geoElementPropertiesFactory.createNotesFontColorProperty(
				getApp().getLocalization(), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE_OR_CONTENT,
				fontColorProperty);

		Property fontSizeProperty = geoElementPropertiesFactory.createTextFontSizeProperty(
				getApp().getLocalization(), activeGeoList, ev);
		addPropertyPopupButton(activeGeoList, "gwt-PopupPanel contextSubMenu", true,
				UndoActionType.STYLE_OR_CONTENT, fontSizeProperty);

		BooleanProperty boldProperty = geoElementPropertiesFactory
				.createBoldProperty(getApp().getLocalization(), activeGeoList);
		addTextFormatPropertyButton(activeGeoList, boldProperty);

		BooleanProperty italicProperty = geoElementPropertiesFactory
				.createItalicProperty(getApp().getLocalization(), activeGeoList);
		addTextFormatPropertyButton(activeGeoList, italicProperty);

		BooleanProperty underlineProperty = geoElementPropertiesFactory
				.createUnderlineProperty(getApp().getLocalization(), activeGeoList);
		addTextFormatPropertyButton(activeGeoList, underlineProperty);

		Property horizontalAlignmentProperty = geoElementPropertiesFactory
				.createHorizontalAlignmentProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, null, true,
				UndoActionType.STYLE_OR_CONTENT, horizontalAlignmentProperty);

		Property verticalAlignmentProperty = geoElementPropertiesFactory
				.createVerticalAlignmentProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, null, true,
				UndoActionType.STYLE_OR_TABLE_CONTENT, verticalAlignmentProperty);

		addDivider();

		addDeleteButton();
		addContextMenuButton();
	}

	private PropertySupplier withStrokeSplitting(Function<List<GeoElement>, Property> map,
			List<GeoElement> activeGeoList) {

		return new PropertySupplier() {

			Property current = map.apply(activeGeoList);

			@Override
			public Property updateAndGet() {
				if (!getApp().getActiveEuclidianView()
						.getEuclidianController().splitSelectedStrokes(true)) {
					return current;
				}
				current = map.apply(getApp().getSelectionManager().getSelectedGeos());
				addUndoActionObserver(new PropertySupplier[]{current},
						getApp().getSelectionManager().getSelectedGeos(),
						UndoActionType.STYLE);
				return current;
			}

			@Override
			public Property get() {
				return current;
			}
		};
	}

	private void addColorPropertyButton(List<GeoElement> geos, UndoActionType undoFiler,
			PropertySupplier... properties) {
		if (properties.length == 0 || properties[0] == null || properties[0].get() == null) {
			return;
		}
		Property firstProperty = properties[0].get();
		addUndoActionObserver(properties, geos, undoFiler);
		IconButtonWithProperty colorButton = new IconButtonWithProperty(getApp(), "colorStyle",
				PropertiesIconAdapter.getIcon(firstProperty), firstProperty.getName(),
				geos.get(0), true, properties);

		setPopupHandlerWithUndoAction(colorButton);
		styleAndRegisterButton(colorButton);
	}

	private void addTextFormatPropertyButton(List<GeoElement> geos,
			BooleanProperty property) {
		if (property == null || !(geos.get(0) instanceof HasTextFormatter
				|| geos.get(0) instanceof TextProperties)) {
			return;
		}
		property.addValueObserver(new UndoActionObserver(geos, UndoActionType.STYLE_OR_CONTENT));
		IconButton toggleButton = new IconButton(getApp(), null,
				PropertiesIconAdapter.getIcon(property), property.getName());
		toggleButton.setActive(property.getValue());
		addFastClickHandlerWithUndoContentAction(toggleButton, property);
		styleAndRegisterButton(toggleButton);
	}

	protected void addFastClickHandlerWithUndoContentAction(IconButton btn,
			BooleanProperty property) {
		btn.addFastClickHandler(ignore -> {
			getApp().closePopups();
			property.setValue(!btn.isActive());
			btn.setActive(!btn.isActive());
		});
	}

	protected void setPopupHandlerWithUndoAction(IconButtonWithProperty iconButton) {
		iconButton.addPopupHandler((property, value) -> {
			getApp().closePopups();
			property.setValue(value);
		});
	}

	private void addPropertyPopupButton(List<GeoElement> geos, String className,
			boolean closePopupOnAction, PropertySupplier... properties) {
		addPropertyPopupButton(geos, className, closePopupOnAction,
				UndoActionType.STYLE, properties);
	}

	private void addPropertyPopupButton(List<GeoElement> geos, String className,
			boolean closePopupOnAction, UndoActionType undoType, PropertySupplier... properties) {
		if (properties.length == 0 || properties[0] == null || properties[0].get() == null) {
			return;
		}
		Property firstProperty = properties[0].get();
		addUndoActionObserver(properties, geos, undoType);
		IconButton button = new IconButtonWithProperty(getApp(), className,
				PropertiesIconAdapter.getIcon(firstProperty), firstProperty.getName(), geos.get(0),
				closePopupOnAction, properties);
		styleAndRegisterButton(button);
	}

	private void addUndoActionObserver(PropertySupplier[] properties, List<GeoElement> geos,
			UndoActionType undoActionType) {
		for (PropertySupplier propertySupplier: properties) {
			Property property = propertySupplier.get();
			if (property instanceof ValuedProperty) {
				((ValuedProperty<?>) property).addValueObserver(
						new UndoActionObserver(geos, undoActionType));
			}
		}
	}

	private void addDivider() {
		if (getElement().hasChildNodes() && !isLastElemDivider()) {
			add(BaseWidgetFactory.INSTANCE.newDivider(true));
		}
	}

	private boolean isLastElemDivider() {
		String lastElemClassName = getChildren() != null
				? getChildren().get(getChildren().size() - 1).getStyleName() : "";
		return lastElemClassName.contains("divider");
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
		// update from slider may trigger temporarily removing geos; use deferred here to
		// avoid closing of the StyleBar
		Scheduler.get().scheduleDeferred(() -> {
			GPoint position = stylebarPositioner.getPositionForStyleBar(getOffsetWidth(),
					getOffsetHeight());
			if (position != null) {
				getElement().getStyle().setLeft(position.x, Unit.PX);
				getElement().getStyle().setTop(position.y, Unit.PX);
			} else {
				setVisible(false);
				getApp().closePopups();
			}
		});
	}

	@Override
	public void updateButtonPointCapture(int mode) {
		// nothing for now
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		if (!isVisible()) {
			return;
		}

		for (IconButton button : quickButtons) {
			if (button instanceof IconButtonWithProperty) {
				((IconButtonWithProperty) button).closePopup();
			}
		}
		updateStyleBar();
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
