package org.geogebra.web.html5.util;

import java.util.List;

import org.geogebra.gwtutil.ExceptionUnwrapper;

import com.google.gwt.core.client.GWT;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLIFrameElement;
import jsinterop.base.Js;

public class SuperDevUncaughtExceptionHandler {
	/**
	 * Registers handler for UnhandledExceptions that are wrapped by GWT by
	 * default
	 */
	public static void register() {
		org.gwtproject.core.client.GWT.setUncaughtExceptionHandler( evt -> {
			DomGlobal.console.warn(evt.getClass()+"...");
				//ExceptionUnwrapper.printErrorMessage(evt);
			});

	}
}
