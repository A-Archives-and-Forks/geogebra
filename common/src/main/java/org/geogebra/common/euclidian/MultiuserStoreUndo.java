package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.ActionType;

import com.google.j2objc.annotations.Weak;

public class MultiuserStoreUndo {
	private final HashMap<GeoElement, String> oldDefinition = new HashMap<>();
	@Weak
	protected final SelectionManager selection;
	private final UndoManager undoManager;

	public MultiuserStoreUndo(SelectionManager selection, UndoManager undoManager) {
		this.selection = selection;
		this.undoManager = undoManager;
	}

	void asDefinitions(ArrayList<GeoElement> moveMultipleObjectsList) {
		if (moveMultipleObjectsList.stream().anyMatch(
				g -> g instanceof Locateable || g instanceof GeoWidget)) {
			// moving buttons/images etc cannot use definition -> fall back to XML undo
			return;
		}
		for (GeoElement geo: moveMultipleObjectsList) {
			oldDefinition.put(geo, getDefintion(geo));
		}
	}

	private String getDefintion(GeoElement movedGeoElement) {
		return movedGeoElement.getLabelSimple() + ":"
				+ movedGeoElement.getRedefineString(false, true, StringTemplate.xmlTemplate);
	}

	public void storeDefinitions() {
		if (oldDefinition.isEmpty()) {
			asDefinitions(selection.getSelectedGeos());
		}
	}

	public void clear() {
		oldDefinition.clear();
	}


	public void storeUpdateAction() {
		List<String> actions = new ArrayList<>(oldDefinition.size());
		List<String> undoActions = new ArrayList<>(oldDefinition.size());
		for (Map.Entry<GeoElement, String> entry: oldDefinition.entrySet()) {
			actions.add(getDefintion(entry.getKey()));
			undoActions.add(entry.getValue());
		}

		undoManager.storeUndoableAction(ActionType.UPDATE, actions.toArray(new String[0]),
						ActionType.UPDATE, undoActions.toArray(new String[0]));
	}

	public boolean storeUndo() {
		if (!oldDefinition.isEmpty()) {
			storeUpdateAction();
		}
		return oldDefinition.isEmpty();
	}
}
