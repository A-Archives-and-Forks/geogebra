package org.geogebra.web.shared;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.main.undo.UndoRedoButtonsController;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.gwtutil.SafeExamBrowser;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconProvider;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.view.button.ActionButton;
import org.geogebra.web.shared.view.button.DisappearingActionButton;
import org.gwtproject.animation.client.AnimationScheduler;
import org.gwtproject.animation.client.AnimationScheduler.AnimationCallback;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Display;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HTML;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;

/**
 * Singleton representing external header bar of unbundled apps.
 */
public class GlobalHeader implements EventRenderable {
	/**
	 * Singleton instance.
	 */
	public static final GlobalHeader INSTANCE = new GlobalHeader();

	private ProfileAvatar profilePanel;
	private Element signIn;
	private MenuToggleButton menuBtn;
	private AppW app;
	private Label timer;
	private StandardButton examInfoBtn;

	private boolean shareButtonInitialized;
	private boolean assignButtonInitialized;
	private @CheckForNull FlowPanel examTypeHolder;

	/**
	 * Activate sign in button in external header
	 *
	 * @param appW
	 *            application
	 */
	public void addSignIn(final AppW appW) {
		this.app = appW;
		signIn = RootPanel.get("signInTextID").getElement().getParentElement();
		if (signIn == null) {
			return;
		}

		registerSignInButtonsAsFocusable();

		Dom.addEventListener(signIn, "click", (e) -> {
			appW.getSignInController().login();
			e.stopPropagation();
			e.preventDefault();
		});
		app.getLoginOperation().getView().add(this);
	}

	private void registerSignInButtonsAsFocusable() {
		registerSignInButton("signInTextID");
		registerSignInButton("signInIconID");
	}

