package org.geogebra.web.full.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.undo.UpdateContentActionStore;

public class InlineTextFormatter {

	private UpdateContentActionStore store;

	/**
	 * @param targetGeos
	 *            geos to be formatter (non-texts are ignored)
	 * @param key
	 *            option name
	 * @param val
	 *            option value
	 * @return whether format changed
	 */
	public boolean formatInlineText(List<GeoElement> targetGeos, String key, Object val) {
		return formatInlineText(targetGeos, formatter -> {
			formatter.format(key, val);
			return true;
		});
	}

	/**
	 * @param targetGeos
	 *            geos to be formatted (non-texts are ignored)
	 * @param formatFn
	 *            formatting function
	 * @return whether format changed
	 */
	public boolean formatInlineText(List<GeoElement> targetGeos,
			Function<HasTextFormat, Boolean> formatFn) {
		boolean changed = false;
		ArrayList<GeoInline> geosToStore = new ArrayList<>();
		for (GeoElement geo : targetGeos) {
			if (geo instanceof HasTextFormatter) {
				geosToStore.add((GeoInline) geo);
			}
		}
		if (!geosToStore.isEmpty()) {
			store = new UpdateContentActionStore(geosToStore);
		}

		for (GeoElement geo : targetGeos) {
			if (geo instanceof HasTextFormatter) {
				changed = formatFn.apply(((HasTextFormatter) geo).getFormatter()) || changed;
			}
		}

		if (changed && store.needUndo() && !geosToStore.isEmpty()) {
			store.storeUndo();
		}

		return changed;
	}
}
