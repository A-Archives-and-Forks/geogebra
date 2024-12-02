package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.gui.view.algebra.fiter.AlgebraOutputFilter;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Algebra output filter for APPS-5926 (don't try to adapt to other use cases!).
 */
public final class CvteAlgebraOutputFilter implements AlgebraOutputFilter {

    /**
     * "For Lines, Rays, Conics, Implicit Equations and Functions created with a command or tool,
     * we do not show the calculated equation."
     */
    @Override
    public boolean isAllowed(GeoElementND element) {
        if (((element.isGeoLine()
                || element.isGeoRay()
                || element.isGeoConic())
                || isImplicitEquation(element)
                || isFunction(element))
                && element.getParentAlgorithm() != null
        ) {
            return false;
        }
        return true;
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