	private void registerSignInButton(String id) {
		final RootPanel signInButton = RootPanel.get(id);
		if (signInButton != null) {
			registerFocusable(app, AccessibilityGroup.SIGN_IN, signInButton);
		}
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent
				&& ((LoginEvent) event).isSuccessful()) {
			if (profilePanel == null) {
				profilePanel = new ProfileAvatar(app);
			}
			updateSignInnVisibility(true);
			profilePanel.setVisible(true);
			profilePanel.update(((LoginEvent) event).getUser());

			Element profile = Dom.createDefaultButton();
			profile.setId("profileId");
			signIn.getParentElement().appendChild(profile);

			getProfileRootPanel().add(profilePanel);
			registerFocusable(app, AccessibilityGroup.AVATAR, getProfileRootPanel());
			Dom.addEventListener(profile, "click", (e) -> {
				profilePanel.togglePopup();
				e.stopPropagation();
				e.preventDefault();
			});
		}
		if (event instanceof LogOutEvent) {
			profilePanel.setVisible(false);
			updateSignInnVisibility(false);
		}
	}

	private RootPanel getProfileRootPanel() {
		return RootPanel.get("profileId");
	}

	private void updateSignInnVisibility(boolean hide) {
		if (hide) {
			signIn.addClassName("hidden");
			getSignInText().addStyleName("hideButton");
			getSignInIcon().addStyleName("hideButton");
		} else {
			signIn.removeClassName("hidden");
			getSignInText().removeStyleName("hideButton");
			getSignInIcon().removeStyleName("hideButton");
		}
	}

	private RootPanel getSignInText() {
		return RootPanel.get("signInTextID");
	}

	private RootPanel getSignInIcon() {
		return RootPanel.get("signInIconID");
	}

	/**
	 * @param callback - click callback
	 * @param app - application
	 */
	public void initShareButton(final AsyncOperation<Widget> callback, AppW app) {
		final RootPanel shareBtn = getShareButton();
		if (shareBtn != null && !shareButtonInitialized) {
			shareButtonInitialized = true;
			registerFocusable(app, AccessibilityGroup.SHARE, shareBtn);
			Dom.addEventListener(shareBtn.getElement(), "click", (e) -> {
				callback.callback(shareBtn);
				e.stopPropagation();
				e.preventDefault();
			});
		}
	}

	/**
	 * Initialize assignment button
	 * @param onClick click handler
	 */
	public void initAssignButton(final Runnable onClick, AppW app) {
		final RootPanel assignButton = getAssignButton();
		if (assignButton != null && !assignButtonInitialized) {
			registerFocusable(app, AccessibilityGroup.ASSIGN, assignButton);
			assignButtonInitialized = true;
			Dom.addEventListener(assignButton.getElement(), "click", (e) -> {
				onClick.run();
				e.stopPropagation();
				e.preventDefault();
			});
		}
	}

	private RootPanel getAssignButton() {
		return RootPanel.get("assignButton");
	}

	private static RootPanel getShareButton() {
		return RootPanel.get("shareButton");
	}

	/**
	 * Get element, NOT panel to make sure root panels are not nested
	 *
	 * @return element containing the buttons in header
	 */
	public static Element getButtonElement() {
		return Document.get().getElementById("buttonsID");
	}

	/**
	 * @return panel of exam timer
	 */
	public RootPanel getExamPanel() {
		return RootPanel.get("examId");
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @return exam timer
	 */
	public Label getTimer() {
		return timer;
	}

	/**
	 * @return exam info button
	 */
	public StandardButton getExamInfoBtn() {
		return examInfoBtn;
	}

	/**
	 * Initialize the settings, undo and redo buttons if they are on the header
	 */
	public void initButtonsIfOnHeader() {
		if (app != null) {
			initSettingButtonIfOnHeader();
			initUndoRedoButtonsIfOnHeader();
		}
	}

	private void initSettingButtonIfOnHeader() {
		ActionButton settingsButton = getActionButton("settingsButton");
		if (settingsButton != null) {
			setTitle(settingsButton, "Settings");
			settingsButton.setAction(() -> app.getGuiManager().showSciSettingsView());
		}
	}

	private void setTitle(ActionButton settingsButton, String string) {
		settingsButton.setTitle(string);
		app.getLocalization().registerLocalizedUI(settingsButton);
	}

	private void initUndoRedoButtonsIfOnHeader() {
		ActionButton undoButton = getUndoButton();
		ActionButton redoButton = getRedoButton();
		if (undoButton != null && redoButton != null) {
			UndoRedoButtonsController.addUndoRedoFunctionality(undoButton,
					redoButton, app.getKernel());
		}
	}

	private ActionButton getUndoButton() {
		ActionButton undoButton = getActionButton("undoButton");
		if (undoButton != null) {
			setTitle(undoButton, "Undo");
		}
		return undoButton;
	}

	private ActionButton getRedoButton() {
		ActionButton undoButton = getDisappearingActionButton("redoButton");
		if (undoButton != null) {
			setTitle(undoButton, "Redo");
		}
		return undoButton;
	}

	private ActionButton getActionButton(String viewId) {
		RootPanel view = getViewById(viewId);
		if (view != null) {
			return new ActionButton(app, view);
		}
		return null;
	}

	private DisappearingActionButton getDisappearingActionButton(String viewId) {
		RootPanel view = getViewById(viewId);
		if (view != null) {
			return new DisappearingActionButton(app, view);
		}
		return null;
	}

	private static RootPanel getViewById(String viewId) {
		return RootPanel.get(viewId);
	}

	/**
	 * remove exam timer and put back button panel
	 */
	public void resetAfterExam() {
		if (getButtonElement() == null) {
			return;
		}
		getExamPanel().getElement().removeFromParent();
		getButtonElement().getStyle().clearDisplay();
		onResize();
	}

	/**
	 * switch right buttons with exam timer and info button
	 */
	public void addExamTimer() {
		if (getButtonElement() == null) {
			return;
		}
		// remove other buttons
		getButtonElement().getStyle().setDisplay(Display.NONE);

		// exam panel with timer and info btn
		Image timerImg = new Image(MaterialDesignResources.INSTANCE.timer()
				.getSafeUri().asString());
		timerImg.addStyleName("timerImg");
		timer = new Label("0:00");
		timer.setStyleName("examTimer");
		examInfoBtn = new StandardButton(
				SharedResources.INSTANCE.info_black(), null, 24);
		examInfoBtn.addStyleName("flatButtonHeader");
		examInfoBtn.addStyleName("examInfoBtn");
		// add exam panel to
		Element exam = DOM.createDiv();
		exam.setId("examId");
		getButtonElement().getParentElement().appendChild(exam);
		// The link should be disabled in all exam-capable apps since APPS-3289, but make sure
		Dom.querySelector("#logoID").setAttribute("href", "#");
		RootPanel examId = RootPanel.get("examId");
		examId.addStyleName("examPanel");

		if (SafeExamBrowser.get() != null && SafeExamBrowser.get().security != null) {
			SafeExamBrowser.SebSecurity security = SafeExamBrowser.get().security;
			String hash = security.configKey.substring(0, 8);
			security.updateKeys((ignore) ->
					addExamType("Safe Exam Browser (" + hash + ")"));
		} else if (!app.getExam().isRestrictedGraphExam()) {
			addExamType(app.getExam().getCalculatorNameForHeader());
		}

		examId.add(timerImg);
		examId.add(timer);
		examId.add(examInfoBtn);
		// run timer
		AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			@Override
			public void execute(double timestamp) {
				if (getApp().getExam() != null) {
					if (getApp().getExam().isCheating()) {
						getApp().getGuiManager()
								.setUnbundledHeaderStyle("examCheat");
						if (examTypeHolder != null) {
							examTypeHolder.addStyleName("cheat");
						}
					}
					getTimer().setText(
							getApp().getExam().getElapsedTimeLocalized());
					AnimationScheduler.get().requestAnimationFrame(this);
				}
			}
		});
		onResize();
	}

	private void addExamType(String examTypeName) {
		HTML examImg = new HTML(DefaultMenuIconProvider.INSTANCE.assignment().getSVG());
		examImg.setStyleName("examTypeIcon");
		Label examType = new Label(examTypeName);
		examType.setStyleName("examType");
		FlowPanel examTypePanel = new FlowPanel();
		examTypePanel.getElement().setId("examTypeId");
		examTypePanel.addStyleName("examTypePanel");
		if (app != null && app.isLockedExam()) {
			examTypePanel.addStyleName("locked");
		}
		examTypePanel.add(examImg);
		examTypePanel.add(examType);
		this.examTypeHolder = examTypePanel;
		RootPanel.get("examId").add(examTypePanel);
	}

	/**
	 * Show/hide apps picker as needed
	 */
	public static void onResize() {
		Function resize = GeoGebraGlobal.getGgbHeaderResize();
		if (resize != null) {
			resize.call();
		}
	}

	/**
	 * Update style of share button, NPE safe.
	 *
	 * @param selected
	 *            whether to mark share button as selected
	 */
	public void selectShareButton(boolean selected) {
		final RootPanel rp = getShareButton();
		if (rp != null) {
			Dom.toggleClass(rp, "selected", selected);
		}
	}

	/**
	 * Initialize without creating any buttons.
	 *
	 * @param app
	 *            application
	 */
	public void setApp(AppW app) {
		this.app = app;
	}

	/**
	 * @return whether there is a header in DOM
	 */
	public static boolean isInDOM() {
		return RootPanel.get("logoID") != null;
	}

	/**
	 * update ui on language change
	 */
	public void setLabels() {
		if (profilePanel != null) {
			profilePanel.setLabels();
		}
		if (menuBtn != null) {
			menuBtn.setLabel();
		}
		if (getAssignButton() != null && app != null) {
			getAssignButton().getElement().setInnerText(app.getLocalization()
					.getMenu("assignButton.title"));
		}
	}

	public void setMenuBtn(MenuToggleButton menuBtn) {
		this.menuBtn = menuBtn;
	}

	public void initLogo(AppW app) {
		RootPanel logo = RootPanel.get("logoID");
		registerFocusable(app, AccessibilityGroup.GEOGEBRA_LOGO, logo);
		Dom.addEventListener(logo.getElement(), "click", (e) -> {
			e.stopPropagation();
			e.preventDefault();
			String link = logo.getElement().getAttribute("href");
			DomGlobal.window.open(link, "_self");
		});
	}

	private void registerFocusable(AppW app, AccessibilityGroup group, Widget widget) {
		if (widget != null && app != null) {
			new FocusableWidget(group, null, widget).attachTo(app);
		}
	}
}
