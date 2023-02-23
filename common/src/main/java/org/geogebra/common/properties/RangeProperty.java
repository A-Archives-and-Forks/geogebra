package org.geogebra.common.properties;

/**
 * A numeric property with min, max and step.
 * @param <T> The type of the number (Integer, Double, etc.)
 */
public interface RangeProperty<T extends Number & Comparable<T>> extends ValuedProperty<T> {

	/**
	 * Returns the minimal possible value for this property inclusive.
	 * @return minimal value
	 */
	T getMin();

	/**
	 * Returns the maximal possible value for this property inclusive.
	 * @return maximal value
	 */
	T getMax();

	/**
	 * @return step
	 */
	T getStep();
}
