package org.geogebra.common.properties.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.GeoElementPropertyFilter;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.collections.BooleanPropertyCollection;
import org.geogebra.common.properties.impl.collections.ColorPropertyCollection;
import org.geogebra.common.properties.impl.collections.IconsEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.common.properties.impl.objects.AnimationStepProperty;
import org.geogebra.common.properties.impl.objects.BoldProperty;
import org.geogebra.common.properties.impl.objects.BorderColorProperty;
import org.geogebra.common.properties.impl.objects.BorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.CellBorderProperty;
import org.geogebra.common.properties.impl.objects.CellBorderThicknessProperty;
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
import org.geogebra.common.properties.impl.objects.NotesColorProperty;
import org.geogebra.common.properties.impl.objects.NotesColorWithOpacityProperty;
import org.geogebra.common.properties.impl.objects.NotesFontColorProperty;
import org.geogebra.common.properties.impl.objects.NotesInlineBackgroundColorProperty;
import org.geogebra.common.properties.impl.objects.NotesOpacityColorProperty;
import org.geogebra.common.properties.impl.objects.NotesThicknessProperty;
import org.geogebra.common.properties.impl.objects.OpacityProperty;
import org.geogebra.common.properties.impl.objects.PointSizeProperty;
import org.geogebra.common.properties.impl.objects.PointStyleExtendedProperty;
import org.geogebra.common.properties.impl.objects.PointStyleProperty;
import org.geogebra.common.properties.impl.objects.SegmentEndProperty;
import org.geogebra.common.properties.impl.objects.SegmentStartProperty;
import org.geogebra.common.properties.impl.objects.ShowInAVProperty;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.geogebra.common.properties.impl.objects.ShowTraceProperty;
import org.geogebra.common.properties.impl.objects.SlopeSizeProperty;
import org.geogebra.common.properties.impl.objects.TextFontSizeProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.common.properties.impl.objects.UnderlineProperty;
import org.geogebra.common.properties.impl.objects.VerticalAlignmentProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Creates the list of properties for a GeoElement or for a list of GeoElements.
 */
public final class GeoElementPropertiesFactory {
	private final Set<GeoElementPropertyFilter> propertyFilters = new HashSet<>();

	/**
	 * Adds a {@link GeoElementPropertyFilter} which can modify the returned properties by
	 * the methods of the class.
	 *
	 * @param filter the {@link GeoElementPropertyFilter} to be added
	 */
	public void addFilter(GeoElementPropertyFilter filter) {
		propertyFilters.add(filter);
	}

	/**
	 * Removes the previously added {@link GeoElementPropertyFilter}, undoing the effect of
	 * {@link GeoElementPropertiesFactory#addFilter}.
	 * @param filter the {@link GeoElementPropertyFilter} to be removed
	 */
	public void removeFilter(GeoElementPropertyFilter filter) {
		propertyFilters.remove(filter);
	}

