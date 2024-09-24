package org.geogebra.common.kernel;

/**
 * The equation forms to be used for lines and other objects, as created by different sources.
 * <p/>
 * Note: The equation form for {@link org.geogebra.common.kernel.geos.GeoLine GeoLine} and
 * subclasses is initalized from the construction defaults (via the call to
 * {@code setConstructionDefaults()} in the {@code GeoLine} constructor). By default, the
 * equation style in the construction defaults for {@code GeoLine} and subclasses is set to
 * {@code EQUATION_IMPLICIT}, but this default setting may be overridden (in the Classic app).
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation forms matrix"</a>.
 */
public interface EquationBehaviour {

	/**
	 * The equation form for lines created from user input (linear equations).
	 * @return one of the EQUATION_* constants defined in GeoLine, or -1 if the equation form
	 * should be taken from the construction defaults for lines (see note in header).
	 */
	int getLinearAlgebraInputEquationForm();

	/**
	 * The equation form for conics created from user input.
	 * @return one of the {@code EQUATION_...} constants defined in GeoConicND, or -1 if ...
	 *  TODO specify condition
	 */
	int getConicAlgebraInputEquationForm();

	// TODO APPS-5867: Lines, Conics, *Implicit Equations and Functions* are restricted to
	//  “Input Form” in standalone Graphing

	/**
	 * The equation form for lines created from a (Line) command or tool.
	 * @return one of the {@code EQUATION_...} constants defined in GeoLine, or -1 if the equation
	 * form should be taken from the construction defaults for lines (see note in header).
	 *
	 * @see org.geogebra.common.kernel.algos.AlgoJoinPoints
	 */
	int getLineCommandEquationForm();

	/**
	 * The equation form for Rays created from a (Ray) command or tool.
	 * @return one of the {@code EQUATION_...} constants defined in GeoLine, or -1 if the equation
	 * form should be taken from the construction defaults for lines (see note in header).
	 */
	int getRayCommandEquationForm();

	/**
	 * The equation form for lines created from FitLine() commands.
	 * @return one of the {@code EQUATION_...} constants defined in GeoLine, or -1 if the equation
	 * form should be taken from the construction defaults for lines (see note in header).
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
}
