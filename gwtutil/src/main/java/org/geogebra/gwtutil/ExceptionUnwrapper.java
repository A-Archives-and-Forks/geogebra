package org.geogebra.gwtutil;

import org.geogebra.common.util.debug.Log;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class ExceptionUnwrapper {

	/**
	 * @param thrown exception
	 */
	public static void printErrorMessage(Object thrown) {
		if (Js.isFalsy(thrown)) {
			Log.warn("nothing");
			return;}
		// This contains the stacktrace in gwt dev mode.
		Object backingJsObject = Js.asPropertyMap(thrown).nestedGet("backingJsObject.stack");
		if (Js.asPropertyMap(DomGlobal.console).has("error")) {
			if (Js.isTruthy(backingJsObject)) {
				DomGlobal.console.error(backingJsObject);
			} else {
				DomGlobal.console.error(thrown);
			}
		}
	}
}
