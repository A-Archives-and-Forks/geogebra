package org.geogebra.common.util;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class that creates an engineering notation based on a numeric input<br/>
 * The engineering notation is similar to the scientific notation (m*10^n), with n
 * being restricted to multiples of three (3) only. <br/>
 */
public class EngineeringNotationString {

	private String engineeringNotation;
	private String sign = "";

	/**
	 * @param number Value
	 */
	public EngineeringNotationString(double number) {
		String valueString = Double.toString(number);
		if (valueString.charAt(0) == '-') {
			valueString = valueString.substring(1);
			sign = "-";
		}

		String predecimalsString = getPredecimalsStringForEngineeringNotation(valueString);
		String decimalsString = getDecimalsStringForEngineeringNotation(valueString);

		int exponent = getPositiveExponentForEngineeringNotation(predecimalsString.length());
		if (exponent == 0 && number < 0.01 && number > -0.01) {
			exponent = getNegativeExponentForEngineeringNotation(decimalsString);
		}

		if (exponent < 0) {
			engineeringNotation = sign + createEngineeringNotationWithNegativeExponent(
					decimalsString, exponent);
			return;
		}
		engineeringNotation = sign + createEngineeringNotationWithPositiveExponent(
				predecimalsString, decimalsString, exponent);
	}

	/**
	 * @return The engineering notation string
	 */
	public String getResult() {
		return engineeringNotation;
	}

	private String getPredecimalsStringForEngineeringNotation(String valueString) {
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

	private String getDecimalsStringForEngineeringNotation(String valueString) {
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

	private int getPositiveExponentForEngineeringNotation(int amountOfPredecimals) {
		if (amountOfPredecimals % 3 == 0) {
			return amountOfPredecimals - 3;
		}
		return amountOfPredecimals % 3 == 2 ? amountOfPredecimals - 2 : amountOfPredecimals - 1;
	}

	private int getNegativeExponentForEngineeringNotation(String decimalsString) {
		boolean nonZeroFound = false;
		int exponent = 0;
		for (int i = 0; i < decimalsString.length(); i++) {
			if (!nonZeroFound && decimalsString.charAt(i) != '0') {
				nonZeroFound = true;
			}
			if (nonZeroFound && i > 1) {
				return -(i + 1) / 3 * 3;
			}
		}
		return exponent;
	}

	private String createEngineeringNotationWithPositiveExponent(String predecimalsString,
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

		engineeringNotation.append(" ").append(Unicode.CENTER_DOT).append(" 10");
		String exponentString = String.valueOf(exponent);
		for (int i = 0; i < exponentString.length(); i++) {
			engineeringNotation.append(Unicode.numberToSuperscript(exponentString.charAt(i) - '0'));
		}
		return engineeringNotation.toString();
	}

	private String createEngineeringNotationWithNegativeExponent(String decimalsString,
			int exponent) {
		StringBuilder engineeringNotation = new StringBuilder();
		int shiftBy = Math.abs(exponent);
		engineeringNotation.append(StringUtil.removeLeadingZeros(
				decimalsString.substring(0, shiftBy)));
		if (engineeringNotation.length() == 0) {
			engineeringNotation.append("0");
		}
		if (!decimalsString.substring(shiftBy).isEmpty()) {
			engineeringNotation.append(".");
		}

		engineeringNotation.append(decimalsString.substring(shiftBy));
		engineeringNotation.append(" ").append(Unicode.CENTER_DOT).append(" 10");
		engineeringNotation.append(Unicode.SUPERSCRIPT_MINUS);
		String exponentString = String.valueOf(exponent);
		for (int i = 1; i < exponentString.length(); i++) {
			engineeringNotation.append(Unicode.numberToSuperscript(exponentString.charAt(i) - '0'));
		}

		return engineeringNotation.toString();
	}

}
