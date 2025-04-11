package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.common.contextmenu.ContextMenuItem;
import org.geogebra.web.full.gui.contextmenu.ImageMap;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;

public class ContextMenuButtonDeleteCard extends ContextMenuButtonCard {
    private MaterialCardI card;

    /**
     * @param app application
     * @param card material card
     */
    public ContextMenuButtonDeleteCard(AppWFull app, MaterialCardI card) {
        super(app);
        this.card = card;
        initPopup();
    }

    @Override
    protected void initPopup() {
        super.initPopup();
        addDeleteItem();
    }

    private void addDeleteItem() {
        for (ContextMenuItem item : app.getContextMenuFactory().makeMaterialContextMenu()) {
            wrappedPopup.addItem(new AriaMenuItem(item.getLocalizedTitle(loc),
					ImageMap.get(item.getIcon()), this::onDelete));
        }
    }

    private void onDelete() {
        card.onDelete();
    }

    @Override
    protected void show() {
        super.show();
        wrappedPopup.show(this, 0, 0);
    }
}
