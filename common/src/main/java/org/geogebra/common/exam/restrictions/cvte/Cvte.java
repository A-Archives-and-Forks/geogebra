package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public final class Cvte {

    public static boolean isLineConicEquationOrFunction(GeoElementND element) {
        return element.isGeoLine()
                || element.isGeoRay()
                || element.isGeoConic()
                || isImplicitEquation(element)
                || isFunction(element);
    }

    public static boolean isCreatedByToolOrCmd(GeoElementND element) {
        return element.getParentAlgorithm() != null;
    }

    private static boolean isImplicitEquation(GeoElementND geoElement) {
        if (geoElement instanceof EquationValue) {
            EquationValue equationValue = (EquationValue) geoElement;
            return equationValue.getEquation().isImplicit();
        }
        ExpressionNode definition = geoElement.getDefinition();
        if (definition != null && definition.unwrap() instanceof Equation) {
            Equation equation = (Equation) definition.unwrap();
            return equation.isImplicit();
        }
        return false;
    }

    private static boolean isFunction(GeoElementND geoElement) {
        return geoElement.isGeoFunction();
    }
}
