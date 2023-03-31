package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.view.table.ScientificDataTableController;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.activity.GeoGebraActivity;
import org.geogebra.web.full.main.activity.ScientificActivity;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class DefineFunctionsDialogTV extends ComponentDialog {
	private MathTextFieldW fieldF;
	private MathTextFieldW fieldG;
	ScientificDataTableController controller;

	/**
	 * dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public DefineFunctionsDialogTV(AppW app, DialogData dialogData) {
		super(app, dialogData, false, true);

		GeoGebraActivity activity = ((AppWFull) app).getActivity();
		controller = ((ScientificActivity)activity).getTableController();

		addStyleName("defineFunctionsDialog");
		buildGUI();
		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}
		this.addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
		});
	}

	private void buildGUI() {
		fieldF = addFunctionRow("f(x) =");
		fieldG = addFunctionRow("g(x) =");
	}

	private MathTextFieldW addFunctionRow(String functionLbl) {
		FlowPanel functionPanel = new FlowPanel();
		functionPanel.addStyleName("functionPanel");

		Label funcLbl = new Label(functionLbl);
		functionPanel.add(funcLbl);

		MathTextFieldW funcField = new MathTextFieldW(app);
		functionPanel.add(funcField);
		addDialogContent(functionPanel);

		return funcField;
	}

	@Override
	public void onPositiveAction() {
		controller.defineFunctions(fieldF.getText(), fieldG.getText());
		if (!controller.hasFDefinitionErrorOccurred()
				&& !controller.hasGDefinitionErrorOccurred()) {
			hide();
			app.storeUndoInfo();
		}
	}

	@Override
	public void hide() {
		super.hide();
		app.hideKeyboard();
	}

	@Override
	public void show() {
		resetFields();
		showDirectly();
		fieldF.requestFocus();
		Scheduler.get().scheduleDeferred(() -> {
			super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
		});
	}

	/**
	 * reset fields from construction
	 */
	public void resetFields() {
		fieldF.setText(text(controller.getDefinitionOfF()));
		fieldG.setText(text(controller.getDefinitionOfG()));
	}

	private static String text(String string) {
		return string != null ? string : "";
	}
}
