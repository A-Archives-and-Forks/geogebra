package org.geogebra.web.full.gui.toolbarpanel.tableview;

import java.util.function.Supplier;

import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarTab;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.full.util.CustomScrollbar;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;

/**
 * Tab of Table Values View.
 * 
 * @author laszlo
 */
public class TableTab extends ToolbarTab {

	private final StickyTable<?> table;
	private final ToolbarPanel toolbarPanel;
	private final AppW app;
	private ComponentInfoErrorPanel emptyPanel;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel, StickyTable<?> table) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		this.table = table;
		this.app = toolbarPanel.getApp();
		TestHarness.setAttr(table, "TV_table");
		table.setStyleName("tvTable", true);
		decorate();
		CustomScrollbar.apply(this);
		buildEmptyTablePanel();
	}

	private void decorate() {
		toolbarPanel.getDecorator().decorateTableTab(this, table);
	}

	private void buildEmptyTablePanel() {
		InfoErrorData data = new InfoErrorData("TableValuesEmptyTitle",
				"TableDiscreteDistribution");
		emptyPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				data, MaterialDesignResources.INSTANCE.toolbar_table_view_black(), null);
	}

	@Override
	protected void onActive() {
		if (app.getConfig().hasDistributionView()
				&& isEmptyProbabilityTable()) {
			setWidget(emptyPanel);
		} else  {
			setWidget(table);
			table.setHeight(getTabHeight());
		}
	}

	private int getTabHeight() {
		return toolbarPanel.getDecorator().getTabHeight(toolbarPanel.getTabHeight());
	}

	private boolean isEmptyProbabilityTable() {
		if (app.getConfig().hasDistributionView()) {
			ProbabilityCalculatorViewW view = (ProbabilityCalculatorViewW) app
					.getGuiManager().getProbabilityCalculator();
			return !view.hasTableView();
		}
		return true;
	}

	@Override
	public void setLabels() {
		if (emptyPanel != null) {
			buildEmptyTablePanel();
		}
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
		resizeTable(h);
	}

	private void resizeTable(int tabHeight) {
		boolean smallScreen = app.getAppletFrame()
				.shouldHaveSmallScreenLayout();
		DockPanelDecorator decorator = toolbarPanel.getDecorator();
		if (smallScreen) {
			decorator.resizeTable(table, tabHeight);
		} else {
			decorator.resizeTableSmallScreen(table, tabHeight);
		}
	}

	/**
	 * Scroll table view to the corresponding column of the geo.
	 * 
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		if (table instanceof StickyValuesTable) {
			((StickyValuesTable) table).scrollTo(geo);
		}
	}

	/**
	 * @param fallback fallback
	 * @return keyboard listener if editable, fallback otherwise
	 */
	public MathKeyboardListener getKeyboardListener(Supplier<MathKeyboardListener> fallback) {
		if (table instanceof StickyValuesTable) {
			return ((StickyValuesTable) table).getKeyboardListener();
		}
		return fallback.get();
	}
}
