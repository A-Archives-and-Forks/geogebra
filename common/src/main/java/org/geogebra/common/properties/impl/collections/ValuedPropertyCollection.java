package org.geogebra.common.properties.impl.collections;

import org.geogebra.common.properties.ValuedProperty;

/**
 * Handles a collection of StringProperty objects as a single StringProperty.
 */
public class ValuedPropertyCollection<T extends ValuedProperty<Integer>>
		extends AbstractValuedPropertyCollection<T, Integer> {

	/**
	 * @param properties properties to handle
	 */
	public ValuedPropertyCollection(T[] properties) {
		super(properties);
	}
}

