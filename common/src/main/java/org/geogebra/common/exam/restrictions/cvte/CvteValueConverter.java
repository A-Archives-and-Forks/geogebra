package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;

public class CvteValueConverter implements ToStringConverter<GeoElement> {

    private final ToStringConverter<GeoElement> wrappedConverter;

    public CvteValueConverter(ToStringConverter<GeoElement> wrappedConverter) {
        this.wrappedConverter = wrappedConverter;
    }

    @Override
    public String convert(GeoElement element) {
        if (restrictionsApplyTo(element)) {
            return element.getDefinition(StringTemplate.algebraTemplate);
        }
        return wrappedConverter.convert(element);
    }

    private boolean restrictionsApplyTo(GeoElement element) {
        return Cvte.isLineConicEquationOrFunction(element)
                && Cvte.isCreatedByToolOrCmd(element);
    }
}
