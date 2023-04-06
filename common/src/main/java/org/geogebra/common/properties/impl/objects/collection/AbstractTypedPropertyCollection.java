package org.geogebra.common.properties.impl.objects.collection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

abstract class AbstractTypedPropertyCollection<T extends ValuedProperty<S>, S> implements
		ValuedProperty<S> {

	private final T[] properties;
	private final Set<PropertyValueObserver> observers = new HashSet<>();

	AbstractTypedPropertyCollection(T[] properties) {
		if (properties.length == 0) {
			throw new IllegalArgumentException("Properties must have at least a single property");
		}
		this.properties = properties;
	}

	@Override
	public String getName() {
		return getFirstProperty().getName();
	}

	protected T getFirstProperty() {
		return properties[0];
	}

	protected T[] getProperties() {
		return properties;
	}

	@Override
	public boolean isEnabled() {
		boolean isEnabled = true;
		for (Property property : properties) {
			isEnabled = isEnabled && property.isEnabled();
		}
		return isEnabled;
	}

	@Override
	public void addValueObserver(PropertyValueObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeValueObserver(PropertyValueObserver observer) {
		observers.remove(observer);
	}

	private void notifyObservers(Consumer<PropertyValueObserver> observerConsumer) {
		observers.forEach(observerConsumer);
	}

	private void callProperty(Consumer<T> propertyConsumer) {
		Arrays.asList(properties).forEach(propertyConsumer);
	}

	@Override
	public S getValue() {
		return getFirstProperty().getValue();
	}

	@Override
	public void setValue(S value) {
		callProperty(property -> property.setValue(value));
		notifyObservers(observer -> observer.onChange(this));
	}

	@Override
	public void startChangingValue() {
		callProperty(ValuedProperty::startChangingValue);
		notifyObservers(observer -> observer.onStartChanging(this));
	}

	@Override
	public void endChangingValue() {
		callProperty(ValuedProperty::endChangingValue);
		notifyObservers(observer -> observer.onEndChanging(this));
	}
}
