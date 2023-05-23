package org.geogebra.web.html5.util;

import org.apache.commons.collections15.Predicate;
import org.geogebra.gwtutil.NativePointerEvent;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.event.dom.client.DomEvent;

import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.KeyboardEvent;
import elemental2.dom.KeyboardEventInit;
import elemental2.dom.MouseEvent;
import elemental2.dom.TouchEvent;
import jsinterop.base.Js;

/**
 * Simple static methods helping event handling.
 */
public final class EventUtil {

	private EventUtil() {
		// utility class
	}

	/**
	 * @param event
	 *            the event to be checked
	 * @return True if the event is a touch event.
	 */
	public static boolean isTouchEvent(DomEvent<?> event) {
		return isTouchEvent(event.getNativeEvent());
	}

	/**
	 * @param event
	 *            the event to be checked
	 * @return True if the event is a touch event.
	 */
	public static boolean isTouchEvent(Event event) {
		return event.type.contains("touch");
	}

	/**
	 * @param event
	 *            click or touch event
	 * @return The x coordinate of the event (in case of touch the coordinate is
	 *         taken from the first touch).
	 */
	public static int getTouchOrClickClientX(Event event) {
		if (isTouchEvent(event)) {
			return (int) ((TouchEvent) event).changedTouches.getAt(0).clientX;
		}
		return (int) ((MouseEvent) event).clientX;
	}

	/**
	 * @param event
	 *            click or touch event
	 * @return The y coordinate of the event (in case of touch the coordinate is
	 *         taken from the first touch).
	 */
	public static int getTouchOrClickClientY(Event event) {
		if (isTouchEvent(event)) {
			return (int) ((TouchEvent) event).changedTouches.getAt(0).clientY;
		}
		return (int) ((MouseEvent) event).clientY;
	}

	/**
	 * @param event
	 *            click or touch event
	 * @return The x coordinate of the event (in case of touch the coordinate is
	 *         taken from the first touch).
	 */
	public static int getTouchOrClickClientX(DomEvent<?> event) {
		return getTouchOrClickClientX(event.getNativeEvent());
	}

	/**
	 * @param event
	 *            click or touch event
	 * @return The y coordinate of the event (in case of touch the coordinate is
	 *         taken from the first touch).
	 */
	public static int getTouchOrClickClientY(DomEvent<?> event) {
		return getTouchOrClickClientY(event.getNativeEvent());
	}

	/**
	 * Stop propagating all pointer events
	 * @param element target element
	 */
	public static void stopPointer(HTMLElement element) {
		stopPointerEvents(element, evt -> true);
	}

	/**
	 * Stops propagating pointer events such that the button matches a predicate.
	 * (we're checking the button instead od the whole event because of ClassCast
	 * for native types in lambdas...)
	 * @param element target element
	 */
	public static void stopPointerEvents(HTMLElement element, Predicate<Integer> check) {
		for (String evtName : new String[]{"pointerup", "pointerdown"}) {
			Dom.addEventListener(element, evtName, e -> {
				NativePointerEvent ptrEvent = Js.uncheckedCast(e);
				if (check.evaluate(ptrEvent.getButton())) {
					e.stopPropagation();
				}
			});
		}
	}



	public static KeyboardEvent createKeyEvent(String type, boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey, int keyCode) {
		KeyboardEventInit init = KeyboardEventInit.create();
		init.setCtrlKey(ctrlKey);
		init.setAltKey(altKey);
		init.setShiftKey(shiftKey);
		init.setMetaKey(metaKey);
		init.setBubbles(true);
		Js.asPropertyMap(init).set("keyCode", keyCode);
		return new KeyboardEvent(type, init);
	}
}
