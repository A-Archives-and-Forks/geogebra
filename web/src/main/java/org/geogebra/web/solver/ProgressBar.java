package org.geogebra.web.solver;

import org.gwtproject.user.client.ui.HTML;

/**
 * Progressbar using native HTML5 element
 */
public class ProgressBar extends HTML {

	/**
	 * Create new progress bar.
	 */
	public ProgressBar() {
		super("<progress></progress>");
		addStyleName("practiceProgressBar");
	}

	public void setMax(int max) {
		getElement().firstElementChild.setAttribute("max", String.valueOf(max));
	}

	public void setValue(int value) {
		getElement().firstElementChild.setAttribute("value", String.valueOf(value));
	}

	public void setProgress(String progress) {
		getElement().firstElementChild.setAttribute("progress", progress);
	}
}
