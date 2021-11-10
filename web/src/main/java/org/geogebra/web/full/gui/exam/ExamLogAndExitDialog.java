package org.geogebra.web.full.gui.exam;

import org.geogebra.common.main.exam.ExamLogBuilder;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Exam exit dialog with the whole information
 */
public class ExamLogAndExitDialog extends GPopupPanel {
	private final Runnable returnHandler;
	private FlowPanel contentPanel;
	private FlowPanel activityPanel;
	private final Widget anchor;

	public ExamLogAndExitDialog(AppW app, boolean isLogDialog,
			Widget anchor) {
		this(app, isLogDialog, null, anchor, "OK");
	}

	/**
	 * @param app
	 *            application
	 * @param isLogDialog
	 *            true if need to build log dialog
	 * @param returnHandler
	 *            return handler
	 * @param anchor
	 *            anchor
	 */
	public ExamLogAndExitDialog(AppW app, boolean isLogDialog,
			Runnable returnHandler, Widget anchor, String positiveKey) {
		super(app.getPanel(), app);
		this.returnHandler = returnHandler;
		this.anchor = anchor;
		this.setStyleName("dialogComponent");
		this.addStyleName(isLogDialog ? "examLogDialog" : "examExitDialog");
		buildGUI(isLogDialog, positiveKey);
	}

	private void buildGUI(boolean isLogDialog, String positiveKey) {
		FlowPanel titlePanel = buildTitlePanel();

		ScrollPanel scrollPanel = new ScrollPanel();
		contentPanel = new FlowPanel();
		contentPanel.setStyleName(app.getExam().isCheating() && isLogDialog
				? "contentPanel cheating" : "contentPanel");
		buildContent(isLogDialog);
		scrollPanel.add(contentPanel);

		FlowPanel buttonPanel = buildButtonPanel(isLogDialog, positiveKey);

		FlowPanel dialog = new FlowPanel();
		dialog.add(titlePanel);
		dialog.add(scrollPanel);
		dialog.add(buttonPanel);
		this.add(dialog);
	}

	private FlowPanel buildButtonPanel(boolean isLogDialog, String positiveKey) {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("dialogPanel");

		if ((app.getExam().isCheating() && !isLogDialog)
				|| (isLogDialog && activityPanel != null
				&& activityPanel.getWidgetCount() > 7)) {
			buttonPanel.addStyleName("withDivider");
		}
		StandardButton positiveBtn = new StandardButton(app.getLocalization()
				.getMenu(positiveKey));
		positiveBtn.addStyleName("dialogTextButton");
		positiveBtn.addFastClickHandler(ignored -> {
			if (isLogDialog) {
				hide();
			} else {
				hideAndExit();
			}
		});
		buttonPanel.add(positiveBtn);

		return buttonPanel;
	}

	private FlowPanel buildTitlePanel() {
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName("titlePanel");
		String calcStr = app.isUnbundled() ? app.getConfig().getAppTransKey()
				: "CreateYourOwn";
		Label calcType = new Label(app.getLocalization().getMenu(calcStr));
		calcType.setStyleName("calcType");
		Label examTitle = new Label(ExamUtil.status((AppW) app));
		examTitle.setStyleName("examTitle");
		titlePanel.add(calcType);
		if (app.getExam().isCheating()) {
			titlePanel.addStyleName("cheating");
			NoDragImage alertImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.exam_error(), 24);
			titlePanel.add(LayoutUtilW.panelRowIndent(alertImg, examTitle));
		} else {
			if (((AppW) app).getAppletParameters().getParamLockExam()) {
				titlePanel.addStyleName("locked");
			}
			titlePanel.add(examTitle);
		}
		return titlePanel;
	}

	private void buildContent(boolean isLogDialog) {
		Label teacherText = new Label(app.getLocalization()
				.getMenu("exam_log_show_screen_to_teacher"));
		teacherText.setStyleName("textStyle");
		if (!isLogDialog) {
			contentPanel.add(teacherText);
			Label durationLbl = new Label(app.getLocalization().getMenu("Duration"));
			Label duration = new Label(app.getExam().getElapsedTimeLocalized());
			contentPanel.add(buildBlock(durationLbl, duration));
		}
		Label dateLbl = new Label(app.getLocalization().getMenu("exam_start_date"));
		Label date = new Label(app.getExam().getDate());
		contentPanel.add(buildBlock(dateLbl, date));
		Label startTimeLbl = new Label(app.getLocalization().getMenu("exam_start_time"));
		Label startTime = new Label(app.getExam().getStartTime());
		contentPanel.add(buildBlock(startTimeLbl, startTime));
		if (!isLogDialog) {
			Label endTimeLbl = new Label(app.getLocalization().getMenu("exam_end_time"));
			Label endTime = new Label(app.getExam().getEndTime());
			contentPanel.add(buildBlock(endTimeLbl, endTime));
		}
		if (app.getExam().isCheating()) {
			activityPanel = buildActivityPanel(isLogDialog);
			Label activityLbl = new Label(app.getLocalization().getMenu("exam_activity"));
			contentPanel.add(buildBlock(activityLbl, activityPanel));
		}
	}

	private FlowPanel buildActivityPanel(boolean isLogDialog) {
		activityPanel = new FlowPanel();
		app.getExam()
				.appendLogTimes(app.getLocalization(), new ExamLogBuilder() {
					@Override
					public void addLine(StringBuilder sb) {
						addActivity(new Label(sb.toString()));
					}
				}, !isLogDialog);
		return activityPanel;
	}

	/**
	 * @param label
	 *            activity row
	 */
	protected void addActivity(Label label) {
		label.setStyleName("textStyle");
		activityPanel.add(label);
	}

	private static FlowPanel buildBlock(Widget caption, Widget text) {
		FlowPanel block = new FlowPanel();
		caption.setStyleName("captionStyle");
		text.setStyleName("textStyle");
		block.add(caption);
		block.add(text);
		return block;
	}

	@Override
	public void show() {
		super.show();
		super.center();
		if (anchor != null) {
			anchor.addStyleName("selected");
		}
	}

	private void hideAndExit() {
		if (app.getGuiManager() instanceof GuiManagerW
				&& ((GuiManagerW) app.getGuiManager())
				.getUnbundledToolbar() != null) {
			((GuiManagerW) app.getGuiManager()).getUnbundledToolbar()
					.resetHeaderStyle();
		}
		((AppW) app).getLAF().toggleFullscreen(false);
		hide();
		returnHandler.run();
	}

	/**
	 * remove selected style of anchor btn
	 */
	public void removeSelection() {
		if (anchor != null) {
			anchor.removeStyleName("selected");
		}
	}

	@Override
	public void hide() {
		super.hide();
		removeSelection();
	}
}
