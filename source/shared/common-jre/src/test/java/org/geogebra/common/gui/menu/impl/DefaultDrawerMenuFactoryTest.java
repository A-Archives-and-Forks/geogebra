package org.geogebra.common.gui.menu.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.in;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

public class DefaultDrawerMenuFactoryTest {

	private LogInOperation logInOperation;

	@Before
	public void setUp() {
		AuthenticationModel baseModel = Mockito.mock(
				AuthenticationModel.class, Answers.RETURNS_MOCKS);
		logInOperation = Mockito.mock(LogInOperation.class, Answers.CALLS_REAL_METHODS);
		logInOperation.setModel(baseModel);
	}

	@Test
	public void testGraphingWebLoggedOut() {
		Mockito.when(logInOperation.isLoggedIn()).thenReturn(false);
		DefaultDrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.WEB,
				GeoGebraConstants.Version.GRAPHING, logInOperation);
		assertBasicProperties(factory, 3, 8, 2, 1);
	}

	@Test
	public void testGraphingWebLoggedIn() {
		Mockito.when(logInOperation.isLoggedIn()).thenReturn(true);
		DefaultDrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.WEB,
				GeoGebraConstants.Version.GRAPHING, logInOperation);
		assertBasicProperties(factory, 3, 8, 2, 2);
	}

	@Test
	public void testScientificIosExam() {
		DefaultDrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.IOS,
				GeoGebraConstants.Version.SCIENTIFIC, null, true);
		assertBasicProperties(factory, 2, 2, 2);
	}

	@Test
	public void testGraphingIosExam() {
		DefaultDrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.IOS,
				GeoGebraConstants.Version.GRAPHING, null, true);
		assertBasicProperties(factory, 2, 5, 2);
	}

	@Test
	public void testGraphingAndroidLoggedOutExam() {
		Mockito.when(logInOperation.isLoggedIn()).thenReturn(false);
		DrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.ANDROID,
				GeoGebraConstants.Version.GRAPHING, logInOperation, true);
		assertBasicProperties(factory, 3, 6, 2, 1);
	}

	@Test
	public void testScientificWeb() {
		DrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.WEB,
				GeoGebraConstants.Version.SCIENTIFIC, null);
		assertBasicProperties(factory, 2, 1, 2);
	}

	private void assertBasicProperties(DrawerMenuFactory factory, int numberOfGroups,
			int... subgroupItemCounts) {
		DrawerMenu menu = factory.createDrawerMenu();
		assertNotNull(menu.getTitle());
		List<MenuItemGroup> groups = menu.getMenuItemGroups();
		assertEquals(numberOfGroups, groups.size());
		for (int i = 0; i < subgroupItemCounts.length; i++) {
			MenuItemGroup group = groups.get(i);
			List<MenuItem> menuItems = group.getMenuItems();
			assertEquals(subgroupItemCounts[i], menuItems.size());
			for (MenuItem menuItem : menuItems) {
				assertNotNull(menuItem.getIcon());
				assertNotNull(menuItem.getLabel());
			}
		}
	}

	@Test
	public void testEnableFileFeatureDisabled() {
		Action[] fileFeatureEnabledActions = {
				Action.CLEAR_CONSTRUCTION, Action.SHOW_SEARCH_VIEW,
				Action.SAVE_FILE, Action.SHARE_FILE, Action.SIGN_IN, Action.SIGN_OUT};
		DrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.WEB,
				GeoGebraConstants.Version.GRAPHING, null, null, false, false, true);
		DrawerMenu menu = factory.createDrawerMenu();

		for (MenuItemGroup menuItemGroup : menu.getMenuItemGroups()) {
			List<MenuItem> menuItems = menuItemGroup.getMenuItems();
			assertThat(menuItems, not(CoreMatchers.<MenuItem>hasItem(
					hasProperty("action", is(in(fileFeatureEnabledActions))))));
		}
	}

	@Test
	public void testSwitchCalculator() {
		DrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.IOS,
				GeoGebraConstants.Version.SUITE, null, null, false, false, true, false);
		DrawerMenu menu = factory.createDrawerMenu();
		MenuItemGroup group = menu.getMenuItemGroups().get(1);

		// Contains Switch calculator above the settings item
		assertThat(group.getMenuItems(),
				containsInRelativeOrder(
						hasProperty("action", is(Action.SWITCH_CALCULATOR)),
						hasProperty("action", is(Action.SHOW_SETTINGS))));
	}

	@Test
	public void testCalculatorHasHelpAndFeedback() {
		DrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.WEB,
				GeoGebraConstants.Version.SUITE, null, null, false, false, true, true);
		DrawerMenu menu = factory.createDrawerMenu();
		MenuItem item = menu.getMenuItemGroups().get(1).getMenuItems().get(2);

		assertEquals(Icon.HELP, item.getIcon());
		assertEquals("HelpAndFeedback", item.getLabel());
	}

	@Test
	public void testPartnerHasNoHelpAndFeedback() {
		Action[] helpAndFeedbackActions = { Action.SHOW_TUTORIALS,
				Action.SHOW_FORUM, Action.REPORT_PROBLEM, Action.SHOW_LICENSE};
		DrawerMenuFactory factory = new DefaultDrawerMenuFactory(
				GeoGebraConstants.Platform.WEB,
				GeoGebraConstants.Version.SUITE, null, null, false, false, true, false);
		DrawerMenu menu = factory.createDrawerMenu();

		for (MenuItemGroup menuItemGroup : menu.getMenuItemGroups()) {
			List<MenuItem> menuItems = menuItemGroup.getMenuItems();
			assertThat(menuItems, not(CoreMatchers.hasItem(
					hasProperty("action", is(in(helpAndFeedbackActions))))));
		}
	}
}
