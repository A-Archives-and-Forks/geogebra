package org.geogebra.common.kernel;

/**
 * The equation forms and visibility settings to be used for lines and other objects, as
 * created by different sources.
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">This spreadsheet"</a>.
 */
public interface EquationBehaviour {

	/**
	 * The default equation form for all lines. This default form may be overwritten by
	 * one of the special cases below.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getDefaultLineEquationForm();

	/**
	 * The equation form for lines created from user input (linear equations).
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getLinearAlgebraInputEquationForm();

	/**
	 * The equation form for conics created from user input.
	 * @return one of the EQUATION_* constants defined in GeoConicND, or -1 if not applicable.
	 */
	int getConicAlgebraInputEquationForm();

	/**
	 * The equation form for lines created from a (Line) command or tool.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 * @see org.geogebra.common.kernel.algos.AlgoJoinPoints
	 */
	int getLineCommandEquationForm();

	/**
	 * The equation form for Rays created from a (Ray) command or tool.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getRayCommandEquationForm();

	// TODO add more getters for any GeoLine/GeoRay/GeoSegment-producing code that needs
	//  special equation forms

	/**
	 * The equation form for lines created from FitLine() commands.
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if not applicable.
	 */
	int getFitLineCommandEquationForm();

	/**
	 * Whether this EquationBehaviour allows the equation forms to be changed by the user.
	 * Since this is currently an all-or-none property (see spreadsheet linked in header)
	 * there's only one method for all equation forms. If we need finer granularity, we can
	 * split this into multiple methods.
	 * @return true if the equation forms can be changed, false otherwise.
	 * TODO could this be overriden for certain exams?
	 */
	boolean allowsChangingEquationFormsByUser();

	/**
	 * Whether to show an output row in the Algebra View for an element.
	 * @param element
	 * @return
	 */
	// TODO e.g. "For Rays, Conics, Implicit Equations and Functions created with a command
	//  or tool we do not show the calculated equation / In the Algebra View no output row is shown"
	//  -> this information needs to be saved at the time the element is created
//	boolean showAlgebraViewOuputRow(GeoElement element);
}
