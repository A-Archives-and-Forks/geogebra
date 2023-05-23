package org.geogebra.web.richtext.impl;

import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaTableFactory {
	public native CarotaTable create(HTMLElement element);
}
