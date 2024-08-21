package org.geogebra.common.contextmenu;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

/**
 * Items contained in context menus
 */
public interface ContextMenuItem {
	/**
	 * @param localization Used for translating the title
	 * @return The title of the item with possible attributes for subscripts
	 */
	@Nonnull
	AttributedString getLocalizedTitle(@Nonnull Localization localization);

	/**
	 * @return The item's icon, or null if the context menu item does not have an icon
	 */
	@CheckForNull
	default Icon getIcon() {
		return null;
	}

	/**
	 * An identifier for the possible icon values
	 */
	enum Icon {
		Expression, Text, Help, Delete
	}
}
