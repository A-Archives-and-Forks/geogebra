package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.main.undo.ConstructionActionExecutor;

class UndoItem {
	private final String previousContent;
	private final GeoElement geo;
	private final boolean isXml;
	private final String previousLabel;

	public UndoItem(GeoElement geo, MoveMode moveMode) {
		this.geo = geo;
		isXml = (geo instanceof Locateable && moveMode != MoveMode.NUMERIC)
				|| geo instanceof GeoWidget || geo instanceof GeoInline;
		previousContent = content();
		previousLabel = geo.getLabelSimple();
	}

	private String getDefinition() {
		return geo.getLabelSimple() + ":"
				+ geo.getRedefineString(false, true, StringTemplate.xmlTemplate);
	}

	public String content() {
		if (geo.isPointOnPath() || geo.isPointInRegion()) {
			return ConstructionActionExecutor.SET + geo.getLabelSimple() + "="
					+ geo.toValueString(StringTemplate.xmlTemplate);
		}
		return isXml ? geo.getStyleXML() : getDefinition();
	}

	public String previousContent() {
		return previousContent;
	}

	public String getLabel() {
		return geo.getLabelSimple();
	}

	public boolean hasGeo(GeoElement geo) {
		return this.geo == geo;
	}

	public String getPreviousLabel() {
		return previousLabel;
	}
}
