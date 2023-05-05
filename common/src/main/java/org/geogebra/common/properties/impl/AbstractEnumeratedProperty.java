package org.geogebra.common.properties.impl;

import java.util.Arrays;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.EnumeratedProperty;

public abstract class AbstractEnumeratedProperty<V> extends AbstractValuedProperty<V> implements
		EnumeratedProperty<V> {

	protected V[] values;

	/**
	 * Constructs an AbstractEnumerableProperty
	 * @param localization the localization used
	 * @param name the name of the property
	 */
	public AbstractEnumeratedProperty(Localization localization, String name) {
		super(localization, name);
	}

	protected void setValues(V... values) {
		this.values = values;
	}

	@Override
	public V[] getValues() {
		return values;
	}

	@Override
	public void setIndex(int index) {
		ensureValuesPresent();
		if (index < 0 || index >= values.length) {
			throw new RuntimeException("Index must be between (0, values.length-1)");
		}
		setValue(values[index]);
	}

	@Override
	public int getIndex() {
		ensureValuesPresent();
		return Arrays.asList(values).indexOf(getValue());
	}

	private void ensureValuesPresent() {
		if (values == null) {
			throw new RuntimeException("Set values must be called in the constructor.");
		}
	}
}
