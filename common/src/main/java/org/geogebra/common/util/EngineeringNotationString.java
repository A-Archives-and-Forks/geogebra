package org.geogebra.common.util;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class that creates an engineering notation based on a numeric input<br/>
 * The engineering notation is similar to the scientific notation (m*10^n), with n
 * being restricted to multiples of three (3) only. <br/>
 */
public final class EngineeringNotationString {

	/**
	 * @param number Value
	 */
	public static String format(double number) {
		String valueString = Double.toString(number);
		String sign = "";
		if (valueString.charAt(0) == '-') {
			valueString = valueString.substring(1);
			sign = "-";
		}

		String predecimalsString = getPredecimalsStringForEngineeringNotation(valueString);
		String decimalsString = getDecimalsStringForEngineeringNotation(valueString);

		int exponent = getPositiveExponentForEngineeringNotation(predecimalsString.length());
		if (exponent == 0 && number < 1 && number > -1) {
			exponent = getNegativeExponentForEngineeringNotation(decimalsString);
		}

		if (exponent < 0) {
			return sign + createEngineeringNotationWithNegativeExponent(
					decimalsString, exponent);
		}
		return sign + createEngineeringNotationWithPositiveExponent(
				predecimalsString, decimalsString, exponent);
	}

	private static String getPredecimalsStringForEngineeringNotation(String valueString) {
		String predecimalsString = valueString;
		if (valueString.contains("e")) {
			if (valueString.contains("e+")) {
				predecimalsString = valueString.substring(0, valueString.indexOf("e"))
						.replace(".", "");
				int remainingPredecimals = Integer.parseInt(valueString.substring(
						valueString.indexOf("+") + 1)) - predecimalsString.length() + 1;
				for (int i = 0; i < remainingPredecimals; i++) {
					predecimalsString += "0";
				}
			} else if (valueString.contains("e-")) {
				predecimalsString = "0";
			}
		} else if (valueString.contains(".")) {
			predecimalsString = valueString.substring(0, valueString.indexOf('.'));
		}
		return predecimalsString;
	}

	private static String getDecimalsStringForEngineeringNotation(String valueString) {
		String decimalsString = "";
		if (valueString.contains("e")) {
			if (valueString.contains("e-")) {
				int zeros = Integer.parseInt(valueString.substring(valueString.indexOf("-") + 1));
				for (int i = 1; i < zeros; i++) {
					decimalsString += "0";
				}
				decimalsString +=
						valueString.substring(0, valueString.indexOf("e")).replace(".", "");
			} else if (valueString.contains("e+")) {
				decimalsString = "";
			}
		} else if (valueString.contains(".")) {
			decimalsString = valueString.substring(valueString.indexOf(".") + 1,
					valueString.length());
		}
		return decimalsString;
	}

	private static int getPositiveExponentForEngineeringNotation(int amountOfPredecimals) {
		if (amountOfPredecimals % 3 == 0) {
			return amountOfPredecimals - 3;
		}
		return amountOfPredecimals % 3 == 2 ? amountOfPredecimals - 2 : amountOfPredecimals - 1;
	}

	private static int getNegativeExponentForEngineeringNotation(String decimalsString) {
		boolean nonZeroFound = false;
		int exponent = 0;
		for (int i = 0; i < decimalsString.length(); i++) {
			if (!nonZeroFound && decimalsString.charAt(i) != '0') {
				nonZeroFound = true;
			}
			if (nonZeroFound) {
				return -(i + 3) / 3 * 3;
			}
		}
		return exponent;
	}

	private static String createEngineeringNotationWithPositiveExponent(String predecimalsString,
			String decimalsString, int exponent) {
		StringBuilder engineeringNotation = new StringBuilder();
		int shiftBy = predecimalsString.length() - exponent;
		engineeringNotation.append(predecimalsString.substring(0, shiftBy));

		String remainingPredecimals = predecimalsString.substring(shiftBy);
		if (decimalsString.isEmpty()) {
			remainingPredecimals = StringUtil.removeTrailingZeros(remainingPredecimals);
		}

		if (!remainingPredecimals.isEmpty() || !decimalsString.isEmpty()) {
			engineeringNotation.append(".");
		}

		engineeringNotation.append(remainingPredecimals);
		engineeringNotation.append(decimalsString);
		removeTrailingZerosAndCommaIfNeeded(engineeringNotation);

		engineeringNotation.append(" ").append(Unicode.CENTER_DOT).append(" 10");
		String exponentString = String.valueOf(exponent);
		for (int i = 0; i < exponentString.length(); i++) {
			engineeringNotation.append(Unicode.numberToSuperscript(exponentString.charAt(i) - '0'));
		}
		return engineeringNotation.toString();
	}

	private static String createEngineeringNotationWithNegativeExponent(String decimalsString,
			int exponent) {
		StringBuilder engineeringNotation = new StringBuilder();
		int shiftBy = Math.abs(exponent);
		String modifiedDecimalsString = decimalsString;

		for (int i = shiftBy; i >= modifiedDecimalsString.length(); i--) {
			modifiedDecimalsString += "0";
		}

		engineeringNotation.append(StringUtil.removeLeadingZeros(
				modifiedDecimalsString.substring(0, shiftBy)));
		if (engineeringNotation.length() == 0) {
			engineeringNotation.append("0");
		}
		if (!modifiedDecimalsString.substring(shiftBy).isEmpty()) {
			engineeringNotation.append(".");
		}

		engineeringNotation.append(modifiedDecimalsString.substring(shiftBy));
		if (decimalsString.length() % 3 == 0) {
			removeTrailingZerosAndCommaIfNeeded(engineeringNotation);
		}

		engineeringNotation.append(" ").append(Unicode.CENTER_DOT).append(" 10");
		engineeringNotation.append(Unicode.SUPERSCRIPT_MINUS);
		String exponentString = String.valueOf(exponent);
		for (int i = 1; i < exponentString.length(); i++) {
			engineeringNotation.append(Unicode.numberToSuperscript(exponentString.charAt(i) - '0'));
		}

		return engineeringNotation.toString();
	}

	private static void removeTrailingZerosAndCommaIfNeeded(StringBuilder engineeringNotation) {
		if (engineeringNotation.length() < 2) {
			return;
		}
		String modified = StringUtil.removeTrailingZeros(engineeringNotation.toString());
		if (modified.charAt(modified.length() - 1) == '.') {
			modified = modified.substring(0, modified.length() - 1);
		}
		engineeringNotation.replace(0, engineeringNotation.length(), modified);
	}

}
