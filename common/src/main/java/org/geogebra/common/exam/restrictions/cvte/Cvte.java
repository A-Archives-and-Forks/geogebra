package org.geogebra.common.exam.restrictions.cvte;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Shared logic for CvTE exam restrictions.
 */
final class Cvte {

    /**
     * APPS-5926: "For Lines, Rays, Conics, Implicit Equations and Functions..."
     * @param element a {@link GeoElementND}
     * @return true if element matches the condition above.
     */
    static boolean isLineConicEquationOrFunction(@Nullable GeoElementND element) {
        if (element == null) {
            return false;
        }
        return element.isGeoLine()
                || element.isGeoRay()
                || element.isGeoConic()
                || element.isGeoFunction()
                || isImplicitEquation(element);
    }

    /**
     * Checks whether a {@link GeoElementND} was created by a tool or command.
     * @param element a {@link GeoElementND}
     * @return if element was created by a tool or command
     */
    static boolean isCreatedByToolOrCmd(@Nullable GeoElementND element) {
        if (element == null) {
            return false;
        }
        return element.getParentAlgorithm() != null;
    }

    private static boolean isImplicitEquation(@Nonnull GeoElementND geoElement) {
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
}
