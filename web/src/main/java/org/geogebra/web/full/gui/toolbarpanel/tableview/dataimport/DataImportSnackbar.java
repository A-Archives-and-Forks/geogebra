package org.geogebra.web.full.gui.toolbarpanel.tableview.dataimport;

import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_300;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_700;

import org.geogebra.common.awt.GColor;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentProgressBar;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

public class DataImportSnackbar extends FlowPanel {
	protected AppW appW;
	private Label titleLbl;
	private Timer fadeIn = new Timer() {
		@Override
		public void run() {
			addStyleName("fadeIn");
		}
	};
	private Timer fadeOut = new Timer() {
		@Override
		public void run() {
			removeStyleName("fadeIn");
			remove.schedule(2000);
		}
	};
	private Timer remove = new Timer() {
		@Override
		public void run() {
			removeFromParent();
		}
	};
	private Runnable cancelImport;
	private Runnable onImportFinished = () -> {
		if (fadeIn.isRunning()) {
			fadeIn.cancel();
		} else {
			fadeOut.run();
		}
	};

	/**
	 * data import snackbar
	 * @param appW - application
	 * @param title - file name
	 */
	public DataImportSnackbar(AppW appW, String title) {
		this.appW = appW;
		addStyleName("dataImporter");
		buildGui(title);
		appW.getAppletFrame().add(this);
		positionSnackbar();
		fadeIn.run();
	}

	/**
	 * data import error snackbar
	 * @param appW - application
	 * @param title - file name
	 * @param cancelImport - x button handler
	 * @param tryAgainRunnable - handler for try again button
	 */
	public DataImportSnackbar(AppW appW, String title, Runnable cancelImport,
			Command tryAgainRunnable) {
		this.appW = appW;
		this.cancelImport = cancelImport;
		addStyleName("dataImporter");
		addStyleName("error");
		buildErrorGui(title, tryAgainRunnable);
		appW.getAppletFrame().add(this);
		positionSnackbar();
		fadeIn.schedule(100);
		fadeOut.schedule(10000);
	}

	private void buildGui(String title) {
		addTitleHolder(title, NEUTRAL_300);

		ComponentProgressBar progressBar = new ComponentProgressBar(true, false);
		add(progressBar);
	}

	private void addTitleHolder(String title, GColor svgFiller) {
		FlowPanel titleHolder = new FlowPanel();
		titleHolder.addStyleName("titleHolder");

		Image dataImg = new Image(MaterialDesignResources.INSTANCE.upload_file().withFill(
				svgFiller.toString()).getSafeUri());
		titleLbl = new Label(title);

		titleHolder.add(dataImg);
		titleHolder.add(titleLbl);

		if (cancelImport != null) {
			StandardButton xButton = new StandardButton(MaterialDesignResources.INSTANCE.clear()
					.withFill(NEUTRAL_300.toString()), 24);
			xButton.addFastClickHandler(source -> {
				cancelImport.run();
				hide();
			});
			titleHolder.add(xButton);
		}

		add(titleHolder);
	}

	private void buildErrorGui(String title, Command tryAgainRunnable) {
		addTitleHolder(title, NEUTRAL_700);

		FlowPanel errorHolder = new FlowPanel();
		errorHolder.addStyleName("errorHolder");
		Label errorLbl = new Label(appW.getLocalization().getMenu("General.ImportFailed"));
		errorLbl.addStyleName("errorMsg");
		StandardButton tryAgain = new StandardButton(appW.getLocalization()
				.getMenu("phone_try_again_loading"));
		tryAgain.addFastClickHandler(source -> {
			hide();
			tryAgainRunnable.execute();
		});

		errorHolder.add(errorLbl);
		errorHolder.add(tryAgain);

		add(errorHolder);
	}

	public void hide() {
		fadeOut.run();
	}

	private void positionSnackbar() {
		addStyleName(appW.isPortrait() ? "portrait" : "landscape");
	}

	public Runnable getOnImportFinished() {
		return onImportFinished;
	}
}
