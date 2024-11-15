package org.geogebra.common.exam;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class CvteExamTests extends BaseExamTests {
    @BeforeEach
    public void setupCvteExam() {
        setInitialApp(SuiteSubApp.GRAPHING);
        examController.startExam(ExamType.CVTE, null);
    }

    @Test
    public void testMatrixOutputRestrictions() {
        evaluate("l1={1,2}");
        evaluate("l2={1,2}");

        assertNull(evaluate("{l1, l2}"));
        assertNull(evaluate("{If(true, l1)}"));
        assertNull(evaluate("{IterationList(x^2,3,2)}"));
        assertNull(evaluate("{Sequence(k,k,1,3)}"));
    }

    @Test
    public void testSyntaxRestrictions() {
        evaluate("A=(1,1)");
        evaluate("B=(2,2)");

        errorAccumulator.resetError();
        assertNull(evaluate("Circle(A, B)"));
        assertThat(errorAccumulator.getErrorsSinceReset(),
                containsString("Illegal argument: Point B"));

        errorAccumulator.resetError();
        assertNotNull(evaluate("Circle(A, 1)"));
        assertEquals("", errorAccumulator.getErrorsSinceReset());
    }

    @Test
    public void testToolRestrictions() {
        assertTrue(app.getAvailableTools().contains(EuclidianConstants.MODE_MOVE));
        assertFalse(app.getAvailableTools().contains(EuclidianConstants.MODE_POINT));
        assertTrue(commandDispatcher.isAllowedByCommandFilters(Commands.Curve));
        assertTrue(commandDispatcher.isAllowedByCommandFilters(Commands.CurveCartesian));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Enabled conics
            "Circle((0, 0), 2)",
            "Circle((1, 1), 4)",
            // Enabled equations
            "x = 0",
            "x + y = 0",
            "2x - 3y = 4",
            "y = 2x",
            "y = x^2",
            "y = x^3",
            // Other enabled expressions
            "x",
            "f(x) = x^2",
            "x^2",
            "A = (1, 2)"
    })
    public void testUnrestrictedGraphicalOutputVisibility(String expression) {
        GeoElement geoElement = evaluateGeoElement(expression);
        assertAll(
                () -> assertTrue(geoElement.isEuclidianVisible()),
                () -> assertTrue(geoElement.isEuclidianToggleable()),
                () -> assertNotNull(geoElementPropertiesFactory.createShowObjectProperty(
                        app.getLocalization(), List.of(geoElement))));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Restricted conics
            "x^2 + y^2 = 4",
            "x^2 / 9 + y^2 / 4 = 1",
            "x^2 - y^2 = 4",
            // Restricted equations
            "x^2 = 0",
            "x^2 = 1",
            "2^x = 0",
            "sin(x) = 0",
            "ln(x) = 0",
            "|x - 3| = 0",
            "y - x^2 = 0",
            "x^2 = y",
            "x^3 = y",
            "y^2 = x",
            "x^2 + y^2 = 4",
            "x^2 / 9 + y^2 / 4 = 1",
            "x^2 - y^2 = 4",
            "x^3 + y^2 = 2",
            "y^3 = x"
    })
    public void testRestrictedGraphicalOutputVisibility(String expression) {
        GeoElement geoElement = evaluateGeoElement(expression);
        assertAll(
                () -> assertFalse(geoElement.isEuclidianVisible()),
                () -> assertFalse(geoElement.isEuclidianToggleable()),
                () -> assertNull(geoElementPropertiesFactory.createShowObjectProperty(
                        app.getLocalization(), List.of(geoElement))));
    }
}
