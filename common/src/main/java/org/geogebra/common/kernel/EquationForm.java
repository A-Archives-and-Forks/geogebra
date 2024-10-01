package org.geogebra.common.kernel;

public final class EquationForm {

	// values originally defined in GeoLine
	public final class Linear {
		/** implicit equation a x + b y = c */
		public static final int IMPLICIT = 0;
		/** explicit equation y = m x + b */
		public static final int EXPLICIT = 1;
		/** parametric equation */
		public static final int PARAMETRIC = 2;
		/** non-canonical implicit equation (not used anywhere) */
		//public static final int EQUATION_IMPLICIT_NON_CANONICAL = 3; // a x + b y = -c
		/** general form a x + b y + c = 0 (GGB-1212) */
		public static final int GENERAL = 4;
		/** user input form */
		public static final int USER = 5;
	}

	// values originally defined in GeoConicND
	public final class Quadric {
		/** ax^2+bxy+cy^2+dx+ey+f=0 */
		public static final int IMPLICIT = 0;
		/** y=ax^2+bx+c */
		public static final int EXPLICIT = 1;
		/** (x-m)^2/a^2+(y-n)^2/b^2=1 */
		public static final int SPECIFIC = 2;
		/** X=(1,1)+(sin(t),cos(t)) */
		public static final int PARAMETRIC = 3;
		/** user input form */
		public static final int USER = 4;
		/** vertex form */
		public static final int VERTEX = 5;
		/** conic form */
		public static final int CONICFORM = 6;
	}
}
