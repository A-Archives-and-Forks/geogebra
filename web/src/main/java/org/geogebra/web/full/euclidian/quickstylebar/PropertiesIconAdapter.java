package org.geogebra.web.full.euclidian.quickstylebar;

import org.geogebra.common.properties.PropertyResource;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.resources.SVGResource;

public class PropertiesIconAdapter {

	public static SVGResource getIcon(PropertyResource propertyResource) {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;
		switch (propertyResource) {
		case ICON_LINE_TYPE_FULL:
			return res.line_solid();
		case ICON_LINE_TYPE_DASHED_DOTTED:
			return res.line_dash_dot();
		case ICON_LINE_TYPE_DASHED_LONG:
			return res.line_dashed_long();
		case ICON_LINE_TYPE_DOTTED:
			return res.line_dotted();
		case ICON_LINE_TYPE_DASHED_SHORT:
			return res.line_dashed_short();
		}
		return res.stylebar_empty();
	}


}
