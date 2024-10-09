package org.geogebra.common.properties.factory;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.collections.BooleanPropertyCollection;
import org.geogebra.common.properties.impl.collections.ColorPropertyCollection;
import org.geogebra.common.properties.impl.collections.EnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.IconsEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.common.properties.impl.objects.AnimationStepProperty;
import org.geogebra.common.properties.impl.objects.BoldProperty;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.ElementColorProperty;
import org.geogebra.common.properties.impl.objects.EquationFormProperty;
import org.geogebra.common.properties.impl.objects.FillingStyleProperty;
import org.geogebra.common.properties.impl.objects.HorizontalAlignmentProperty;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.IsFixedObjectProperty;
import org.geogebra.common.properties.impl.objects.ItalicProperty;
import org.geogebra.common.properties.impl.objects.LineStyleProperty;
import org.geogebra.common.properties.impl.objects.MaxProperty;
import org.geogebra.common.properties.impl.objects.MinProperty;
import org.geogebra.common.properties.impl.objects.NameProperty;
import org.geogebra.common.properties.impl.objects.NotesThicknessProperty;
import org.geogebra.common.properties.impl.objects.OpacityProperty;
import org.geogebra.common.properties.impl.objects.PointSizeProperty;
import org.geogebra.common.properties.impl.objects.PointStyleProperty;
import org.geogebra.common.properties.impl.objects.SegmentEndProperty;
import org.geogebra.common.properties.impl.objects.SegmentStartProperty;
import org.geogebra.common.properties.impl.objects.ShowInAVProperty;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.geogebra.common.properties.impl.objects.ShowTraceProperty;
import org.geogebra.common.properties.impl.objects.SlopeSizeProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.common.properties.impl.objects.UnderlineProperty;
import org.geogebra.common.properties.impl.objects.VerticalAlignmentProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Creates the list of properties for a GeoElement or for a list of GeoElements.
 */
public class GeoElementPropertiesFactory {

