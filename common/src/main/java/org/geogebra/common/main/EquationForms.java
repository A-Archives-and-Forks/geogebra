package org.geogebra.common.main;

/**
 * The equation forms to be used for lines and other objects, as created by different sources.
 */
public interface EquationForms {

	/**
	 * The default (baseline) equation form for all lines.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getDefaultLineEquationForm();

	/**
	 * The equation form for lines created from linear equations.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getAlgebraicLineEquationForm();

	/**
	 * The equation form for lines created from Line() commands.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getLineCommandEquationForm();

	/**
	 * The equation form for lines created from FitLine() commands.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getFitLineCommandEquationForm();

	/**
	 * The equation form for lines created from Tangent() commands.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getTangentCommandEquationForm();

	/**
	 * The equation form for conics created from ? TODO
	 * @return one of the EQUATION_* constants defined in GeoConicND, or -1 if not applicable.
	 */
	int getConicEquationForm();
}
