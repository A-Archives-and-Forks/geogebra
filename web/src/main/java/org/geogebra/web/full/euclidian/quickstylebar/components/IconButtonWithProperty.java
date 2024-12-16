package org.geogebra.web.full.euclidian.quickstylebar.components;

import static org.geogebra.web.full.euclidian.quickstylebar.QuickStyleBar.POPUP_MENU_DISTANCE;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertySupplier;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.impl.collections.ColorPropertyCollection;
import org.geogebra.common.properties.impl.collections.FlagListPropertyCollection;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.common.properties.impl.objects.BorderColorProperty;
import org.geogebra.common.properties.impl.objects.BorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.CellBorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.LabelStyleProperty;
import org.geogebra.common.properties.impl.objects.NotesInlineBackgroundColorProperty;
import org.geogebra.common.properties.impl.objects.NotesThicknessProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.euclidian.LabelSettingsPanel;
import org.geogebra.web.full.euclidian.LabelValuePanel;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.euclidian.quickstylebar.PropertyWidgetAdapter;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

public class IconButtonWithProperty extends IconButton {
	private final AppW appW;
	private final List<GeoElement> geos;
	private GPopupPanel propertyPopup;
	private SliderWithProperty lineThicknessSlider;
	private LabelValuePanel labelPanel;
	private final PropertyWidgetAdapter widgetAdapter;
	private PopupColorHandler popupHandler;

	/**
	 * Constructor
	 * @param appW - application
	 * @param className - class name
	 * @param icon - svg resource of button
	 * @param ariaLabel - aria label
	 * @param geos - geo elements
	 * @param closePopupOnAction - weather should close popup after clicking on popup element
	 * @param properties - array of applicable properties
	 */
	public IconButtonWithProperty(AppW appW, String className, SVGResource icon, String ariaLabel,
			GeoElement geo, boolean closePopupOnAction, PropertySupplier... properties) {
		super(appW, new ImageIconSpec(icon), ariaLabel, ariaLabel, () -> {}, null);
		this.appW = appW;
		this.geos = geos;
		widgetAdapter = new PropertyWidgetAdapter(appW, closePopupOnAction);
		AriaHelper.setAriaHasPopup(this);

		buildGUI(properties);
		if (className != null) {
			propertyPopup.addStyleName(className);
		}
		addHandlers();
	}

	private void addHandlers() {
		addFastClickHandler((source) -> {
			if (propertyPopup.isShowing()) {
				propertyPopup.hide();
			} else {
				update();
				showPropertyPopup();
			}
			AriaHelper.setAriaExpanded(this, propertyPopup.isShowing());
		});

		propertyPopup.addCloseHandler((event) -> {
			setActive(false);
			AriaHelper.setAriaExpanded(this, false);
		});
	}

	private void buildGUI(PropertySupplier... properties) {
		initPropertyPopup();
		FlowPanel propertyPanel = new FlowPanel();

		for (PropertySupplier property : properties) {
			processProperty(property, propertyPanel);
		}

		propertyPopup.add(propertyPanel);
	}

