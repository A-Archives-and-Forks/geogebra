package org.geogebra.web.full.gui.contextmenu;

import static org.geogebra.common.GeoGebraConstants.BAYERN_GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SCIENTIFIC_APPCODE;

import java.util.Locale;
import java.util.Set;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ContextMenuItemFactory;
import org.geogebra.web.full.gui.dialog.AppDescription;
import org.geogebra.web.full.gui.laf.BundleLookAndFeel;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

public class CalculatorSubMenu extends AriaMenuBar {

	private static final String BOARD_URL = "https://board.bycs.de";
	private final AppW app;
	private final ContextMenuItemFactory factory;
	private final EmbedManager embedManager;
	private final Set<SuiteSubApp> restrictions;
	private final String appOrExamModeName;

	/**
	 * Constructor
	 * @param app - application
	 */
	public CalculatorSubMenu(AppW app) {
		this.app = app;
		factory = new ContextMenuItemFactory();
		embedManager = app.getEmbedManager();
		ExamType examType = ExamType.byName(app.getAppletParameters().getParamFeatureSet());
		restrictions = examType == null ? Set.of()
				: ExamRestrictions.forExamType(examType).getDisabledSubApps();
		appOrExamModeName = examType == null
				? GeoGebraConstants.SUITE_APPCODE : examType.name().toLowerCase(Locale.ROOT);
		if (embedManager != null) {
			if (!app.isByCS() || examType != ExamType.BAYERN_GR) {
				addItem(SuiteSubApp.GRAPHING);
			}
			addItem(SuiteSubApp.G3D);
			addItem(SuiteSubApp.GEOMETRY);
			addItem(SuiteSubApp.CAS);
			addItem(SuiteSubApp.PROBABILITY);
			if (app.isByCS() && examType != ExamType.BAYERN_GR) {
				addItemWithButton(AppDescription.get(SuiteSubApp.SCIENTIFIC).getNameKey(),
						BOARD_URL + "/taschenrechner", () -> embedManager
								.addCalcWithPreselectedApp(appOrExamModeName, SCIENTIFIC_APPCODE));
			} else {
				addItem(SuiteSubApp.SCIENTIFIC);
			}

			if (app.isByCS()) {
				if (examType == null || examType == ExamType.BAYERN_GR) {
					addItemWithButton("Grafikrechner (Bayern)",
							BOARD_URL + "/grafikrechnerbayern",
							() -> embedManager.addCalcWithPreselectedApp(BAYERN_GRAPHING_APPCODE,
									GeoGebraConstants.GRAPHING_APPCODE));
				}
			}
		}
	}

	private void addItemWithButton(String itemText, String url,
			Scheduler.ScheduledCommand cmd) {
		FlowPanel itemHolder = new FlowPanel();
		itemHolder.addStyleName("itemWithButton");

		Label text = BaseWidgetFactory.INSTANCE.newPrimaryText(
				app.getLocalization().getMenu(itemText), "text");
		text.addClickHandler(event -> cmd.execute());
		itemHolder.add(text);

		if (!GlobalScope.examController.isExamActive()
				&& !(app.getLAF() instanceof BundleLookAndFeel)) {
			Image newTabImage = new NoDragImage(MaterialDesignResources
					.INSTANCE.open_in_new_tab().getSafeUri().asString());
			newTabImage.addClickHandler(event -> Browser.openWindow(url));
			itemHolder.add(newTabImage);
		}

		AriaMenuItem ariaMenuItem = new AriaMenuItem(itemHolder, () -> {});
		ariaMenuItem.addStyleName("ariaItemWithButton");
		addItem(ariaMenuItem);
	}

	private void addItem(SuiteSubApp subApp) {
		if (restrictions.contains(subApp)) {
			return;
		}
		AppDescription description = AppDescription.get(subApp);
		addItem(factory.newAriaMenuItem(null,
				app.getLocalization().getMenu(description.getNameKey()),
				() -> embedManager.addCalcWithPreselectedApp(appOrExamModeName,
						subApp.appCode)));
	}
}
