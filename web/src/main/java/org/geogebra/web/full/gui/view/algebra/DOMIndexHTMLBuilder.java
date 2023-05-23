package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.App;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;

/**
 * Index builder that creates SUB elements in DOM directly
 * 
 * @author Zbynek
 *
 */
public final class DOMIndexHTMLBuilder extends IndexHTMLBuilder {
	private final Widget w;
	private final App app;
	private HTMLElement sub = null;

	/**
	 * @param w
	 *            parent widget
	 * @param app
	 *            application (for font size)
	 */
	public DOMIndexHTMLBuilder(Widget w, App app) {
		super(false);
		this.w = w;
		this.app = app;
	}

	@Override
	public void append(String s) {

		if (sub == null) {
			w.getElement()
					.appendChild(DomGlobal.document.createTextNode(s));
		} else {
			sub.appendChild(DomGlobal.document.createTextNode(s));
		}
	}

	@Override
	public void startIndex() {
		sub = DOM.createElement("sub");
		sub.style.fontSize = CSSProperties.FontSizeUnionType.of(
				(app.getFontSize() * 0.8) + "px");
	}

	@Override
	public void endIndex() {
		if (sub != null) {
			w.getElement().appendChild(sub);
		}
		sub = null;
	}

	@Override
	public String toString() {
		if (sub != null) {
			endIndex();
		}
		return w.getElement().innerHTML;
	}

	@Override
	public void clear() {
		DOM.removeAllChildren(w.getElement());
		sub = null;
	}

	@Override
	public boolean canAppendRawHtml() {
		return false;
	}

	@Override
	public void appendHTML(String str) {
		append(str);
	}
}