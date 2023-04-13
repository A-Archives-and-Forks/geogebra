package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.toolbarpanel.tableview.StickyValuesTable;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Adds the scientific header to AV panel.
 */
public final class ScientificDockPanelDecorator implements DockPanelDecorator {

	// TODO to find out where is this come from.
	public static final int TAB_HEIGHT_DIFFERENCE = 40;
	public static final int TABLE_HEIGHT_DIFFERENCE = 64;
	private FlowPanel main;
	private Widget tableTab;
	private StickyValuesTable table;
	private Widget algebraTab;
	private AppW app;

	@Override
	public Panel decorate(Widget algebraTab, Panel wrapper, AppW appW) {
		this.algebraTab = algebraTab;
		this.app = appW;
		main = new FlowPanel();
		main.setWidth("100%");
		main.add(wrapper);
		main.addStyleName("algebraPanel");
		algebraTab.setStyleName("scientific");
		return buildAndStylePanel();
	}

	private Panel buildAndStylePanel() {
		FlowPanel panel = new FlowPanel();
		stylePanel(panel);
		panel.add(main);
		main.addStyleName("algebraPanelScientific");

		ScientificScrollHandler scrollController = new ScientificScrollHandler(
				app, panel);
		panel.addDomHandler(scrollController, MouseDownEvent.getType());
		panel.addBitlessDomHandler(scrollController, TouchStartEvent.getType());
		return panel;
	}

	private static void stylePanel(Panel panel) {
		panel.setHeight("100%");
	}

	@Override
	public void onResize(AlgebraViewW aView, int offsetHeight) {
		GeoGebraFrameW frame = app.getAppletFrame();
		toggleSmallScreen(main, frame.shouldHaveSmallScreenLayout());
	}

	private void toggleSmallScreen(Widget w, boolean smallScreen) {
		Dom.toggleClass(w, "algebraPanelScientificSmallScreen",
				"panelScientificDefaults", smallScreen);
		Dom.toggleClass(algebraTab, "scientific", !smallScreen);
	}

	@Override
	public void resizeTable(int tabHeight) {
		table.setHeight(getTableHeight(tabHeight));
		tableTab.setHeight((tabHeight + TAB_HEIGHT_DIFFERENCE) + "px");
		toggleSmallScreen(tableTab, false);
	}

	private int getTableHeight(int tableHeight) {
		return app.getAppletFrame().isKeyboardShowing()
				? getTabHeight(tableHeight)
				: tableHeight - TABLE_HEIGHT_DIFFERENCE;

	}

	@Override
	public void resizeTableSmallScreen(int tabHeight) {
		resizeTable(tabHeight);
		toggleSmallScreen(tableTab, true);
	}

	@Override
	public void decorateTableTab(Widget tab, StickyTable<?> table) {
		tableTab = tab;
		this.table = (StickyValuesTable) table;
		tab.addStyleName("panelScientificDefaults");
		disableShadedColumns(this.table);
		table.addStyleName("scientific");

		SimplePanel btnHolder = new SimplePanel();
		btnHolder.addStyleName("btnRow");

		StandardButton btn = new StandardButton(app.getLocalization()
				.getMenu("DefineFunctions"));
		btn.addStyleName("materialTextButton");
		btnHolder.add(btn);
		table.getElement().insertBefore(btnHolder.getElement(), table.getElement().getChild(0)) ;

		btn.addFastClickHandler((event) -> table.openDefineFunctions());
	}

	@Override
	public int getTabHeight(int tabHeight) {
		return tabHeight - TAB_HEIGHT_DIFFERENCE;
	}

	private void disableShadedColumns(StickyValuesTable table) {
		table.disableShadedColumns();
	}
}