package org.geogebra.common.exam;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.junit.Before;
import org.junit.Test;

public class IBExamTests extends BaseExamTests {
    @Before
    public void setupIBExam() {
        setInitialApp(SuiteSubApp.GRAPHING);
        examController.startExam(ExamType.IB, null);
    }

    @Test
    public void testDerivativeRestrictions() {
        evaluate("f(x) = x^2");
        evaluate("p = 2");

        assertNotNull(evaluate("f'(1)"));
        assertNotNull(evaluate("f'(p)"));

        assertNull(evaluate("f'"));
        assertNull(evaluate("f'(x)"));
        assertNull(evaluate("g = f'"));
        assertNull(evaluate("g(x) = f'"));
        assertNull(evaluate("g(x) = f'(x)"));
    }
}
