package org.geogebra.web.full.gui.exam;

import java.util.ArrayList;

import org.geogebra.common.main.exam.restriction.ExamRegion;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.Label;

/**
 * Dialog to enter in graphing or cas calc exam mode
 */
public class ExamStartDialog extends ComponentDialog {

	private ExamRegion selectedRegion;

	/**
	 * @param app application
	 * @param data dialog transkeys
	 */
	public ExamStartDialog(AppWFull app, DialogData data) {
		super(app, data, false, true);
		addStyleName("examStartDialog");
		buildContent();
	}

	private void buildContent() {
		Label startText = new Label(app.getLocalization().getMenu("exam_start_dialog_text"));
		startText.addStyleName("examStartText");
		addDialogContent(startText);
		ArrayList<RadioButtonData> data = new ArrayList<>();
		Log.error(ExamRegion.values());
		for (ExamRegion region: ExamRegion.values()) {
			String displayName = region.getDisplayName(app.getLocalization());
			data.add(new RadioButtonData(displayName, region == ExamRegion.GENERIC,
					() -> selectedRegion = region));
		}
		RadioButtonPanel regionPicker = new RadioButtonPanel(app.getLocalization(),
				data);
		addDialogContent(regionPicker);
	}

	@Override
	public void onEscape() {
		if (!((AppW) app).getAppletParameters().getParamLockExam()) {
			hide();
		}
	}

	public ExamRegion getSelectedRegion() {
		return selectedRegion;
	}
}