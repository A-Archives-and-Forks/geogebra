package org.geogebra.common.properties.impl.objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NameCaptionPropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Name
 */
public class NameCaptionProperty extends AbstractValuedProperty<String> implements StringProperty {

	private final GeoElementDelegate delegate;

	/***/
	public NameCaptionProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Name");
		delegate = new NameCaptionPropertyDelegate(element);
	}

	@Override
	public String getValue() {
		GeoElement element = delegate.getElement();
		if (!element.isAlgebraLabelVisible()) {
			return "";
		}
		return hasCaptionStyle(element) ? element.getCaptionSimple() : element.getLabelSimple();
	}

	@Override
	public void doSetValue(String value) {
		GeoElement element = delegate.getElement();
		String oldLabel = getValue();
		if (value.equals(oldLabel)) {
			return;
		}

		if (LabelManager.isValidLabel(value, element.getKernel(), element)) {
			if (!value.equals(element.getLabelSimple())) {
				// changing from caption to existing label
				String newLabel = element.getFreeLabel(value);
				element.rename(newLabel);
			}
			element.setLabelMode(showsValue(element) ? GeoElementND.LABEL_NAME_VALUE
					: GeoElementND.LABEL_NAME);
		} else {
			element.setCaption(value);
			element.setLabelMode(showsValue(element) ? GeoElementND.LABEL_CAPTION_VALUE
					: GeoElementND.LABEL_CAPTION);
		}

		element.setAlgebraLabelVisible(true);
		element.getKernel().notifyUpdate(element);
		element.updateRepaint();
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	private boolean showsValue(GeoElementND element) {
		int labelStyle = element.getLabelMode();
		return labelStyle == GeoElementND.LABEL_VALUE
				|| labelStyle == GeoElementND.LABEL_CAPTION_VALUE
				|| labelStyle == GeoElementND.LABEL_NAME_VALUE;
	}

	private boolean hasCaptionStyle(GeoElementND element) {
		int labelMode = element.getLabelMode();
		return labelMode == GeoElementND.LABEL_CAPTION
				|| labelMode == GeoElementND.LABEL_CAPTION_VALUE;
	}
}
