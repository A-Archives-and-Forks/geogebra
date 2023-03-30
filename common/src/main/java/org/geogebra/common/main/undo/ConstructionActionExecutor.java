package org.geogebra.common.main.undo;

import static org.geogebra.common.euclidian.StrokeSplitHelper.DEL;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.ActionType;

public class ConstructionActionExecutor
		implements ActionExecutor {

	private final App app;

	public ConstructionActionExecutor(App app) {
		this.app = app;
	}
	
	@Override
	public boolean executeAction(ActionType action, String... args) {
		if (action == ActionType.REMOVE) {
			for (String arg: args) {
				app.getGgbApi().deleteObject(arg);
			}
		} else if (action == ActionType.ADD) {
			for (String arg: args) {
				evalXML(arg);
			}
			app.getActiveEuclidianView().invalidateDrawableList();
		} else if (action == ActionType.UPDATE) {
			for (String arg: args) {
				if (arg.charAt(0) == '<') {
					evalXML(arg);
				} else if (arg.startsWith(DEL)) {
						app.getGgbApi().deleteObject(arg.substring(DEL.length()));
					}
				else {
					app.getGgbApi().evalCommand(arg);
				}
			}
		} else if (action == ActionType.SPLIT_STROKE || action == ActionType.MERGE_STROKE) {
			for (String arg: args) {
				if (arg.startsWith(DEL)) {
					app.getGgbApi().deleteObject(arg.substring(DEL.length()));
				} else  {
					evalXML(arg);
				}
			}
			app.getActiveEuclidianView().invalidateDrawableList();
			return true;
		} else {
			return false;
		}
		return true;
	}

	private void evalXML(String arg) {
		app.getGgbApi().evalXML(arg);
		app.getKernel().notifyRepaint();
	}
}
