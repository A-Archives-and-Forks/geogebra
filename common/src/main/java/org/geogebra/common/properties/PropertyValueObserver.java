package org.geogebra.common.properties;

/**
 * A listener for value sets emitted by a {@link ValuedProperty}.
 */
public interface PropertyValueObserver<V> {

    /**
     * Called when the property value was set.
     *
     * @param property property
     */
    void onDidSetValue(ValuedProperty<V> property);

    /**
     * Called when the property will have it's value set multiple times.
     * For more information see {@link ValuedProperty#beginSetValue()}.
     *
     * @param property property
     */
    void onBeginSetValue(ValuedProperty<V> property);

    /**
     * Called when the property value ends changing.
     * For more information see {@link ValuedProperty#beginSetValue()}.
     *
     * @param property property
     */
    void onEndSetValue(ValuedProperty<V> property);
}
