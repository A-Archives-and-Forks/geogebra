package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.CardInfoPanel;
import org.geogebra.web.full.gui.browser.MaterialCardController;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Material card
 */
public class MaterialCard extends FlowPanel implements MaterialCardI {
	private AppW app;
	// image of material
	private FlowPanel imgPanel;
	// material information
	private CardInfoPanel infoPanel;
	private ContextMenuButtonMaterialCard moreBtn;
	private FlowPanel infoPanelContent;
	private MaterialCardController controller;

	/**
	 * @param m
	 *            material
	 * @param app
	 *            see {@link AppW}
	 */
	public MaterialCard(final Material m, final AppW app) {
		this.app = app;
		controller = new MaterialCardController(app);
		controller.setMaterial(m);
		initGui();
		this.addDomHandler(event -> openMaterial(), ClickEvent.getType());
	}

	/**
	 * Open this material.
	 */
	protected void openMaterial() {
		app.getGuiManager().getBrowseView().closeAndSave(obj -> controller.loadOnlineFile());
	}

	private void initGui() {
		this.setStyleName("materialCard");
		// panel containing the preview image of material
		imgPanel = new MaterialImagePanel(getMaterial());
		this.add(imgPanel);
		// panel containing the info regarding the material

		moreBtn = new ContextMenuButtonMaterialCard(app, getMaterial(), this);
		// panel for visibility state
		infoPanelContent = new FlowPanel();
		updateVisibility(getMaterial());
		infoPanel = new CardInfoPanel(getMaterial().getTitle(), infoPanelContent);

		infoPanel.add(moreBtn);
		this.add(infoPanel);
	}

	private boolean isOwnMaterial() {
		return app.getLoginOperation().getResourcesAPI().owns(getMaterial());
	}

	private String getCardAuthor() {
		return getMaterial().getCreator() != null
				? getMaterial().getCreator().getDisplayName()
				: "";
	}

	/**
	 * @return represented material
	 */
	Material getMaterial() {
		return controller.getMaterial();
	}

	@Override
	public void remove() {
		removeFromParent();
	}

	/**
	 * Actually delete the file.
	 */
	protected void onConfirmDelete() {
		controller.onConfirmDelete(this);
	}

	/**
	 * Change name on card and rename via API
	 * @param text new name
	 */
	public void rename(String text) {
		String oldTitle = infoPanel.getCardId();
		infoPanel.setCardId(text);
		controller.rename(text, this, oldTitle);
	}

	/**
	 * Call API to copy yhe material.
	 */
	public void copy() {
		controller.copy();
	}

	@Override
	public void onDelete() {
		DialogData data = new DialogData(null, "Cancel", "Delete");
		ComponentDialog removeDialog = new RemoveDialog(app, data, this);
		removeDialog.show();
		removeDialog.setOnPositiveAction(this::onConfirmDelete);
	}

	/**
	 * @return card title
	 */
	public String getCardTitle() {
		return getMaterial().getTitle();
	}

	/**
	 * @param material
	 *            material
	 */
	public void updateVisibility(Material material) {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;
		String visibility = material.getVisibility();
		if (material.isSharedWithGroup()) {
			visibility = "S";
		}
		NoDragImage visibiltyImg;
		Label visibilityTxt;
		if (material.isMultiuser()) {
			visibiltyImg = getMultiuserIcon();
			if (isOwnMaterial()) {
				visibilityTxt = new Label(app.getLocalization().getMenu("Collaborative"));
			} else {
				visibilityTxt = new Label(getCardAuthor());
			}
		} else if (!isOwnMaterial()) {
			visibiltyImg = null;
			visibilityTxt = new Label(getCardAuthor());
		} else {
			switch (visibility) {
			case "P":
				visibiltyImg = new NoDragImage(res.mow_card_private(), 24);
				visibilityTxt = new Label(app.getLocalization().getMenu("Private"));
				break;
			case "S":
				if (app.isMebis()) {
					visibiltyImg = new NoDragImage(res.mow_card_shared(), 24);
				} else {
					visibiltyImg = new NoDragImage(res.resource_card_shared(), 24);
				}
				visibilityTxt = new Label(app.getLocalization().getMenu("Shared"));
				break;
			case "O":
			default:
				visibiltyImg = new NoDragImage(res.mow_card_public(), 24);
				visibilityTxt = new Label(app.getLocalization().getMenu("Public"));
				break;
			}
		}
		infoPanelContent.clear();
		if (visibiltyImg != null) {
			infoPanelContent.setStyleName("visibilityPanel");
			infoPanelContent
					.add(LayoutUtilW.panelRow(visibiltyImg, visibilityTxt));
		} else {
			infoPanelContent.setStyleName("cardAuthor");
			infoPanelContent.add(visibilityTxt);
		}
	}

	private NoDragImage getMultiuserIcon() {
		return new NoDragImage(
				MaterialDesignResources.INSTANCE.mow_card_multiuser(), 24);
	}

	public void setLabels() {
		updateVisibility(getMaterial());
	}
}