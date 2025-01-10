package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

import elemental2.dom.DomGlobal;

/**
 * Shows the tutorial.
 */
public class ShowTutorialsAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		String url = app.getLocalization().getTutorialURL(app.getConfig());
		DomGlobal.window.open(url, "_blank", "");
	}
}
