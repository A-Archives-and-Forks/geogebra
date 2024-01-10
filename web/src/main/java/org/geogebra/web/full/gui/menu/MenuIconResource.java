package org.geogebra.web.full.gui.menu;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.full.gui.menu.icons.MenuIconProvider;
import org.geogebra.web.html5.gui.view.IconSpec;

class MenuIconResource {

	private final MenuIconProvider menuIconProvider;

	MenuIconResource(MenuIconProvider menuIconProvider) {
		this.menuIconProvider = menuIconProvider;
	}

	IconSpec getImageResource(Icon icon) {
		return menuIconProvider.matchIconWithResource(icon);
	}

}
