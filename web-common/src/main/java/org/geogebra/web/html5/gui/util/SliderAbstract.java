package org.geogebra.web.html5.gui.util;

import java.util.ArrayList;

import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FocusWidget;

import elemental2.dom.HTMLInputElement;

public abstract class SliderAbstract<T> extends FocusWidget {

	private final HTMLInputElement range;
	private boolean valueChangeHandlerInitialized;
	private final ArrayList<ValueChangeHandler<T>> valueChangeHandlers = new ArrayList<>();

	/**
	 * Create a new slider.
	 * @param min slider min
	 * @param max slider max
	 */
	public SliderAbstract(double min, double max) {
		range = DOM.createInput("range");
		range.setAttribute("min", String.valueOf(min));
		range.setAttribute("max", String.valueOf(max));
		range.value = String.valueOf(min);
		setElement(range);
		addMouseMoveHandler(DomEvent::stopPropagation);
	}

	public void addInputHandler(SliderInputHandler handler) {
		Dom.addEventListener(range, "input", evt -> handler.onSliderInput());
	}

	public T getValue() {
		return convert(range.value);
	}

	protected abstract T convert(String val);

	public void setMinimum(double min) {
		range.setAttribute("min", String.valueOf(min));
	}

	public void setMaximum(double max) {
		range.setAttribute("max", String.valueOf(max));
	}

	public void setStep(double step) {
		range.setAttribute("step", String.valueOf(step));
	}

	public void setTickSpacing(int step) {
		range.setAttribute("step", String.valueOf(step));
	}

	/**
	 * @param handler handler for change event (drag end)
	 */
	public void addValueChangeHandler(ValueChangeHandler<T> handler) {
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addDomHandler(event -> this.notifyValueChangeHandlers(), ChangeEvent.getType());
		}
		valueChangeHandlers.add(handler);
	}

	public void setValue(T value) {
		range.value = String.valueOf(value);
	}

	/**
	 * Notify change handlers.
	 */
	public void notifyValueChangeHandlers() {
		for (ValueChangeHandler<T> handler: valueChangeHandlers) {
			handler.onValueChange(new MyValueChangeEvent<>(getValue()));
		}
	}

	private static class MyValueChangeEvent<E> extends ValueChangeEvent<E> {

		/**
		 * Creates a value change event.
		 * @param value the value
		 */
		protected MyValueChangeEvent(E value) {
			super(value);
		}
	}
}

