package org.geogebra.web.full.gui.app;

import org.geogebra.common.main.exam.ExamLogBuilder;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.HTML;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLDivElement;

/**
 * HTML builder for exam logs
 * 
 * @author Zbynek
 *
 */
public class HTMLLogBuilder extends ExamLogBuilder {
	private HTML html;

	/**
	 * Default constructor.
	 */
	public HTMLLogBuilder() {
		this.html = new HTML();
	}

	@Override
	public void addLine(StringBuilder sb) {
		addLineEl(sb.toString());
	}

	private HTMLDivElement addLineEl(String string) {
		HTMLDivElement div = DOM.createDiv();
		div.textContent = string;
		html.getElement().appendChild(div);
		return div;
	}

	@Override
	public void addField(String name, String value) {
		HTMLDivElement nameEl = addLineEl(name);
		nameEl.style.color ="rgba(0,0,0,0.54)";
		nameEl.style.fontSize = CSSProperties.FontSizeUnionType.of("75%");
		addLineEl(value);
	}

	/**
	 * @return HTML content
	 */
	public HTML getHTML() {
		return html;
	}

}
