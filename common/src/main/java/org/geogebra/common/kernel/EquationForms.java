package org.geogebra.common.kernel;

/**
 * The equation forms to be used for lines and other objects, as created by different sources.
 *
 * @see https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?usp=sharing
 */
public interface EquationForms {

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

	// TODO add more getters for any GeoLine/GeoRay/GeoSegment-producing code that needs
	//  special equation forms

	/**
	 * The equation form for conics created from ? TODO
	 * @return one of the EQUATION_* constants defined in GeoConicND, or -1 if not applicable.
	 */
	int getConicEquationForm();
}
