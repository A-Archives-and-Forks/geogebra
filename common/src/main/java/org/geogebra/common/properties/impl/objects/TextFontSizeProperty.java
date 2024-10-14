package org.geogebra.common.properties.impl.objects;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.InlineTextFormatter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.kernel.geos.properties.TextFontSize;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;

public class TextFontSizeProperty extends AbstractNamedEnumeratedProperty<TextFontSize> {
	private final List<TextFontSize> fontSizes = Arrays.asList(TextFontSize.EXTRA_SMALL,
			TextFontSize.VERY_SMALL, TextFontSize.SMALL, TextFontSize.MEDIUM,
			TextFontSize.LARGE, TextFontSize.VERY_LARGE, TextFontSize.EXTRA_LARGE);
	private final GeoElementDelegate delegate;
	private final EuclidianView ev;
	private final InlineTextFormatter inlineTextFormatter;

	/**
	 * Text font size property
	 * @param localization localization
	 * @param element geo element
	 * @param ev euclidian view
	 */
	public TextFontSizeProperty(Localization localization, GeoElement element, EuclidianView ev)
			throws NotApplicablePropertyException {
		super(localization, "FontSize");
		delegate = new TextFormatterDelegate(element);
		this.ev = ev;
		inlineTextFormatter = new InlineTextFormatter();
		setValues(fontSizes.toArray(new TextFontSize[0]));
		setValueNames(fontSizes.stream().map(TextFontSize::getName).toArray(String[]::new));
	}

	@Override
	protected void doSetValue(TextFontSize value) {
		GeoElement element = delegate.getElement();
		double size = GeoText.getRelativeFontSize(fontSizes.indexOf(value)) * ev.getFontSize();
		inlineTextFormatter.formatInlineText(Collections.singletonList(element), "size", size);
		element.updateVisualStyleRepaint(GProperty.FONT);
	}

	@Override
	public TextFontSize getValue() {
		GeoElement element = delegate.getElement();
		return fontSizes.get(GeoText.getFontSizeIndex(
				((TextStyle) element).getFontSizeMultiplier()));
	}
}