	/**
	 * Creates properties for a list of GeoElements.
	 * @param processor algebra processor
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public static PropertiesArray createGeoElementProperties(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		if (elements.isEmpty()) {
			return new PropertiesArray("");
		}
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createNameProperty(localization, elements));
		addPropertyIfNotNull(properties, createMinProperty(processor, localization, elements));
		addPropertyIfNotNull(properties, createMaxProperty(processor, localization, elements));
		addPropertyIfNotNull(properties, createStepProperty(processor, localization, elements));
		addPropertyIfNotNull(properties, createShowObjectProperty(localization, elements));
		addPropertyIfNotNull(properties, createColorProperty(localization, elements));
		addPropertyIfNotNull(properties, createPointStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createPointSizeProperty(localization, elements));
		addPropertyIfNotNull(properties, createOpacityProperty(localization, elements));
		addPropertyIfNotNull(properties, createLineStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createThicknessProperty(localization, elements));
		addPropertyIfNotNull(properties, createSlopeSizeProperty(localization, elements));
		addPropertyIfNotNull(properties, createEquationFormProperty(localization, elements));
		addPropertyIfNotNull(properties, createCaptionStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createShowTraceProperty(localization, elements));
		addPropertyIfNotNull(properties, createFixObjectProperty(localization, elements));
		addPropertyIfNotNull(properties, createShowInAvProperty(localization, elements));
		return createPropertiesArray(localization, properties, elements);
	}

	/**
	 * Creates Point style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public static PropertiesArray createPointStyleProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createPointStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createPointSizeProperty(localization, elements));
		return createPropertiesArray(localization, properties, elements);
	}

	/**
	 * Creates Lines style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public static PropertiesArray createLineStyleProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createLineStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createThicknessProperty(localization, elements));
		return createPropertiesArray(localization, properties, elements);
	}

	/**
	 * Creates Lines style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public static PropertiesArray createNotesLineStyleProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createLineStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createNotesThicknessProperty(localization, elements));
		return createPropertiesArray(localization, properties, elements);
	}

	/**
	 * Creates a color property for the elements
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public static ColorProperty createColorProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<ElementColorProperty> colorProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				colorProperties.add(new ElementColorProperty(localization, element));
			}
			return new ColorPropertyCollection<>(
					colorProperties.toArray(new ElementColorProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns with a Boolean property that fixes the object, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static BooleanProperty createFixObjectProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<IsFixedObjectProperty> fixObjectProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				fixObjectProperties.add(new IsFixedObjectProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(
					fixObjectProperties.toArray(new IsFixedObjectProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns with a Boolean property that formats bold the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return bold property or null
	 */
	public static BooleanProperty createBoldProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<BoldProperty> boldProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				boldProperties.add(new BoldProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(boldProperties.toArray(new BoldProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns with a Boolean property that formats italic the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return italic property or null
	 */
	public static BooleanProperty createItalicProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<ItalicProperty> italicProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				italicProperties.add(new ItalicProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(italicProperties
					.toArray(new ItalicProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns with a Boolean property that formats underlined the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return underline property or null
	 */
	public static BooleanProperty createUnderlineProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<UnderlineProperty> underlineProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				underlineProperties.add(new UnderlineProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(underlineProperties
					.toArray(new UnderlineProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the point style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedProperty createPointStyleProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<PointStyleProperty> pointStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				pointStyleProperties.add(new PointStyleProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					pointStyleProperties.toArray(new PointStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an Integer RangeProperty controlling the point size or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static RangeProperty<Integer> createPointSizeProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<PointSizeProperty> pointSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				pointSizeProperties.add(new PointSizeProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					pointSizeProperties.toArray(new PointSizeProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an Integer RangeProperty controlling the line thickness null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static RangePropertyCollection<?> createThicknessProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<ThicknessProperty> thicknessProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				thicknessProperties.add(new ThicknessProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					thicknessProperties.toArray(new ThicknessProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an Integer RangeProperty controlling the line thickness in notes,
	 * null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static RangePropertyCollection<?> createNotesThicknessProperty(Localization
			localization, List<GeoElement> elements) {
		try {
			List<NotesThicknessProperty> thicknessProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				thicknessProperties.add(new NotesThicknessProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					thicknessProperties.toArray(new NotesThicknessProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the line style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedProperty createLineStyleProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<LineStyleProperty> lineStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				lineStyleProperties.add(new LineStyleProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					lineStyleProperties.toArray(new LineStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the filling type or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedPropertyCollection createFillingStyleProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<FillingStyleProperty> fillingStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				fillingStyleProperties.add(new FillingStyleProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					fillingStyleProperties.toArray(new FillingStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the horizontal alignment or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedPropertyCollection createHorizontalAlignmentProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<HorizontalAlignmentProperty> horizontalAlignmentProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				horizontalAlignmentProperties.add(new HorizontalAlignmentProperty(localization,
						element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					horizontalAlignmentProperties.toArray(new HorizontalAlignmentProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the horizontal alignment or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedPropertyCollection createVerticalAlignmentProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<VerticalAlignmentProperty> verticalAlignmentProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				verticalAlignmentProperties.add(new VerticalAlignmentProperty(localization,
						element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					verticalAlignmentProperties.toArray(new VerticalAlignmentProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the segment start style or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedPropertyCollection createSegmentStartProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<SegmentStartProperty> segmentStartProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				segmentStartProperties.add(new SegmentStartProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					segmentStartProperties.toArray(new SegmentStartProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the segment end style or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedPropertyCollection createSegmentEndProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<SegmentEndProperty> segmentEndProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				segmentEndProperties.add(new SegmentEndProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					segmentEndProperties.toArray(new SegmentEndProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static void addPropertyIfNotNull(List<Property> properties,
			Property property) {
		if (property != null) {
			properties.add(property);
		}
	}

	private static StringPropertyCollection<NameProperty> createNameProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<NameProperty> nameProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				nameProperties.add(new NameProperty(localization, element));
			}
			return new StringPropertyCollection<>(nameProperties.toArray(new NameProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static BooleanPropertyCollection<ShowObjectProperty> createShowObjectProperty(
			Localization localization, List<GeoElement> elements) {
		List<ShowObjectProperty> showObjectProperties = new ArrayList<>();
		for (GeoElement element : elements) {
			showObjectProperties.add(new ShowObjectProperty(localization, element));
		}
		return new BooleanPropertyCollection<>(
				showObjectProperties.toArray(new ShowObjectProperty[0]));
	}

	private static EnumeratedPropertyCollection<CaptionStyleProperty, Integer>
	createCaptionStyleProperty(Localization localization, List<GeoElement> elements) {
		try {
			List<CaptionStyleProperty> captionStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				captionStyleProperties.add(new CaptionStyleProperty(localization, element));
			}
			return new NamedEnumeratedPropertyCollection<>(
					captionStyleProperties.toArray(new CaptionStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static RangeProperty<Integer> createOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<OpacityProperty> opacityProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				opacityProperties.add(new OpacityProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					opacityProperties.toArray(new OpacityProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static RangeProperty<Integer> createImageOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<ImageOpacityProperty> opacityProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				opacityProperties.add(new ImageOpacityProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					opacityProperties.toArray(new ImageOpacityProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static StringPropertyCollection<MinProperty> createMinProperty(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		try {
			List<MinProperty> minProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				minProperties.add(new MinProperty(processor, localization, element));
			}
			return new StringPropertyCollection<>(
					minProperties.toArray(new MinProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static StringPropertyCollection<MaxProperty> createMaxProperty(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		try {
			List<MaxProperty> maxProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				maxProperties.add(new MaxProperty(processor, localization, element));
			}
			return new StringPropertyCollection<>(
					maxProperties.toArray(new MaxProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static StringPropertyCollection<AnimationStepProperty> createStepProperty(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		try {
			List<AnimationStepProperty> stepProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				stepProperties.add(new AnimationStepProperty(processor, localization, element));
			}
			return new StringPropertyCollection<>(
					stepProperties.toArray(new AnimationStepProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static RangePropertyCollection<SlopeSizeProperty> createSlopeSizeProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<SlopeSizeProperty> slopeSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				slopeSizeProperties.add(new SlopeSizeProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					slopeSizeProperties.toArray(new SlopeSizeProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static EnumeratedPropertyCollection<EquationFormProperty, Integer>
	createEquationFormProperty(Localization localization, List<GeoElement> elements) {
		try {
			List<EquationFormProperty> equationFormProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				equationFormProperties.add(new EquationFormProperty(localization, element));
			}
			return new NamedEnumeratedPropertyCollection<>(
					equationFormProperties.toArray(new EquationFormProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static BooleanPropertyCollection<ShowTraceProperty> createShowTraceProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<ShowTraceProperty> traceProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				traceProperties.add(new ShowTraceProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(
					traceProperties.toArray(new ShowTraceProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static BooleanPropertyCollection<ShowInAVProperty> createShowInAvProperty(
			Localization localization, List<GeoElement> elements) {
		List<ShowInAVProperty> showInAvProperties = new ArrayList<>();
		for (GeoElement element : elements) {
			showInAvProperties.add(new ShowInAVProperty(localization, element));
		}
		return new BooleanPropertyCollection<>(
				showInAvProperties.toArray(new ShowInAVProperty[0]));

	}

	private static PropertiesArray createPropertiesArray(Localization localization,
			List<Property> properties, List<GeoElement> geoElements) {
		String name;
		if (geoElements.size() > 1) {
			name = localization.getMenu("Selection");
		} else if (geoElements.size() == 1) {
			GeoElement element = geoElements.get(0);
			name = element.translatedTypeString();
		} else {
			name = "";
		}

		return new PropertiesArray(name, properties.toArray(new Property[0]));
	}
}