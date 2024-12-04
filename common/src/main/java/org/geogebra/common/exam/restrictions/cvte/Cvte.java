package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Shared logic for CvTE exam restrictions.
 */
public final class Cvte {

    /**
     * APPS-5926: "For Lines, Rays, Conics, Implicit Equations and Functions..."
     * @param element a {@link GeoElementND}
     * @return true if element matches the condition above.
     */
    public static boolean isLineConicEquationOrFunction(GeoElementND element) {
        return element.isGeoLine()
                || element.isGeoRay()
                || element.isGeoConic()
                || isImplicitEquation(element)
                || isFunction(element);
    }

    /**
     * Checks whether a {@link GeoElementND} was created by a tool or command.
     * @param element a {@link GeoElementND}
     * @return if element was created by a tool or command
     */
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