	/**
	 * Creates properties for a list of GeoElements.
	 * @param processor algebra processor
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createGeoElementProperties(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.<Property>of(
				createPropertyCollection(elements,
						element -> new NameProperty(localization, element),
						properties -> new StringPropertyCollection<>(
								properties.toArray(new NameProperty[0]))),
				createPropertyCollection(elements,
						element -> new MinProperty(processor, localization, element),
						properties -> new StringPropertyCollection<>(
								properties.toArray(new MinProperty[0]))),
				createPropertyCollection(elements,
						element -> new MaxProperty(processor, localization, element),
						properties -> new StringPropertyCollection<>(
								properties.toArray(new MaxProperty[0]))),
				createPropertyCollection(elements,
						element -> new AnimationStepProperty(processor, localization, element),
						properties -> new StringPropertyCollection<>(
								properties.toArray(new AnimationStepProperty[0]))),
				createShowObjectProperty(localization, elements),
				createColorProperty(localization, elements),
				createPointStyleProperty(localization, elements),
				createPointSizeProperty(localization, elements),
				createOpacityProperty(localization, elements),
				createLineStyleProperty(localization, elements),
				createThicknessProperty(localization, elements),
				createPropertyCollection(elements,
						element -> new SlopeSizeProperty(localization, element),
						properties -> new RangePropertyCollection<>(
								properties.toArray(new SlopeSizeProperty[0]))),
				createPropertyCollection(elements,
						element -> new EquationFormProperty(localization, element),
						properties -> new NamedEnumeratedPropertyCollection<>(
								properties.toArray(new EquationFormProperty[0]))),
				createPropertyCollection(elements,
						element -> new CaptionStyleProperty(localization, element),
						properties -> new NamedEnumeratedPropertyCollection<>(
								properties.toArray(new CaptionStyleProperty[0]))),
				createPropertyCollection(elements,
						element -> new ShowTraceProperty(localization, element),
						properties -> new BooleanPropertyCollection<>(
								properties.toArray(new ShowTraceProperty[0]))),
				createFixObjectProperty(localization, elements),
				createPropertyCollection(elements,
						element -> new ShowInAVProperty(localization, element),
						properties -> new BooleanPropertyCollection<>(
								properties.toArray(new ShowInAVProperty[0])))
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Creates Point style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createPointStyleProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.<Property>of(
				createPointStyleProperty(localization, elements),
				createPointSizeProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Creates extended point style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createPointStyleExtendedProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createPointStyleExtendedProperty(localization, elements));
		addPropertyIfNotNull(properties, createPointSizeProperty(localization, elements));
		return createPropertiesArray(localization, elements, properties);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the extended point style or null if
	 * not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedProperty<?> createPointStyleExtendedProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<PointStyleExtendedProperty> pointStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				pointStyleProperties.add(new PointStyleExtendedProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					pointStyleProperties.toArray(new PointStyleExtendedProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Creates Lines style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createLineStyleProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.<Property>of(
				createLineStyleProperty(localization, elements),
				createThicknessProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Creates Lines style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public List<Property> createNotesLineStyleProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createLineStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createNotesThicknessProperty(localization, elements));
		return properties;
	}

	/**
	 * Creates color with opacity properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public static PropertiesArray createNotesColorWithOpacityProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createColorWithOpacityProperty(localization, elements));
		addPropertyIfNotNull(properties, createOpacityColorProperty(localization, elements));
		return createPropertiesArray(localization, elements, properties);
	}

	/**
	 * Creates border color and thickness for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public static PropertiesArray createObjectBorderProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createBorderColorProperty(localization, elements));
		addPropertyIfNotNull(properties, createBorderThicknessProperty(localization, elements));
		return createPropertiesArray(localization, elements, properties);
	}

	/**
	 * Creates cell border style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public static PropertiesArray createCellBorderStyleProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createCellBorderStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createCellBorderThicknessProperty(localization,
				elements));
		return createPropertiesArray(localization, elements, properties);
	}

	/**
	 * Returns an Integer RangeProperty controlling the border thickness null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static RangePropertyCollection<?> createCellBorderThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<CellBorderThicknessProperty> borderThicknessProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				borderThicknessProperties.add(new CellBorderThicknessProperty(localization,
						element));
			}
			return new RangePropertyCollection<>(
					borderThicknessProperties.toArray(new CellBorderThicknessProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the cell border or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedProperty<?> createCellBorderStyleProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<CellBorderProperty> cellBorderProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				cellBorderProperties.add(new CellBorderProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					cellBorderProperties.toArray(new CellBorderProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Creates a color property for the elements
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ElementColorProperty(localization, element),
				properties -> new ColorPropertyCollection<>(
						properties.toArray(new ElementColorProperty[0])));
	}

	/**
	 * Creates a color property for strokes and shapes
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public static ColorProperty createNotesColorProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<NotesColorProperty> colorProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				colorProperties.add(new NotesColorProperty(localization, element));
			}
			return new ColorPropertyCollection<>(
					colorProperties.toArray(new NotesColorProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Creates a color property for non-mask shapes
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public static ColorProperty createColorWithOpacityProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<NotesColorWithOpacityProperty> colorProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				colorProperties.add(new NotesColorWithOpacityProperty(localization, element));
			}
			return new ColorPropertyCollection<>(
					colorProperties.toArray(new NotesColorWithOpacityProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Creates a color property for inline object
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public static ColorProperty createNotesFontColorProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<NotesFontColorProperty> colorProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				colorProperties.add(new NotesFontColorProperty(localization, element));
			}
			return new ColorPropertyCollection<>(
					colorProperties.toArray(new NotesFontColorProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Creates a background color property for inline object
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public static ColorProperty createInlineBackgroundColorProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<NotesInlineBackgroundColorProperty> colorProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				colorProperties.add(new NotesInlineBackgroundColorProperty(localization, element));
			}
			return new ColorPropertyCollection<>(
					colorProperties.toArray(new NotesInlineBackgroundColorProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Creates border color property for text and mindmap
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public static ColorProperty createBorderColorProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<BorderColorProperty> colorProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				colorProperties.add(new BorderColorProperty(localization, element));
			}
			return new ColorPropertyCollection<>(
					colorProperties.toArray(new BorderColorProperty[0]));
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
	public BooleanProperty createFixObjectProperty(Localization localization,
			List<GeoElement> elements) {
        return createPropertyCollection(elements,
				element -> new IsFixedObjectProperty(localization, element),
				properties -> new BooleanPropertyCollection<>(
						properties.toArray(new IsFixedObjectProperty[0])));
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
	public IconsEnumeratedProperty<?> createPointStyleProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new PointStyleProperty(localization, element),
				properties -> new IconsEnumeratedPropertyCollection<>(
						properties.toArray(new PointStyleProperty[0])));
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
	public IconsEnumeratedProperty<?> createLineStyleProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new LineStyleProperty(localization, element),
				properties -> new IconsEnumeratedPropertyCollection<>(
						properties.toArray(new LineStyleProperty[0])));
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the filling type or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static IconsEnumeratedPropertyCollection<?, ?> createFillingStyleProperty(
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
	public static IconsEnumeratedPropertyCollection<?, ?> createHorizontalAlignmentProperty(
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
	public static IconsEnumeratedPropertyCollection<?, ?> createVerticalAlignmentProperty(
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
	public static IconsEnumeratedPropertyCollection<?, ?> createSegmentStartProperty(
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
	 * Returns property controlling the text font size or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @param ev euclidian view
	 * @return property or null
	 */
	public static NamedEnumeratedPropertyCollection<?, ?> createTextFontSizeProperty(
			Localization localization, List<GeoElement> elements, EuclidianView ev) {
		try {
			List<TextFontSizeProperty> fontSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				fontSizeProperties.add(new TextFontSizeProperty(localization, element, ev));
			}
			return new NamedEnumeratedPropertyCollection<>(
					fontSizeProperties.toArray(new TextFontSizeProperty[0]));
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
	public static IconsEnumeratedPropertyCollection<?, ?> createSegmentEndProperty(
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

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new OpacityProperty(localization, element),
				properties -> new RangePropertyCollection<>(
						properties.toArray(new OpacityProperty[0])));
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static RangeProperty<Integer> createOpacityColorProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<NotesOpacityColorProperty> opacityProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				opacityProperties.add(new NotesOpacityColorProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					opacityProperties.toArray(new NotesOpacityColorProperty[0]));
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
	public static RangeProperty<Integer> createBorderThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<BorderThicknessProperty> opacityProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				opacityProperties.add(new BorderThicknessProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					opacityProperties.toArray(new BorderThicknessProperty[0]));
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

	/**
	 * Creates a {@link BooleanPropertyCollection} to control the visibility of the elements.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public BooleanProperty createShowObjectProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ShowObjectProperty(localization, element),
				properties -> new BooleanPropertyCollection<>(
						properties.toArray(new ShowObjectProperty[0])));
	}

	/**
	 * Creates a {@link RangePropertyCollection} to control the size of the points.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public RangeProperty<Integer> createPointSizeProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new PointSizeProperty(localization, element),
				properties -> new RangePropertyCollection<>(
						properties.toArray(new PointSizeProperty[0])));
	}

	/**
	 * Creates a {@link RangePropertyCollection} to control the thickness of lines.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public RangeProperty<Integer> createThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ThicknessProperty(localization, element),
				properties -> new RangePropertyCollection<>(
						properties.toArray(new ThicknessProperty[0])));
	}

	private static PropertiesArray createPropertiesArray(Localization localization,
			List<GeoElement> geoElements, List<Property> properties) {
		if (properties.isEmpty()) {
			return new PropertiesArray("");
		}

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

	/**
	 * A factory interface for creating instances of
	 * properties associated with a {@link GeoElement}.
	 * @param <PropertyType> the type of property that this factory produces
	 */
	private interface PropertyFactory<PropertyType extends Property> {
		/**
		 * Creates a property instance for the specified {@link GeoElement}.
		 * If the property is not applicable to the provided {@link GeoElement}, a
		 * {@link NotApplicablePropertyException} is thrown.
		 *
		 * @param geoElement {@link GeoElement} for which the property should be created
		 * @return an instance of the specific property type
		 * @throws NotApplicablePropertyException if the property cannot be applied
		 * @throws IllegalArgumentException if the property can't be created from
		 * to the given {@link GeoElement}
		 */
		PropertyType create(GeoElement geoElement) throws NotApplicablePropertyException;
	}

	/**
	 * Collector interface for aggregating multiple properties
	 * of a specific type into a single collection.
	 *
	 * @param <PropertyType> the type of individual properties that will be collected
	 * @param <PropertyCollection> the type of the resulting collection of properties
	 */
	private interface PropertyCollector<
			PropertyType extends Property,
			PropertyCollection extends Property> {
		/**
		 * Collects a list of individual properties into a single {@link PropertyCollection}.
		 *
		 * @param properties the list of individual properties to collect
		 * @return a collection of properties that represents the aggregated result
		 * @throws IllegalArgumentException if the input list of properties is invalid
		 */
		PropertyCollection collect(List<PropertyType> properties) throws IllegalArgumentException;
	}

	/**
	 * Creates a collection of properties by applying a {@link PropertyFactory} to a list of
	 * {@link GeoElement} s and then aggregating the resulting properties using a
	 * {@link PropertyCollector}. The method filters properties using the provided property filters
	 * before collecting them.
	 *
	 * @param <Prop> the type of individual properties to be created
	 * @param <PropCollection> the type of the property collection to be created
	 * @param geoElements the list of {@link GeoElement}s for which properties are to be created
	 * @param propertyFactory the factory used to create
	 * individual properties for each {@link GeoElement}
	 * @param propertyCollector the collector used to
	 * aggregate the individual properties into a collection
	 * @return a collection of properties of type {@link PropCollection}, or {@code null}
	 * if a property cannot be created for one of the {@link GeoElement}s.
	 */
	private <
			Prop extends Property,
			PropCollection extends Property
	> PropCollection createPropertyCollection(
			List<GeoElement> geoElements,
			PropertyFactory<Prop> propertyFactory,
			PropertyCollector<Prop, PropCollection> propertyCollector
	) {
		try {
			ArrayList<Prop> properties = new ArrayList<>();
			for (GeoElement geoElement : geoElements) {
				Prop property = propertyFactory.create(geoElement);
				if (property != null && isAllowedByFilters(property, geoElement)) {
					properties.add(property);
				}
			}
			if (properties.isEmpty()) {
				return null;
			}
			return propertyCollector.collect(properties);
		} catch (NotApplicablePropertyException ignored) {
			return null;
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	private boolean isAllowedByFilters(Property property, GeoElement geoElement) {
		return propertyFilters.stream().allMatch(filter -> filter.isAllowed(property, geoElement));
	}
}
