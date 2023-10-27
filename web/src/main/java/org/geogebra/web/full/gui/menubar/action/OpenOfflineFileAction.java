package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.gui.menubar.FileChooser;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.BrowserStorage;

public class OpenOfflineFileAction extends DefaultMenuAction<Void>  {

	private FileChooser fileChooser;

	@Override
	public void execute(Void item, final AppWFull app) {
		if (!app.getLoginOperation().isLoggedIn()) {
			BrowserStorage.SESSION.setItem("saveAction", "openOfflineFile");
			app.getSaveController().showDialogIfNeeded(obj -> onOpenFile(app), false);
		} else {
			onOpenFile(app);
		}
	}

	private void onOpenFile(final AppWFull app) {
		if (fileChooser == null) {
			fileChooser = new FileChooser(app);
			fileChooser.addStyleName("hidden");
		}
		app.getAppletFrame().add(fileChooser);
		fileChooser.open();
	}
}
