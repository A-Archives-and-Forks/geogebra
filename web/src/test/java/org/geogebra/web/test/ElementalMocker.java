package org.geogebra.web.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;

import org.geogebra.common.util.debug.Log;
import org.mockito.Mockito;

import elemental2.core.Global;
import elemental2.core.JSONType;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.Console;
import elemental2.dom.DOMTokenList;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadElement;
import elemental2.dom.HTMLHtmlElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableColElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableRowElement;
import elemental2.dom.HTMLTableSectionElement;
import elemental2.dom.Location;
import elemental2.dom.Navigator;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import elemental2.dom.Text;
import elemental2.webstorage.WebStorageWindow;
import jsinterop.base.JsPropertyMap;

public class ElementalMocker {

	/**
	 * Initiate some global variables (window, document, ...)
	 */
	public static void setupElemental() {
		try {
			DomGlobal.console = new Console();
			DomGlobal.window = new WebStorageWindow();
			Location location = new Location();
			location.search = "";
			setFinalStatic(DomGlobal.class.getField("location"), location);
			setFinalStatic(DomGlobal.class.getField("document"), new HTMLDocument());
			Navigator newValue = new Navigator();
			newValue.platform = "SunOS";
			newValue.userAgent = "Chrome";
			setFinalStatic(DomGlobal.class.getField("navigator"), newValue);
			Global.JSON = new JSONType() {
				@Override
				public Object parse(String s) {
					return JsPropertyMap.of();
				}
			};
			HTMLDocument doc = mock(HTMLDocument.class);
			doc.documentElement = new HTMLHtmlElement();
			doc.scrollingElement = new HTMLHtmlElement();
			doc.body = (HTMLBodyElement) buildElement("body", doc);
			initBasicProps(doc.body, doc);
			doc.head = new HTMLHeadElement();
			when(doc.createElement(anyString())).thenAnswer(invocation -> {
				String tagName = invocation.getArgumentAt(0, String.class);
				HTMLElement el = buildElement(tagName, doc);
				return el;
			});
			when(doc.createTextNode(anyString())).thenAnswer(inv -> new Text());
			setFinalStatic(DomGlobal.class.getField("document"), doc);
		} catch (Exception e) {
			System.err.println("Failed to set up elemental2 mocks");
			Log.debug(e);
		}
	}

	private static HTMLElement buildElement(String tagName, HTMLDocument doc) {
		HTMLElement el = spy(createElement(tagName));
		if (el instanceof HTMLTableElement) {
			((HTMLTableElement) el).tBodies = new HTMLCollection<>();
			when(((HTMLTableElement) el).createTHead()).thenAnswer(inv -> buildElement("thead", doc));
			when(((HTMLTableElement) el).createTFoot()).thenAnswer(inv -> buildElement("tfoot", doc));
		}
		HashMap<String, String> attr = new HashMap<>();
		Mockito.doAnswer(inv -> {
			attr.put(inv.getArgumentAt(0, String.class),
					inv.getArgumentAt(1, String.class));
			return null;
				}).when(el).setAttribute(anyString(), anyString());
		when(el.getAttribute(anyString())).thenAnswer(inv ->
				attr.get(inv.getArgumentAt(0, String.class)));
		when(el.cloneNode(anyBoolean())).thenAnswer(inv -> {
			HTMLElement ret = buildElement(tagName, doc);
			ret.firstChild = el.firstChild;
			return ret;
		});
		when(el.appendChild(any())).thenAnswer(inv -> {
			Node child = inv.getArgumentAt(0, Node.class);
			if (el.firstChild== null) {
				el.firstChild = child;
				if (child instanceof Element) {
					el.firstElementChild = (Element) child;
				}
			}
			if (el.lastChild != null) {
				el.lastChild.nextSibling = child;
			}
			child.parentElement = el;
			el.lastChild = child;
			return child;
		});
		initBasicProps(el, doc);
		el.outerHTML = "<" + tagName + "/>";
		el.parentElement = doc.body;
		el.tagName = tagName.toUpperCase(Locale.ROOT);;
		el.nodeName = tagName.toUpperCase(Locale.ROOT);
		return el;
	}

	private static void initBasicProps(HTMLElement el, HTMLDocument doc) {
		el.classList = new DOMTokenList();
		el.style = new CSSStyleDeclaration();
		el.childNodes = new NodeList<>();
		el.ownerDocument = doc;
		el.className = "";
	}

	private static HTMLElement createElement(String name) {
		switch(name.toLowerCase(Locale.ROOT)) {
		case "script": return new HTMLScriptElement();
		case "div": return new HTMLDivElement();
		case "canvas": return new HTMLCanvasElement();
		case "img": return new HTMLImageElement();
		case "table": return new HTMLTableElement();
		case "tr": return new HTMLTableRowElement();
		case "td": return new HTMLTableCellElement();
		case "input": return new HTMLInputElement();
		case "thead":
		case "tfoot":
		case "tbody": return new HTMLTableSectionElement();
		case "body": return new HTMLBodyElement();
		case "colgroup":
		case "col": return new HTMLTableColElement();
		case "button": return new HTMLButtonElement();
		}
		return new HTMLElement();
	}

	private static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);
		Field modifiersField = null;
		try {
			modifiersField = Field.class.getDeclaredField("modifiers");
		} catch (NoSuchFieldException e) {
			try {
				Method getDeclaredFields0 = Class.class.getDeclaredMethod(
						"getDeclaredFields0", boolean.class);
				boolean accessibleBeforeSet = getDeclaredFields0.isAccessible();
				getDeclaredFields0.setAccessible(true);
				Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
				getDeclaredFields0.setAccessible(accessibleBeforeSet);
				for (Field ff : fields) {
					if ("modifiers".equals(ff.getName())) {
						modifiersField = ff;
						break;
					}
				}
				if (modifiersField == null) {
					throw e;
				}
			} catch (NoSuchMethodException | InvocationTargetException ex) {
				e.addSuppressed(ex);
				throw e;
			}
		}
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}
}
