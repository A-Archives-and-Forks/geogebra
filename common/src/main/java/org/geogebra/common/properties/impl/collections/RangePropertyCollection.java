package org.geogebra.common.properties.impl.collections;

import org.geogebra.common.properties.RangeProperty;

/**
 * Handles a collection of RangeProperty objects as a single RangeProperty.
 */
public class RangePropertyCollection<T extends RangeProperty<Integer>>
		extends AbstractValuedPropertyCollection<T, Integer> implements RangeProperty<Integer> {

	/**
	 * @param properties properties to handle
	 */
	public RangePropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public Integer getStep() {
		return getFirstProperty().getStep();
	}

	@Override
	public Integer getMin() {
		return getFirstProperty().getMin();
	}

	@Override
	public Integer getMax() {
		return getFirstProperty().getMax();
	}

}
