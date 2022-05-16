package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CustomScrollbar;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;

/**
 * Tab of Table Values View.
 * 
 * @author laszlo
 */
public class TableTab extends ToolbarPanel.ToolbarTab {

	private final StickyValuesTable table;
	private final ToolbarPanel toolbarPanel;
	private ComponentInfoErrorPanel emptyPanel;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		AppW app = toolbarPanel.getApp();
		TableValuesView view = (TableValuesView) app.getGuiManager().getTableValuesView();
		this.table = new StickyValuesTable(app, view);
		TestHarness.setAttr(table, "TV_table");
		table.setStyleName("tvTable", true);
		CustomScrollbar.apply(this);
		buildEmptyTablePanel();
	}

	private void buildEmptyTablePanel() {
		InfoErrorData data = new InfoErrorData("TableValuesEmptyTitle",
				"TableValuesEmptyDescription");
		emptyPanel = new ComponentInfoErrorPanel(toolbarPanel.getApp().getLocalization(),
				data, MaterialDesignResources.INSTANCE.toolbar_table_view_black(), null);
	}

	@Override
	protected void onActive() {
		ProbabilityCalculatorViewW view = (ProbabilityCalculatorViewW) toolbarPanel.getApp()
				.getGuiManager().getProbabilityCalculator();
		if (view != null && view.hasTableView()) {
			setWidget(table);
			table.setHeight(toolbarPanel.getTabHeight());
		} else {
			setWidget(emptyPanel);
		}
	}

	@Override
	public void setLabels() {
		// nothing to do
	}

	@Override
	public void open() {
		toolbarPanel.openTableView(true);
	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}

	@Override
	public void onResize() {
		int w = this.toolbarPanel.getTabWidth();
		int h = toolbarPanel.getTabHeight();
		if (w < 0 || h < 0) {
			return;
		}

		setWidth(w + "px");
		setHeight(h + "px");
		table.setHeight(h);
	}

	/**
	 * Scroll table view to the corresponding column of the geo.
	 * 
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		table.scrollTo(geo);
	}

	public MathKeyboardListener getKeyboardListener() {
		return table.getKeyboardListener();
	}
}
