package org.geogebra.web.full.gui.toolbarpanel.tableview;

import java.util.List;

import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.ContextMenuTV;
import org.geogebra.web.full.gui.toolbarpanel.TVRowData;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.StickyTable;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Sticky table of values.
 *
 * @author laszlo
 *
 */
public class StickyValuesTable extends StickyTable<TVRowData> implements TableValuesListener {

	private final TableValuesModel tableModel;
	private final TableValuesView view;
	private final AppW app;
	private final HeaderCell headerCell = new HeaderCell();
	private boolean transitioning;

	private static class HeaderCell {
		private final String value;

		/**
		 * Header
		 */
		HeaderCell() {
			FlowPanel p = new FlowPanel();
			p.add(new Label("%s"));
			MyToggleButtonW btn = new MyToggleButtonW(
					new NoDragImage(MaterialDesignResources.INSTANCE.more_vert_black(), 24));
			TestHarness.setAttr(btn, "btn_tvHeader3dot");
			p.add(btn);
			value = p.getElement().getInnerHTML();
		}

		/**
		 * @param content
		 *            cell text content
		 * @return cell HTML markup
		 *
		 */
		SafeHtmlHeader getHtmlHeader(String content) {
			String stringHtmlContent = value.replace("%s", content);
			SafeHtml safeHtmlContent = SafeHtmlUtils.fromTrustedString(stringHtmlContent);
			return new SafeHtmlHeader(makeCell(safeHtmlContent));
		}
	}

	/**
	 * @param app  {@link AppW}
	 * @param view to feed table with data.
	 */
	public StickyValuesTable(AppW app, TableValuesView view) {
		this.app = app;
		this.view = view;
		this.tableModel = view.getTableValuesModel();
		tableModel.registerListener(this);
		reset();
		addCellClickHandler((row, column, el) -> {
			if (el != null && (el.hasClassName("MyToggleButton") || el.getParentNode() != null
					&& el.getParentElement().hasClassName("MyToggleButton"))) {
				onHeaderClick(el, column);
			} else if (row < tableModel.getRowCount()
					&& column < tableModel.getColumnCount()) {
				tableModel.setCell(row, column);
			} else if (column == tableModel.getColumnCount()) {
				// do nothing now, start editing empty column in follow up ticket
			} else if (row == tableModel.getRowCount()) {
				// do nothing now, start editing empty row in follow up ticket
			}
		});
	}

	private void onHeaderClick(Element source, int column) {
		new ContextMenuTV(app, view, view.getGeoAt(column), column)
				.show(source.getAbsoluteLeft(), source.getAbsoluteTop() - 8);
	}

	@Override
	protected void addCells() {
		for (int column = 0; column < tableModel.getColumnCount(); column++) {
			addColumn(column);
		}
		addEmptyColumn();
		addEmptyColumn();
	}

	private void addEmptyColumn() {
		Column<TVRowData, SafeHtml> col = new Column<TVRowData, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(TVRowData object) {
				return makeCell(() -> "");
			}
		};

		getTable().addColumn(col, new SafeHtmlHeader(makeCell(() -> "")));
	}

	@Override
	protected void addColumn() {
		addColumn(tableModel.getColumnCount() - 1);
	}

	private void addColumn(int column) {
		Column<TVRowData, ?> colValue = getColumnValue(column);
		getTable().addColumn(colValue, getHeaderFor(column));
	}

	private Header<SafeHtml> getHeaderFor(int columnIndex) {
		String content = tableModel.getHeaderAt(columnIndex);
		return headerCell.getHtmlHeader(content);
	}

	@Override
	protected void fillValues(List<TVRowData> rows) {
		rows.clear();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			rows.add(new TVRowData(row, tableModel));
		}
		rows.add(new TVRowData(tableModel.getRowCount(), tableModel));
		rows.add(new TVRowData(tableModel.getRowCount(), tableModel));
	}

	/**
	 * Makes a cell as SafeHtml.
	 *
	 * @param content
	 *            of the cell.
	 * @return SafeHtml of the cell.
	 */
	static SafeHtml makeCell(SafeHtml content) {
		return () -> "<div class=\"content\">" + content.asString() + "</div>";
	}

	private static Column<TVRowData, SafeHtml> getColumnValue(final int col) {
		return new Column<TVRowData, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(TVRowData object) {
				String valStr = object.getValue(col);
				SafeHtml value = SafeHtmlUtils.fromSafeConstant(valStr);
				return makeCell(value);
			}
		};
	}

	/**
	 * Deletes the specified column from the table
	 *
	 * @param column
	 *            column to delete.
	 */
	public void deleteColumn(int column) {
		if (transitioning) {
			// multiple simultaneous deletions
			reset();
			return;
		}
		NodeList<Element> elems = getColumnElements(column);
		Element header = getHeaderElement(column);

		if (elems == null || elems.getLength() == 0 || header == null) {
			decreaseColumnNumber();
			return;
		}
		transitioning = true;

		header.addClassName("delete");

		CSSEvents.runOnTransition(this::onDeleteColumn, header, "delete");

		for (int i = 0; i < elems.getLength(); i++) {
			Element e = elems.getItem(i);
			e.addClassName("delete");
		}
	}

	@Override
	protected void reset() {
		super.reset();
		transitioning = false;
	}

	/**
	 * Runs on column delete.
	 */
	void onDeleteColumn() {
		transitioning = false;
		if (!isLastColumnDeleted()) {
			decreaseColumnNumber();
		}
	}

	private boolean isLastColumnDeleted() {
		return view.getTableValuesModel().getColumnCount() == 0;
	}

	/**
	 * Sets height of the values to be able to scroll.
	 *
	 * @param height
	 *            to set.
	 */
	public void setHeight(int height) {
		setBodyHeight(height);
	}

	/**
	 *
	 * Scroll table view to the corresponding column of the geo.
	 *
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		if (geo == null) {
			return;
		}

		int col = view.getColumn(geo);
		setHorizontalScrollPosition(getHeaderElement(col).getAbsoluteLeft());
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model,
			GeoEvaluatable evaluatable, int column) {
		deleteColumn(column);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		//
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		onColumnAdded();
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		refresh();
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		reset();
	}
}
