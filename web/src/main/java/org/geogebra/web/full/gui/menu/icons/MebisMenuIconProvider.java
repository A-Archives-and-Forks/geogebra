package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;

/**
 * Gives access to Mebis menu icons.
 */
public class MebisMenuIconProvider extends DefaultMenuIconProvider {

	@Override
	public IconSpec matchIconWithResource(Icon icon) {
		switch (icon) {
		case CLEAR:
			return new FaIconSpec("fa-file");
		case SEARCH:
			return new FaIconSpec("fa-folder-open");
		}
		return super.matchIconWithResource(icon);
	}

	public static class FaIconSpec implements IconSpec {
		String name;

		public FaIconSpec(String name) {
			this.name = name;
		}

		@Override
		public Element toElement() {
			Element icon = DOM.createElement("I");
			icon.setClassName(name);
			icon.addClassName("fa-regular");
			return icon;
		}
	}
}