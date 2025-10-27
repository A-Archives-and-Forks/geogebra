package org.geogebra.common.properties;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.graphics.AxisDistanceProperty;
import org.geogebra.common.properties.impl.graphics.GridDistanceProperty;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PropertyViewTests extends BaseAppTestSetup {
	@BeforeAll
	public static void enablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
	}

	@AfterAll
	public static void disabledPreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Test
	public void testGridTypeDependentLineStyleVisibility() {
		setupApp(SuiteSubApp.GRAPHING);

		PropertiesArray graphicsProperties = getApp().getConfig().createPropertiesFactory()
				.createProperties(getApp(), getLocalization(), GlobalScope.propertiesRegistry)
				.get(2);
		PropertyView.ExpandableList gridPropertyView = (PropertyView.ExpandableList)
				PropertyViewFactory.propertyViewListOf(graphicsProperties).get(1);
		PropertyView.SingleSelectionIconRow gridTypeSelector = (PropertyView.SingleSelectionIconRow)
				gridPropertyView.getItems().get(0);
		PropertyView.SingleSelectionIconRow
				lineStyleSelector = (PropertyView.SingleSelectionIconRow)
				gridPropertyView.getItems().get(2);

		assertNotEquals(PropertyResource.ICON_DOTS,
				gridTypeSelector.getIcons().get(gridTypeSelector.getSelectedIconIndex()));
		assertTrue(lineStyleSelector.isVisible());

		gridTypeSelector.setSelectedIconIndex(4);

		assertEquals(PropertyResource.ICON_DOTS,
				gridTypeSelector.getIcons().get(gridTypeSelector.getSelectedIconIndex()));
		assertFalse(lineStyleSelector.isVisible());
	}

	@Test
	public void testFixedDistanceDependentComboBoxEnabledState() {
		setupApp(SuiteSubApp.GRAPHING);

		GridDistancePropertyCollection gridDistancePropertyCollection =
				new GridDistancePropertyCollection(getApp(), getLocalization(),
						getEuclidianSettings(), getEuclidianView());
		PropertyView.RelatedPropertyViewCollection relatedPropertyViewCollection = (PropertyView
				.RelatedPropertyViewCollection) PropertyView.of(gridDistancePropertyCollection);
		PropertyView.Checkbox fixedDistanceCheckbox = (PropertyView.Checkbox)
				relatedPropertyViewCollection.getPropertyViews().get(0);
		PropertyView.ComboBoxRow gridDistanceComboBoxRow = (PropertyView.ComboBoxRow)
				relatedPropertyViewCollection.getPropertyViews().get(1);
		PropertyView.ComboBox xGridDistanceComboBox = gridDistanceComboBoxRow.getLeadingComboBox();
		PropertyView.ComboBox yGridDistanceComboBox = gridDistanceComboBoxRow.getTrailingComboBox();

		assertFalse(fixedDistanceCheckbox.isSelected());
		assertFalse(xGridDistanceComboBox.isEnabled());
		assertFalse(yGridDistanceComboBox.isEnabled());

		fixedDistanceCheckbox.setSelected(true);

		assertTrue(fixedDistanceCheckbox.isSelected());
		assertTrue(xGridDistanceComboBox.isEnabled());
		assertTrue(yGridDistanceComboBox.isEnabled());
	}

	@Test
	public void testAxisDistanceTextFieldInputValidation() {
		setupApp(SuiteSubApp.GRAPHING);

		AxisDistanceProperty axisDistanceProperty = new AxisDistanceProperty(getLocalization(),
				getEuclidianSettings(), getEuclidianView(), getKernel(), "xAxis", 0);
		getEuclidianSettings().setAutomaticAxesNumberingDistance(false, 0, true);
		PropertyView.ComboBox axisDistanceComboBox =
				(PropertyView.ComboBox) PropertyView.of(axisDistanceProperty);

		assertNull(axisDistanceComboBox.getErrorMessage());
		axisDistanceComboBox.setValue("");
		assertEquals("Please check your input", axisDistanceComboBox.getErrorMessage());
		axisDistanceComboBox.setValue("1");
		assertNull(axisDistanceComboBox.getErrorMessage());
		axisDistanceComboBox.setValue("1/");
		assertEquals("Please check your input", axisDistanceComboBox.getErrorMessage());
		axisDistanceComboBox.setValue("1/2");
		assertNull(axisDistanceComboBox.getErrorMessage());
	}

	@Test
	public void testOrdinalPositionsOfExpandableListsInGraphicsSettings() {
		setupApp(SuiteSubApp.GRAPHING);

		PropertiesArray graphicsProperties = getApp().getConfig().createPropertiesFactory()
				.createProperties(getApp(), getLocalization(), GlobalScope.propertiesRegistry)
				.get(2);
		List<PropertyView> graphicsPropertyViews =
				PropertyViewFactory.propertyViewListOf(graphicsProperties);

		assertFalse(graphicsPropertyViews.get(0) instanceof PropertyView.ExpandableList);
		assertInstanceOf(PropertyView.ExpandableList.class, graphicsPropertyViews.get(1));
		assertEquals(PropertyView.ExpandableList.OrdinalPosition.First,
				((PropertyView.ExpandableList) graphicsPropertyViews.get(1)).ordinalPosition);
		assertEquals(PropertyView.ExpandableList.OrdinalPosition.InBetween,
				((PropertyView.ExpandableList) graphicsPropertyViews.get(2)).ordinalPosition);
		assertInstanceOf(PropertyView.ExpandableList.class,
				graphicsPropertyViews.get(graphicsPropertyViews.size() - 1));
		assertEquals(PropertyView.ExpandableList.OrdinalPosition.Last,
				((PropertyView.ExpandableList) graphicsPropertyViews.get(
						graphicsPropertyViews.size() - 1)).ordinalPosition);
	}

	@Test
	public void testCheckboxConfigurationUpdate() {
		setupApp(SuiteSubApp.GRAPHING);

		GridVisibilityProperty gridVisibilityProperty = new GridVisibilityProperty(
				getLocalization(), getEuclidianSettings());
		PropertyView.Checkbox gridVisibilityCheckbox = (PropertyView.Checkbox)
				PropertyView.of(gridVisibilityProperty);
		AtomicInteger visibilityUpdatedCount = new AtomicInteger();
		gridVisibilityCheckbox.setConfigurationUpdateDelegate(
				() -> visibilityUpdatedCount.addAndGet(1));

		assertTrue(gridVisibilityCheckbox.isSelected());
		gridVisibilityCheckbox.setSelected(false);
		assertFalse(gridVisibilityCheckbox.isSelected());
		assertEquals(1, visibilityUpdatedCount.get());
		gridVisibilityCheckbox.setSelected(true);
		assertTrue(gridVisibilityCheckbox.isSelected());
		assertEquals(2, visibilityUpdatedCount.get());
	}

	@Test
	public void testComboBoxRowVisibilityListenersForEachPropertyViewInTheTree() {
		setupApp(SuiteSubApp.GRAPHING);

		GridDistanceProperty leadingComboBoxProperty = new GridDistanceProperty(
				getAlgebraProcessor(), getLocalization(), getEuclidianView(), "x", 0);
		GridDistanceProperty trailingComboBoxProperty = new GridDistanceProperty(
				getAlgebraProcessor(), getLocalization(), getEuclidianView(), "y", 0);
		PropertyView.ComboBoxRow comboBoxRow = new PropertyView.ComboBoxRow(
				leadingComboBoxProperty, trailingComboBoxProperty);

		AtomicBoolean leadingComboBoxVisibilityListenerCalled = new AtomicBoolean(false);
		comboBoxRow.getLeadingComboBox().setVisibilityUpdateDelegate(() ->
				leadingComboBoxVisibilityListenerCalled.set(true));

		AtomicBoolean trailingComboBoxVisibilityListenerCalled = new AtomicBoolean(false);
		comboBoxRow.getTrailingComboBox().setVisibilityUpdateDelegate(() ->
				trailingComboBoxVisibilityListenerCalled.set(true));

		AtomicBoolean comboBoxRowVisibilityListenerCalled = new AtomicBoolean(false);
		comboBoxRow.setVisibilityUpdateDelegate(() ->
				comboBoxRowVisibilityListenerCalled.set(true));

		getEuclidianSettings().setGridType(EuclidianView.GRID_POLAR);

		assertAll(() -> assertTrue(comboBoxRowVisibilityListenerCalled.get()),
				() -> assertTrue(leadingComboBoxVisibilityListenerCalled.get()),
				() -> assertTrue(trailingComboBoxVisibilityListenerCalled.get()));
	}

	@Test
	public void testSingleComboBoxRowVisibilityListener() {
		setupApp(SuiteSubApp.GRAPHING);

		GridDistanceProperty leadingComboBoxProperty = new GridDistanceProperty(
				getAlgebraProcessor(), getLocalization(), getEuclidianView(), "x", 0);
		GridDistanceProperty trailingComboBoxProperty = new GridDistanceProperty(
				getAlgebraProcessor(), getLocalization(), getEuclidianView(), "y", 0);
		PropertyView.ComboBoxRow comboBoxRow = new PropertyView.ComboBoxRow(
				leadingComboBoxProperty, trailingComboBoxProperty);

		AtomicBoolean comboBoxRowVisibilityListenerCalled = new AtomicBoolean(false);
		comboBoxRow.setVisibilityUpdateDelegate(() ->
				comboBoxRowVisibilityListenerCalled.set(true));

		getEuclidianSettings().setGridType(EuclidianView.GRID_POLAR);

		assertTrue(comboBoxRowVisibilityListenerCalled.get());
	}

	private Localization getLocalization() {
		return getApp().getLocalization();
	}

	private EuclidianView getEuclidianView() {
		return getApp().getActiveEuclidianView();
	}

	private EuclidianSettings getEuclidianSettings() {
		return getApp().getActiveEuclidianView().getSettings();
	}
}
