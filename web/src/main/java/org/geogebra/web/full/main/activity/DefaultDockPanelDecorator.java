package org.geogebra.web.full.main.activity;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.Widget;

public class DefaultDockPanelDecorator implements DockPanelDecorator {
	private StickyTable<?> table;

	@Override
	public Panel decorate(Widget algebraTab, Panel panel, AppW app) {
		return panel;
	}

	@Override
	public void onResize(AlgebraViewW aview, int offsetHeight) {
		// nothing to do.
	}

	@Override
	public void decorateTableTab(Widget tab, StickyTable<?> table) {
		this.table = table;
		tab.getElement().getFirstChildElement().getStyle().setHeight(100, Style.Unit.PCT);
	}

	@Override
	public int getTabHeight(int tabHeight) {
		return tabHeight;
	}

	@Override
	public void resizeTable(int tabHeight) {
		table.setHeight(tabHeight);
	}

	@Override
	public void resizeTableSmallScreen(int tabHeight) {
		resizeTable(tabHeight);
	}

}
