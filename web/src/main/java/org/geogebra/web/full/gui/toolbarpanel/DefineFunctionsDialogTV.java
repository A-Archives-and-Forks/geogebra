package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.view.table.ScientificDataTableController;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class DefineFunctionsDialogTV extends ComponentDialog {
	private final ScientificDataTableController controller;
	private MathTextFieldW f;
	private MathTextFieldW g;

	/**
	 * dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public DefineFunctionsDialogTV(AppW app, DialogData dialogData) {
		super(app, dialogData, false, true);
		addStyleName("defineFunctionsDialog");
		buildGUI();
		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}
		this.addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
		});
		this.controller = new ScientificDataTableController(app.getKernel());
	}

	private void buildGUI() {
		f = addFunctionRow("f(x) =");
		g = addFunctionRow("g(x) =");
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
		boolean success = controller.defineFunctions(f.getText(), g.getText());
		setErrorState(f, controller.hasFDefinitionErrorOccurred());
		setErrorState(g, controller.hasGDefinitionErrorOccurred());
		if (!success) {
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
		f.requestFocus();
		Scheduler.get().scheduleDeferred(() -> {
			super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
		});
	}

	private void setErrorState(MathTextFieldW field, boolean error) {
		Dom.toggleClass(field.asWidget().getParent(), "error", error);
	}

	/**
	 * reset fields from construction
	 */
	public void resetFields() {
		f.setText(controller.getDefinitionOfF());
		g.setText(controller.getDefinitionOfG());
	}
}
