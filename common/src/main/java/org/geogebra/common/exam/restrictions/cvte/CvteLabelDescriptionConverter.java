package org.geogebra.common.exam.restrictions.cvte;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.util.ToStringConverter;

public class CvteLabelDescriptionConverter implements ToStringConverter<GeoElement> {

    private final ToStringConverter<GeoElement> wrappedConverter;

    public CvteLabelDescriptionConverter(ToStringConverter<GeoElement> wrappedConverter) {
        this.wrappedConverter = wrappedConverter;
    }

    @Override
    public String convert(GeoElement element) {
        if (restrictionsApplyTo(element)) {
            return getRestrictedLabelDescription(element);
        }
        return wrappedConverter.convert(element);
    }

    private boolean restrictionsApplyTo(GeoElement element) {
        return Cvte.isLineConicEquationOrFunction(element)
                && Cvte.isCreatedByToolOrCmd(element);
    }

    private String getRestrictedLabelDescription(GeoElement element) {
        String label;
        switch (element.getLabelMode()) {
            case LABEL_VALUE:
            case LABEL_NAME_VALUE:
                label = element.getDefinition(element.getLabelStringTemplate());
                break;
            default:
                label = wrappedConverter.convert(element);
        }
        return label.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : label;
    }
}
