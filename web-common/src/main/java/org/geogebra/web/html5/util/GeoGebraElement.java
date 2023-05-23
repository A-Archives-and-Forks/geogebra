package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.regexp.shared.MatchResult;
import org.geogebra.regexp.shared.RegExp;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLElement;
import elemental2.dom.ViewCSS;
import jsinterop.base.Js;

public final class GeoGebraElement implements AttributeProvider {

	private HTMLElement el;

	/**
	 * @param element
	 *            Assert, that the given {@link Element} is compatible with this
	 *            class and automatically typecast it.
	 * @return cast element
	 */
	public static GeoGebraElement as(HTMLElement element) {
		GeoGebraElement ge = new GeoGebraElement();
		ge.el = element;
		// tabindex -1 prevents slider reading on Android
		if (element != null && !Browser.isAndroid()) {
			element.tabIndex = -1;
		}
		return ge;
	}

	/**
	 * @return list of articles on the page that have the proper class (
	 *         {@value GeoGebraConstants#GGM_CLASS_NAME})
	 */
	public static ArrayList<GeoGebraElement> getGeoGebraMobileTags() {
		HTMLCollection<elemental2.dom.Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<GeoGebraElement> articleNodes = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			GeoGebraElement ae = GeoGebraElement.as(Js.uncheckedCast(nodes.getAt(i)));
			ae.initID(i);
			articleNodes.add(ae);
		}
		return articleNodes;
	}

	/**
	 * Set the ID of this article to something unique; prefer getDataParamId,
	 * append number in case of conflicts. If not set, use a string that
	 * contains i
	 *
	 * @param i
	 *            number for id if fdataParamId not set
	 */
	public void initID(int i) {
		AppletParameters params = new AppletParameters(this);
		String paramID = params.getDataParamId();
		if (paramID.equals(el.id)) {
			return;
		}
		if (paramID.length() > 0) {
			int suffix = 0;
			while (DOM.getElementById(paramID) != null) {
				paramID = params.getDataParamId() + suffix;
				suffix++;
			}
			el.id = paramID;
			return;
		}
		Date creationDate = new Date();
		el.id = GeoGebraConstants.GGM_CLASS_NAME + i + creationDate.getTime();
	}

	/**
	 * Create new article element
	 */
	protected GeoGebraElement() {
		// needed for GWT
	}

	public void clear() {
		el.innerHTML = "";
	}

	public static CSSStyleDeclaration getComputedStyle(HTMLElement element) {
		ViewCSS view = Js.cast(DomGlobal.window);
		return view.getComputedStyle(element);
	}

	/**
	 *
	 * @param element ui element
	 * @return primary color
	 */

	public String getPrimaryColor(HTMLElement element) {
		return getComputedStyle(element).getPropertyValue("--ggb-primary-color");
	}

	/**
	 *
	 * @return that the article element has (inherited) direction attribute
	 */

	public boolean isRTL() {
		return "rtl".equals(getComputedStyle(el).direction);
    }

	private double envScale(HTMLElement element, String type,
			boolean deep) {
        double sx = 1;
        double sy = 1;

        HTMLElement current = element;
        do {
            RegExp matrixRegex = RegExp.compile("matrix\\((-?\\d*\\.?\\d+),\\s*(-?\\d*\\.?\\d+),"
					+ "\\s*(-?\\d*\\.?\\d+),\\s*(-?\\d*\\.?\\d+),"
					+ "\\s*(-?\\d*\\.?\\d+),\\s*(-?\\d*\\.?\\d+)\\)");

            CSSStyleDeclaration style = getComputedStyle(current);

			String transform = style.transform;
			MatchResult matches = matrixRegex.exec(transform);

			if (matches != null) {
				sx *= length(matches, 1, 2);
				sy *= length(matches, 3, 4);
			} else if (transform.indexOf("scale") == 0) {
				double mul = Double.parseDouble(transform.substring(transform
					.indexOf("(") + 1, transform.indexOf(")")));
				sx *= mul;
				sy *= mul;
			}

			if (!StringUtil.empty((String) Js.asPropertyMap(style).get("zoom"))
					&& current != DomGlobal.document.body.parentElement) {
				double zoom = Double.parseDouble((String) Js.asPropertyMap(style).get("zoom"));
				sx *= zoom;
				sy *= zoom;
			}

            current = Js.uncheckedCast(current.parentElement);
        } while (deep && current != null);

        return "x".equals(type) ? sx : sy;
    }

	private static double length(MatchResult matches, int id1, int id2) {
		double a = Double.parseDouble(matches.getGroup(id1));
		double b = Double.parseDouble(matches.getGroup(id2));
		return Math.hypot(a, b);
	}

	private double envScale(String type) {
		return envScale(el, type, true);
	}

	/**
	 * @return get CSS scale of parent element
	 */

	public double getParentScaleX() {
		return envScale(Js.uncheckedCast(el.parentElement), "x", false);
	}

	/**
	 * Read scale value and cache it
	 *
	 * @return the CSS scale attached to the article element
	 */

	public double getScaleX() {
		// no instance fields in subclasses of Element, so no way to assign it
		// to
		// a simple field
		if (StringUtil.empty(el.getAttribute("data-scalex"))) {
			setAttribute("data-scalex", String.valueOf(envScale("x")));
		}
		return Double.parseDouble(el.getAttribute("data-scalex"));
	}

	/**
	 * Read cached scale value or compute it, do not cache it
	 *
	 * @return the CSS scale attached to the article element
	 */

	public double readScaleX() {
		if (StringUtil.empty(el.getAttribute("data-scalex"))) {
			return envScale("x");
		}
		return Double.parseDouble(el.getAttribute("data-scalex"));
	}

	/**
	 * @return the CSS scale attached to the article element
	 *
	 */

	public double getScaleY() {
		// no instance fields in subclasses of Element, so no way to asign it to
		// a simple field
		if (StringUtil.empty(el.getAttribute("data-scaley"))) {
			setAttribute("data-scaley", String.valueOf(envScale("y")));
		}
		return Double.parseDouble(el.getAttribute("data-scaley"));
	}

	/**
	 * Remove cached scale values
	 */

	public void resetScale() {
		setAttribute("data-scalex", "" + envScale("x"));
		setAttribute("data-scaley", "" + envScale("y"));
	}

	@Override
	public boolean hasAttribute(String attribute) {
		return el.hasAttribute(attribute);
	}

	@Override
	public void removeAttribute(String attribute) {
		el.removeAttribute(attribute);
	}

	@Override
	public void setAttribute(String attribute, String value) {
		el.setAttribute(attribute, value);
	}

	@Override
	public String getAttribute(String attribute) {
		return el.getAttribute(attribute);
	}

	public String getId() {
		return el.id;
	}

	public HTMLElement getElement() {
		return el;
	}

	public HTMLElement getParentElement() {
		return Js.uncheckedCast(el.parentElement);
	}

	public CSSStyleDeclaration getStyle() {
		return el.style;
	}
}
