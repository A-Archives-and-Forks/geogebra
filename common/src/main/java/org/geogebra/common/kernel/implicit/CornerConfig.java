package org.geogebra.common.kernel.implicit;

public class CornerConfig {
	/**
	 * All corners are inside / outside
	 */
	public static final int T0000 = 0;

	/**
	 * only bottom left corner is inside / outside
	 */
	public static final int T0001 = 1;

	/**
	 * bottom right corner is inside / outside
	 */
	public static final int T0010 = 2;

	/**
	 * both corners at the bottom are inside / outside
	 */
	public static final int T0011 = 3;

	/**
	 * top left corner is inside / outside
	 */
	public static final int T0100 = 4;

	/**
	 * opposite corners are inside / outside. NOTE: This configuration is
	 * regarded as invalid
	 */
	public static final int T0101 = 5;

	/**
	 * both the corners at the left are inside / outside
	 */
	public static final int T0110 = 6;

	/**
	 * only top left corner is inside / outside
	 */
	public static final int T0111 = 7;

	/**
	 * invalid configuration. expression value is undefined / infinity for at
	 * least one of the corner
	 */
	public static final int T_INV = -1;
	public static final int EMPTY = 0;
	public static final int VALID = 1;

}
