package org.geogebra.common.properties.impl.objects;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.ColorValues;
import org.geogebra.common.main.color.GeoColorValues;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.NotesInlineColorPropertyDelegate;
import org.geogebra.common.util.StringUtil;

public class NotesFontColorProperty extends ElementColorProperty
		implements ColorProperty {

	private final GeoElement element;

	/**
	 * @param localization - localization
	 * @param element - element
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public NotesFontColorProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, new NotesInlineColorPropertyDelegate(element));
		this.element = element;
		setValues(Arrays.stream(GeoColorValues.values()).map(ColorValues::getColor)
				.collect(Collectors.toList()));
	}

	@Override
	public void doSetValue(GColor value) {
		EuclidianStyleBarStatic.applyColor(value, element.getAlphaValue(), element.getApp(),
				List.of(element));

		String htmlColor = StringUtil.toHtmlColor(value);
		if (element instanceof HasTextFormatter) {
			((HasTextFormatter) element).getFormatter().format("color", htmlColor);
		}
	}
}