	private void processProperty(PropertySupplier propertySupplier, FlowPanel parent) {
		Property property = propertySupplier.get();
		if (property instanceof IconsEnumeratedProperty) {
			FlowPanel enumeratedPropertyButtonPanel = widgetAdapter.getIconListPanel(
					(IconsEnumeratedProperty<?>) property, propertySupplier, (index) -> {
						if (lineThicknessSlider != null) {
							lineThicknessSlider.setLineType(index);
						}
						setIcon(PropertiesIconAdapter.getIcon(((IconsEnumeratedProperty<?>)
								property).getValueIcons()[index]));
					});
			parent.add(enumeratedPropertyButtonPanel);
		}

		if (property instanceof NamedEnumeratedPropertyCollection) {
			GPopupMenuW fontSizeMenu = widgetAdapter.getMenuWidget(
					(NamedEnumeratedPropertyCollection<?, ?>) property);
			parent.add(fontSizeMenu.getPopupMenu());
		}

		if (property instanceof ColorPropertyCollection<?>) {
			ColorPropertyCollection<?> colorProperty = (ColorPropertyCollection<?>) property;
			ColorChooserPanel colorPanel = new ColorChooserPanel(appW,
					colorProperty.getValues(), color -> {
				if (popupHandler != null) {
					ColorPropertyCollection<?> updatedProperty =
							(ColorPropertyCollection<?>) propertySupplier.updateAndGet();
					popupHandler.fireActionPerformed(updatedProperty, color);
				}
			});
			if (colorProperty.getFirstProperty() instanceof BorderColorProperty) {
				colorPanel.addStyleName("withMargin");
			}
			colorPanel.updateColorSelection(colorProperty.getFirstProperty().getValue());
			parent.add(colorPanel);

			if (colorProperty.getFirstProperty() instanceof NotesInlineBackgroundColorProperty) {
				StandardButton noColorButton = new StandardButton(MaterialDesignResources.INSTANCE
						.no_color(), appW.getLocalization().getMenu("noColor"), 24);
				noColorButton.addStyleName("noColBtn");
				noColorButton.addFastClickHandler(source -> {
					if (popupHandler != null) {
						popupHandler.fireActionPerformed(colorProperty, null);
					}
				});
				parent.add(noColorButton);
			}
		}
		if (property instanceof RangePropertyCollection<?>) {
			RangePropertyCollection<?> rangeProperty = (RangePropertyCollection<?>) property;
			RangeProperty<?> firstProperty = rangeProperty.getFirstProperty();
			if (firstProperty instanceof NotesThicknessProperty) {
				lineThicknessSlider = widgetAdapter.getSliderWidget(rangeProperty,
						propertySupplier, geo);
				parent.add(lineThicknessSlider);
			} else if (firstProperty instanceof CellBorderThicknessProperty
					|| firstProperty instanceof BorderThicknessProperty) {
				FlowPanel borderThickness = widgetAdapter.getBorderThicknessWidget(
						rangeProperty);
				parent.add(borderThickness);
			} else {
				SliderWithProperty sliderWithProperty = widgetAdapter.getSliderWidget(
						rangeProperty, propertySupplier, geo);
				parent.add(sliderWithProperty);
			}
		}

		if (property instanceof StringPropertyCollection<?>) {
			labelPanel = new LabelValuePanel(appW, (StringPropertyCollection<?>) property, geos);
			propertyPopup.addCloseHandler(labelPanel);
			parent.add(labelPanel);
		}

		if (property instanceof FlagListPropertyCollection<?>) {
			FlagListPropertyCollection<?> valuedProperty = (FlagListPropertyCollection<?>) property;
			ValuedProperty<?> firstProperty = valuedProperty.getFirstProperty();
			LabelSettingsPanel labelStylePanel = widgetAdapter.getLabelPanel(valuedProperty);
			if (firstProperty instanceof LabelStyleProperty && labelPanel != null) {
				//TODO labelPanel.setLabelStyleProperty(valuedProperty);
			}
			parent.add(labelStylePanel);
		}
	}

	private void initPropertyPopup() {
		if (propertyPopup == null) {
			propertyPopup = new GPopupPanel(false, appW.getAppletFrame(), appW);
			propertyPopup.setStyleName("quickStyleBarPopup");
		}
	}

	private void update() {
		if (lineThicknessSlider != null) {
			lineThicknessSlider.setLineColor(geos.get(0).getObjectColor());
		}
	}

	private void showPropertyPopup() {
		appW.closePopups();
		propertyPopup.show();
		propertyPopup.setPopupPosition((int) (getAbsoluteLeft() - appW.getAbsLeft()),
				(int) (getAbsoluteTop() + getOffsetHeight() - appW.getAbsTop())
						+ 2 * POPUP_MENU_DISTANCE);
		appW.registerPopup(propertyPopup);
	}

	/**
	 * @param popupHandler {@link PopupColorHandler}
	 */
	public void addPopupHandler(PopupColorHandler popupHandler) {
		this.popupHandler = popupHandler;
	}

	/**
	 * close popup of button if it doesn't contain sliders
	 */
	public void closePopup() {
		if (propertyPopup != null && Dom.querySelectorForElement(
				propertyPopup.getElement(), "[type=range]") == null) {
			propertyPopup.hide();
		}
	}
}
