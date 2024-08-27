package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.ThicknessPropertyDelegate;

/**
 * Line thickness
 */
public class NotesThicknessProperty extends ThicknessProperty {
	/***/
	public NotesThicknessProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, element, 60, new ThicknessPropertyDelegate(element));
	}
}
