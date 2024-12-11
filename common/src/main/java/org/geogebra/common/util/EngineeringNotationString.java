package org.geogebra.common.util;

import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class that creates an engineering notation based on a numeric input<br/>
 * The engineering notation is similar to the scientific notation (m*10^n), with n
 * being restricted to multiples of three (3) only. <br/>
 */
public final class EngineeringNotationString {

	/**
	 * @param number Value
	 * @param stringType StringType
	 * @return The formatted engineering notation
	 */
	public static String format(double number, StringType stringType) {
		if (number == 0) {
			return formatEngineeringNotation("0", 0, stringType);
		}
		String valueString = Double.toString(number)
				.replace('e', 'E')
				.replaceAll("E(\\d+)", "E+$1");
		String sign = "";

		if (valueString.charAt(0) == '-') {
			valueString = valueString.substring(1);
			sign = "-";
		}

		String significantDigitsWithComma = getSignificantDigitsWithComma(valueString);
		String integerPartString = getIntegerPartString(significantDigitsWithComma);
		String decimalPartString = getDecimalPartString(significantDigitsWithComma);

		int exponent = 0;
		if (valueString.contains("E")) {
			exponent = extractExponentFromScientificNotation(valueString, significantDigitsWithComma);
		} else if (shouldShiftCommaLeft(integerPartString)) {
			exponent = getExponentByShiftingCommaLeft(integerPartString);
		} else if (shouldShiftCommaRight(number)) {
			exponent = getExponentByShiftingCommaRight(decimalPartString);
		}
		exponent = adjustExponent(exponent);

		String modifiedValueString = modifySignificantDigits(significantDigitsWithComma, exponent);
		return sign + formatEngineeringNotation(modifiedValueString, exponent, stringType);
	}

	private static String getSignificantDigitsWithComma(String valueString) {
		String significantDigits = valueString;
		if (valueString.contains("E")) {
			significantDigits = extractSignificantDigitsFromScientificNotation(valueString);
		}
		return significantDigits;
	}

	private static String extractSignificantDigitsFromScientificNotation(String scientific) {
		if (scientific.contains("E") && scientific.contains(".")) {
			int indexOfComma = Integer.parseInt(scientific.substring(scientific.indexOf('E') + 2));
			StringBuilder significantDigits = new StringBuilder(
					scientific.substring(0, scientific.indexOf('E'))
					.replace(".", ""));
			if (significantDigits.length() > indexOfComma) {
				significantDigits.insert(indexOfComma + 1, '.');
			}
			return significantDigits.toString();
		}
		return scientific.substring(0, scientific.indexOf('E'));
	}

	private static String getIntegerPartString(String significantDigitsWithComma) {
		if (significantDigitsWithComma.contains(".")) {
			return significantDigitsWithComma.substring(0, significantDigitsWithComma.indexOf('.'));
		}
		return significantDigitsWithComma;
	}

	private static String getDecimalPartString(String significantDigitsWithComma) {
		if (significantDigitsWithComma.contains(".")) {
			return significantDigitsWithComma.substring(
					significantDigitsWithComma.indexOf('.') + 1, significantDigitsWithComma.length());
		}
		return "";
	}

	private static int extractExponentFromScientificNotation(String scientific,
			String significantDigitsWithComma) {
		int partAfterE = Integer.parseInt(scientific.substring(scientific.indexOf('E') + 1));
		if (partAfterE % 3 == 0) {
			return partAfterE;
		}

		StringBuilder significantDigits = new StringBuilder(significantDigitsWithComma);
		removeTrailingZerosAndCommaIfNeeded(significantDigits);

		int significantDigitsAfterComma = 0;
		if (significantDigits.toString().contains(".")) {
			significantDigitsAfterComma = significantDigits
					.substring(significantDigits.indexOf(".") + 1).length();
		}
		return partAfterE - significantDigitsAfterComma;
	}

	private static boolean shouldShiftCommaLeft(String integerPartString) {
		return integerPartString.length() > 3;
	}

	private static int getExponentByShiftingCommaLeft(String integerPartString) {
		return (integerPartString.length() - 1) / 3 * 3;
	}

	private static boolean shouldShiftCommaRight(double number) {
		return Math.abs(number) < 1;
	}

	private static int getExponentByShiftingCommaRight(String decimalPartString) {
		return -(decimalPartString.length() + 2) / 3 * 3;
	}

	private static int adjustExponent(int exponent) {
		if (exponent == 0) {
			return 0;
		} else if (exponent > 0) {
			return exponent - exponent % 3;
		}
		return (exponent - 2) / 3 * 3;
	}

	private static String modifySignificantDigits(String significantDigitsWithComma, int shiftBy) {
		StringBuilder modified = new StringBuilder(significantDigitsWithComma.length());
		String significantDigits = significantDigitsWithComma.replace(".", "");
		int indexOfComma = significantDigitsWithComma.indexOf('.');
		if (indexOfComma == -1) {
			indexOfComma = 0;
		}
		indexOfComma -= shiftBy;

		int index = 0;
		while (index < significantDigits.length()) {
			if (index == indexOfComma) {
				modified.append('.');
			}
			modified.append(significantDigits.charAt(index));
			index++;
		}

		removeLeadingZeros(modified);
		while (index < indexOfComma && modified.length() < 3) {
			modified.append('0');
			index++;
		}
		return modified.toString();
	}

	private static String formatEngineeringNotation(String valueString, int exponent,
			StringType stringType) {
		StringBuilder engineeringNotation = new StringBuilder();
		engineeringNotation.append(valueString);
		removeTrailingZerosAndCommaIfNeeded(engineeringNotation);

		if (stringType == StringType.LATEX) {
			engineeringNotation.append(" \\cdot 10^{").append(exponent).append("}");
		} else {
			engineeringNotation.append(" ").append(Unicode.CENTER_DOT).append(" 10");
			String exponentString = String.valueOf(exponent);
			int i = 0;
			if (exponentString.charAt(0) == '-') {
				engineeringNotation.append(Unicode.SUPERSCRIPT_MINUS);
				i = 1;
			}
			while (i < exponentString.length()) {
				engineeringNotation.append(
						Unicode.numberToSuperscript(exponentString.charAt(i) - '0'));
				i++;
			}
		}
		return engineeringNotation.toString();
	}

	private static void removeLeadingZeros(StringBuilder stringBuilder) {
		if (stringBuilder.length() < 2) {
			return;
		}
		String modified = StringUtil.removeLeadingZeros(stringBuilder.toString());
		if (modified.charAt(0) == '.') {
			modified = "0" + modified;
		}
		stringBuilder.replace(0, stringBuilder.length(), modified);
	}

	private static void removeTrailingZerosAndCommaIfNeeded(StringBuilder stringBuilder) {
		if (stringBuilder.length() < 2 || !stringBuilder.toString().contains(".")) {
			return;
		}
		String modified = StringUtil.removeTrailingZeros(stringBuilder.toString());
		if (modified.charAt(modified.length() - 1) == '.') {
			modified = modified.substring(0, modified.length() - 1);
		}
		stringBuilder.replace(0, stringBuilder.length(), modified);
	}
}
