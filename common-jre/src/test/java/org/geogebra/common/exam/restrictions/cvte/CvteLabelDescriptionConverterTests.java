package org.geogebra.common.exam.restrictions.cvte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTests;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.description.DefaultLabelDescriptionConverter;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ToStringConverter;
import org.junit.Before;
import org.junit.Test;

public class CvteLabelDescriptionConverterTests extends BaseExamTests {

    @Before
    public void setup() {
        setInitialApp(SuiteSubApp.GRAPHING);
    }

    @Test
    public void testLabelRestrictions() throws NotApplicablePropertyException {
        ToStringConverter<GeoElement> converter = new CvteLabelDescriptionConverter(
                new DefaultLabelDescriptionConverter());

        Localization localization = app.getLocalization();

        // For Lines, Rays, Conics, Implicit Equations and Functions created with command or tool:
        // When choosing "Value" or "Name & Value" for the caption style the "definition" is shown.
        GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
        CaptionStyleProperty captionStyleProperty = new CaptionStyleProperty(localization, line);
        captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
        assertEquals(line.getDefinition(StringTemplate.defaultTemplate),
                converter.convert(line));
        captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
        assertEquals(line.getDefinition(StringTemplate.defaultTemplate),
                converter.convert(line));
        captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
        assertEquals(line.getLabel(StringTemplate.defaultTemplate),
                converter.convert(line));
        captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
        assertEquals(line.getCaption(StringTemplate.defaultTemplate),
                converter.convert(line));

        GeoElement ray = evaluateGeoElement("Ray((0, 0), (1, 2))");
        captionStyleProperty = new CaptionStyleProperty(localization, ray);
        captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
        assertEquals(ray.getDefinition(StringTemplate.defaultTemplate),
                converter.convert(ray));
        captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
        assertEquals(ray.getDefinition(StringTemplate.defaultTemplate),
                converter.convert(ray));
        captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
        assertEquals(ray.getLabel(StringTemplate.defaultTemplate),
                converter.convert(ray));
        captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
        assertEquals(ray.getCaption(StringTemplate.defaultTemplate),
                converter.convert(ray));

        GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
        captionStyleProperty = new CaptionStyleProperty(localization, circle);
        captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
        assertEquals(circle.getDefinition(StringTemplate.defaultTemplate),
                converter.convert(circle));
        captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
        assertEquals(circle.getDefinition(StringTemplate.defaultTemplate),
                converter.convert(circle));
        captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
        assertEquals(circle.getLabel(StringTemplate.defaultTemplate),
                converter.convert(circle));
        captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
        assertEquals(circle.getCaption(StringTemplate.defaultTemplate),
                converter.convert(circle));

        // TODO are there any examples for implicit equation or function created with a command?
    }
}
