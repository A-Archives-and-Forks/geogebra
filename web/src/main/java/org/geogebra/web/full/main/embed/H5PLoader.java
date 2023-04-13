package org.geogebra.web.full.main.embed;

import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.gwtutil.ScriptLoadCallback;
import org.geogebra.web.html5.util.h5pviewer.H5PPaths;

/**
 * 
 * Loader for H5P
 *
 */
public class H5PLoader {
	public static final H5PLoader INSTANCE = new H5PLoader();
	private boolean loaded = false;
	private boolean loadingStarted = false;

	public boolean isLoaded() {
		return loaded;
	}

	public boolean isLoadingStarted() {
		return loadingStarted;
	}

	/**
	 * Loads H5P library
	 *
	 * @param onLoadCallback to run after success.
	 */
	public void loadIfNeeded(Runnable onLoadCallback) {
		if (loadingStarted) {
			return;
		}
		loadingStarted = true;
		JavaScriptInjector.loadJS(H5PPaths.MAIN_JS, new ScriptLoadCallback() {

			@Override
			public void onLoad() {
				Log.debug("[H5P] library is loaded");
				loaded = true;
				onLoadCallback.run();
			}

			@Override
			public void onError() {
				Log.warn("Could not load H5P Viewer");
			}

			@Override
			public void cancel() {
				// no need to cancel
			}
		});
	}

}
