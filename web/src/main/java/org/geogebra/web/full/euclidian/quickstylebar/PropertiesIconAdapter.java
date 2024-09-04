package org.geogebra.web.full.euclidian.quickstylebar;

import org.geogebra.common.properties.PropertyResource;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.resources.SVGResource;

public class PropertiesIconAdapter {

	/**
	 * Get icon of property
	 * @param propertyResource - property
	 * @return icon of property
	 */
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
		case ICON_FILLING_HATCHED:
			return res.pattern_hatching();
		case ICON_FILLING_DOTTED:
			return res.pattern_dots();
		case ICON_FILLING_CROSSHATCHED:
			return res.pattern_cross_hatching();
		case ICON_FILLING_HONEYCOMB:
			return res.pattern_honeycomb();
		case ICON_NO_FILLING:
			return res.no_pattern();
		case ICON_ALIGNMENT_LEFT:
			return res.horizontal_align_left();
		case ICON_ALIGNMENT_CENTER:
			return res.horizontal_align_center();
		case ICON_ALIGNMENT_RIGHT:
			return res.horizontal_align_right();
		}
		return res.stylebar_empty();
	}

}
