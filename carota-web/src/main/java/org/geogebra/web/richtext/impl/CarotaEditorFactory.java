package org.geogebra.web.richtext.impl;

import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "carota")
public class CarotaEditorFactory {
	public native CarotaDocument create(HTMLElement div);
}
