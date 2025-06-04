package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.ActionType;

import com.google.j2objc.annotations.Weak;

public class UpdateActionStore {
	private final List<UndoItem> undoItems = new ArrayList<>();

	@Weak
	protected final SelectionManager selection;
	private final UndoManager undoManager;

	/**
	 * Constructor
	 * @param selection {@link SelectionManager}
	 * @param undoManager {@link UndoManager}
	 */
	public UpdateActionStore(SelectionManager selection, UndoManager undoManager) {
		this.selection = selection;
		this.undoManager = undoManager;
	}

	/**
	 * Store selected geo to items.
	 * @param moveMode active move mode
	 */
	public void storeSelection(MoveMode moveMode) {
		if (undoItems.isEmpty()) {
			storeItems(moveMode);
		}
	}

	private void storeItems(MoveMode defaultMode) {
		for (GeoElement geo : selection.getSelectedGeos()) {
			if (geo.hasChangeableParent3D()) {
				GeoNumeric num = geo.getChangeableParent3D().getNumber();
				if (num.isLabelSet()) {
					undoItems.add(new UndoItem(num, MoveMode.NUMERIC));
				} else {
					undoItems.add(new UndoItem(geo.getChangeableParent3D().getSurface(),
							defaultMode));
				}
				continue;
			}
			if (geo.getParentAlgorithm() != null
					&& !geo.isPointOnPath() && !geo.isPointInRegion()) {
				addAll(geo.getParentAlgorithm().getDefinedAndLabeledInput(), defaultMode);
			} else if (geo instanceof GeoImage) {
				addAll(((GeoImage) geo).getDefinedAndLabeledStartPoints(), defaultMode);
			}
			undoItems.add(new UndoItem(geo, defaultMode));
		}
	}

	private void addAll(List<? extends GeoElement> geos, MoveMode mode) {
		geos.forEach(geo -> undoItems.add(new UndoItem(geo, mode)));
	}

	/**
	 * Add a single element if not already present
	 * @param geo element to add
	 * @param mode move mode
	 */
	public void addIfNotPresent(GeoElement geo, MoveMode mode) {
		if (undoItems.stream().noneMatch(it -> it.hasGeo(geo))) {
			undoItems.add(new UndoItem(geo, mode));
		}
	}

	/**
	 * Remove all items related to given element.
	 * @param geo element
	 */
	public void remove(GeoElement geo) {
		undoItems.removeIf(it -> it.hasGeo(geo));
	}

	/**
	 * Clear all items.
	 */
	public void clear() {
		undoItems.clear();
	}

	/**
	 * Builds actions from items and stores it in UndoManager
	 */
	public void storeUpdateAction() {
		List<String> actions = new ArrayList<>(undoItems.size());
		List<String> undoActions = new ArrayList<>(undoItems.size());
		List<String> labels = new ArrayList<>(undoItems.size());
		for (UndoItem item: undoItems) {
			actions.add(item.content());
			undoActions.add(item.previousContent());
			labels.add(item.getLabel());
		}
		undoManager.buildAction(ActionType.UPDATE, actions.toArray(new String[0]))
				.withUndo(ActionType.UPDATE, undoActions.toArray(new String[0]))
				.withLabels(labels.toArray(new String[0]))
				.storeAndNotifyUnsaved();
	}

	/**
	 * Store undo
	 * @return if there is items in undo list.
	 */
	public boolean storeUndo() {
		if (!undoItems.isEmpty()) {
			storeUpdateAction();
		}
		return undoItems.isEmpty();
	}

	/**
	 * Store undo
	 * @return if there is items in undo list.
	 */
	public boolean isEmpty() {
		return undoItems.isEmpty();
	}
}
