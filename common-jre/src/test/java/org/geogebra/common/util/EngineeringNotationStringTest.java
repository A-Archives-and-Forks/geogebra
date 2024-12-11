package org.geogebra.common.util;


import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class EngineeringNotationStringTest extends BaseUnitTest {

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput1() {
		assertEquals("12 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0,
				EngineeringNotationString.format(12, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput2() {
		assertEquals("123.456 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_3,
				EngineeringNotationString.format(123456, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput3() {
		assertEquals("-18 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0,
				EngineeringNotationString.format(-18, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput4() {
		assertEquals("-7.654321 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_6,
				EngineeringNotationString.format(-7654321, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput5() {
		assertEquals("0 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0,
				EngineeringNotationString.format(0, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput6() {
		assertEquals("0 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0,
				EngineeringNotationString.format(-0, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput7() {
		assertEquals("10 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_9
						+ Unicode.SUPERSCRIPT_9,
				EngineeringNotationString.format(1E100, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput8() {
		assertEquals("-10 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_9
						+ Unicode.SUPERSCRIPT_9,
				EngineeringNotationString.format(-1E100, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput9() {
		assertEquals("100 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0 + Unicode.SUPERSCRIPT_2,
				EngineeringNotationString.format(1E-100, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput10() {
		assertEquals("-100 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0 + Unicode.SUPERSCRIPT_2,
				EngineeringNotationString.format(-1E-100, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput1() {
		assertEquals("500 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_3,
				EngineeringNotationString.format(0.5, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput2() {
		assertEquals("10 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_3,
				EngineeringNotationString.format(0.01, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput3() {
		assertEquals("3 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_3,
				EngineeringNotationString.format(0.003, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput4() {
		assertEquals("-17.32 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0,
				EngineeringNotationString.format(-17.32, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput5() {
		assertEquals("126.3122 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0,
				EngineeringNotationString.format(126.3122, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput6() {
		assertEquals("5.0001 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0,
				EngineeringNotationString.format(5.000100, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput7() {
		assertEquals("123.4567893 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_6,
				EngineeringNotationString.format(123456789.3, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput8() {
		assertEquals("12.34512345 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_3,
				EngineeringNotationString.format(12345.12345, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput9() {
		assertEquals("1.2345678903 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_9,
				EngineeringNotationString.format(1234567890.3, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput10() {
		assertEquals("70 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_6,
				EngineeringNotationString.format(0.00007, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput11() {
		assertEquals("100.7 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_6,
				EngineeringNotationString.format(0.0001007, StringType.GEOGEBRA));
	}
}
