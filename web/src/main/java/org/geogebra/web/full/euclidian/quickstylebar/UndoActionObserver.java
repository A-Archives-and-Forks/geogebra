package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.undo.UpdateStyleActionStore;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

public class UndoActionObserver
		implements PropertyValueObserver<Object> {

	private final List<GeoElement> geos;
	private UpdateStyleActionStore store;
	private boolean batchMode;

	public UndoActionObserver(List<GeoElement> geos) {
		this.geos = geos;
	}

	@Override
	public void onBeginSetValue(ValuedProperty<Object> property) {
		batchMode = true;
	}

	@Override
	public void onWillSetValue(ValuedProperty<Object> property) {
		if (store == null) {
			store = new UpdateStyleActionStore(geos,
					geos.get(0).getConstruction().getUndoManager());
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
