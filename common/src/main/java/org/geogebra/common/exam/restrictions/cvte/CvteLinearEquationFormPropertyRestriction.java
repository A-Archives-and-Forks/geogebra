package org.geogebra.common.exam.restrictions.cvte;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.restrictions.PropertyRestriction;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.ValueFilter;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;

/**
 * For any Conic, Line, Equation, Function or Implicit Equation manually entered
 * by the user, restrict the equation form to “Input Form”.
 */
public class CvteLinearEquationFormPropertyRestriction extends PropertyRestriction
        implements ValueFilter {

    private LinearEquationFormProperty property;

    public CvteLinearEquationFormPropertyRestriction() {
        super(false, null);
    }

    @Override
    public void applyTo(@Nonnull Property property) {
        super.applyTo(property);
        this.property = property instanceof LinearEquationFormProperty
                ? (LinearEquationFormProperty) property : null;
    }

    @Override
    public boolean isValueAllowed(Object value) {
        if (property != null) {
            GeoElement element = property.getGeoElement();
            if (Cvte.isEquationFormRestrictedToUserForm(element)) {
                return LinearEquationRepresentable.Form.CONST_USER == (Integer) value;
            }
        }
        return false;
    }
}