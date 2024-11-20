package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.main.undo.UpdateContentActionStore;
import org.geogebra.common.main.undo.UpdateStyleActionStore;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

public class UndoActionObserver
		implements PropertyValueObserver<Object> {

	private final List<GeoElement> styleableGeos;
	private final UndoManager undoManager;
	private final List<GeoInline> inlines;
	private UpdateStyleActionStore store;
	private UpdateContentActionStore contentStore;
	private boolean batchMode;

	/**
	 * @param geos selected elements
	 * @param type undo action type
	 */
	public UndoActionObserver(List<GeoElement> geos, UndoActionType type) {
		this.undoManager = geos.get(0).getConstruction().getUndoManager();
		if (type == UndoActionType.STYLE_OR_CONTENT) {
			this.styleableGeos = skipTextObjects(geos);
			this.inlines = keepOnlyTextObjects(geos);
		} else {
			this.styleableGeos = geos;
			this.inlines = List.of();
		}
	}

	private static List<GeoElement> skipTextObjects(List<GeoElement> geos) {
		return geos.stream().filter(geo -> !(geo instanceof HasTextFormatter))
				.collect(Collectors.toList());
	}

	private static List<GeoInline> keepOnlyTextObjects(List<GeoElement> geos) {
		return geos.stream()
				.filter(geo1 -> geo1 instanceof HasTextFormatter)
				.map(geo -> (GeoInline) geo).collect(Collectors.toList());
	}

	@Override
	public void onBeginSetValue(ValuedProperty<Object> property) {
		batchMode = true;
	}

	@Override
	public void onWillSetValue(ValuedProperty<Object> property) {
		if (store == null && !styleableGeos.isEmpty()) {
			store = new UpdateStyleActionStore(styleableGeos, undoManager);
		}
		if (contentStore == null && !inlines.isEmpty()) {
			contentStore = new UpdateContentActionStore(inlines);
		}
	}

	@Override
	public void onEndSetValue(ValuedProperty<Object> property) {
		batchMode = false;
		storeUndoAndReset();
	}

	private void storeUndoAndReset() {
		if (store != null) {
			store.storeUndo();
		}
		if (contentStore != null) {
			contentStore.storeUndo();
		}
		store = null;
	}

	@Override
	public void onDidSetValue(ValuedProperty<Object> property) {
		if (batchMode) {
			return;
		}
		storeUndoAndReset();
	}
}
