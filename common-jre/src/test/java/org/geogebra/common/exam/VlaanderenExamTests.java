package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class VlaanderenExamTests extends BaseExamTests {
    @BeforeEach
    public void setupVlaanderenExam() {
        setInitialApp(SuiteSubApp.GRAPHING);
        examController.startExam(ExamType.VLAANDEREN, null);
    }

    @Test
    public void testDerivativeOperationRestriction() {
        assertNotNull(evaluate("f(x) = x^2"));
        assertNull(evaluate("f'"));
    }
}
