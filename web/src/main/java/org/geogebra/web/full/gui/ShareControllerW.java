package org.geogebra.web.full.gui;

import java.util.function.Consumer;

import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.ShareController;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.multiplayer.MultiplayerResources;
import org.geogebra.web.full.gui.browser.CollaborationStoppedDialog;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.util.SaveDialogI;
import org.geogebra.web.full.util.GGBMultiplayer;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.util.StringConsumer;
import org.geogebra.web.shared.ShareDialogMow;
import org.geogebra.web.shared.ShareLinkDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * If no existent material -> show save dialog and ask for title to save
 * 
 * If material existent -> always auto save before share
 * 
 * Share with group -> material stays private (visibility)
 * 
 * Share by link -> material will be shared (visibility)
 * 
 * @author laszlo
 *
 */
public class ShareControllerW implements ShareController {

	private AppW app;
	private Widget anchor = null;
	private GGBMultiplayer multiplayer;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            the application.
	 */
	public ShareControllerW(AppW app) {
		this.app = app;
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return app;
	}

	@Override
	public void share() {
		AsyncOperation<Boolean> shareCallback = getShareCallback();
		// not saved as material yet
		Material activeMaterial = app.getActiveMaterial();
		boolean untitled = activeMaterial == null
				|| activeMaterial.getType() == Material.MaterialType.ggsTemplate;
		if (untitled || "P".equals(activeMaterial.getVisibility())) {
			if (!app.getLoginOperation().isLoggedIn()) {
				// not saved, not logged in
				loginForShare();
			} else {
				// not saved, logged in
				if (untitled) {
				    saveUntitledMaterial(shareCallback);
				} else {
					autoSaveMaterial(shareCallback);
				}
			}
		} else {
			// share public or shared material but not logged in
			if (!app.getLoginOperation().isLoggedIn()) {
				// not saved, not logged in
				loginForShare();
			}
			// auto save changes of existent material before share
			else if (app.getActiveMaterial() != null
					&& app.getLoginOperation().isLoggedIn()) {
				autoSaveMaterial(shareCallback);
			}
		}
	}

	/**
	 * Create material and save online
	 */
	private void saveUntitledMaterial(AsyncOperation<Boolean> shareCallback) {
		SaveDialogI saveDialog = ((DialogManagerW) app.getDialogManager())
				.getSaveDialog(false, false);
		((SaveControllerW) app.getSaveController())
				.showDialogIfNeeded(shareCallback, true, anchor,
						false, false);
		saveDialog.setDiscardMode();
	}

	private void autoSaveMaterial(AsyncOperation<Boolean> shareCallback) {
		if (app.isSaved()) {
			shareCallback.callback(true);
		} else {
			app.getSaveController().saveActiveMaterial(shareCallback);
		}
	}

	private void loginForShare() {
		app.getLoginOperation().getView().add(event -> {
			if (event instanceof LoginEvent && ((LoginEvent) event).isSuccessful()) {
				share();
			}
		});
		app.getLoginOperation().showLoginDialog();
	}

	private AsyncOperation<Boolean> getShareCallback() {
		return new AsyncOperation<Boolean>() {
			protected ShareLinkDialog shareDialog;
			protected ShareDialogMow shareDialogMow;

			@Override
			public void callback(Boolean active) {
				if (!active) {
					return;
				}

				String sharingKey = "";
				Material activeMaterial = getAppW().getActiveMaterial();
				if (activeMaterial != null && activeMaterial.getSharingKey() != null) {
					sharingKey = activeMaterial.getSharingKey();
				}
				if (getAppW().isMebis()) {
					DialogData data = new DialogData("Share", "Cancel", "Save");
					shareDialogMow = new ShareDialogMow(getAppW(), data,
							getAppW().getCurrentURL(sharingKey, true),
							null);
					shareDialogMow.show();
				} else {
					DialogData data = new DialogData("Share",
							null, null);
					shareDialog = new ShareLinkDialog(getAppW(), data,
							getAppW().getCurrentURL(sharingKey, true),
							getAnchor());
					shareDialog.show();
					shareDialog.center();
				}
			}
		};
	}

	/**
	 * 
	 * @return handler for native sharing
	 */
	public StringConsumer getShareStringHandler() {
		return s -> {
			String title = getAppW().getKernel().getConstruction()
					.getTitle();
			MaterialsManagerI fm = getAppW().getFileManager();
			fm.nativeShare(s, "".equals(title) ? "construction" : title);
		};
	}

	/**
	 * 
	 * @return anchor widget.
	 */
	public Widget getAnchor() {
		return anchor;
	}

	/**
	 * 
	 * @param anchor
	 *            widget to set.
	 */
	public void setAnchor(Widget anchor) {
		this.anchor = anchor;
	}

	@Override
	public void getBase64() {
		app.getGgbApi().getBase64(true, getShareStringHandler());
	}

	@Override
	public void startMultiuser(String sharingKey) {
		onMultiplayerLoad(sharingKey, ((ScriptManagerW) app.getScriptManager()).getApi(),
				mp -> {
					multiplayer = Js.uncheckedCast(mp);
					multiplayer.addUserChangeListener(this::handleMultiuserChange);
					multiplayer.start(app.getLoginOperation().getUserName());
				});
	}

	@Override
	public void terminateMultiuser(String sharingKey) {
		Material activeMaterial = app.getActiveMaterial();
		if (multiplayer != null && activeMaterial != null
				&& activeMaterial.getSharingKey().equals(sharingKey)) {
			multiplayer.terminate();
			multiplayer = null;
		} else {
			// temporary instance, do not store
			onMultiplayerLoad(sharingKey, null,
					mp -> Js.<GGBMultiplayer>uncheckedCast(mp).terminate());
		}
	}

	@Override
	public void disconnectMultiuser() {
		if (multiplayer != null) {
			multiplayer.disconnect();
			multiplayer = null;
		}
	}

	private void handleMultiuserChange(JsArray<Object> users) {
		if (users.length == 0) {
			CollaborationStoppedDialog dialog = new CollaborationStoppedDialog(app);
			dialog.show();
		}
		if (GeoGebraGlobal.getGgbMultiplayerChange() != null) {
			GeoGebraGlobal.getGgbMultiplayerChange().call(DomGlobal.window, users);
		}
	}

	private void onMultiplayerLoad(String sharingKey, Object api, Consumer<Object> callback) {
		GeoGebraTubeUser loggedInUser =
				app.getLoginOperation().getModel().getLoggedInUser();
		GWT.runAsync(GGBMultiplayer.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Log.error("Multiplayer script failed to load");
			}

			@Override
			public void onSuccess() {
				String paramMultiplayerUrl = app.getAppletParameters().getParamMultiplayerUrl();
				JavaScriptInjector.inject(MultiplayerResources.INSTANCE.multiplayer());
				JsPropertyMap<?> config = JsPropertyMap.of("collabUrl", paramMultiplayerUrl);

				GGBMultiplayer multiplayer = new GGBMultiplayer(api, sharingKey, config,
						loggedInUser.getJWTToken());
				callback.accept(multiplayer);
			}
		});
	}
}
