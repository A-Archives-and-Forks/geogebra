package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.ActionType;

import com.google.j2objc.annotations.Weak;

public class UpdateActionStore {
	private final List<UndoItem> undoItems = new ArrayList<>();
	@Weak
	protected final SelectionManager selection;
	private final UndoManager undoManager;

	public UpdateActionStore(SelectionManager selection, UndoManager undoManager) {
		this.selection = selection;
		this.undoManager = undoManager;
	}

	void store(List<GeoElement> geos) {
		clear();
		for (GeoElement geo: geos) {
			undoItems.add(new UndoItem(geo));
		}
	}


	public void storeSelection() {
		if (undoItems.isEmpty()) {
			store(selection.getSelectedGeos());
		}
	}

	public void clear() {
		undoItems.clear();
	}


	public void storeUpdateAction() {
		List<String> actions = new ArrayList<>(undoItems.size());
		List<String> undoActions = new ArrayList<>(undoItems.size());
		for (UndoItem item: undoItems) {
			actions.add(item.content());
			undoActions.add(item.previousContent());
		}

		undoManager.storeUndoableAction(ActionType.UPDATE, actions.toArray(new String[0]),
						ActionType.UPDATE, undoActions.toArray(new String[0]));
	}

	public boolean storeUndo() {
		if (!undoItems.isEmpty()) {
			storeUpdateAction();
		}
		return undoItems.isEmpty();
	}
}
