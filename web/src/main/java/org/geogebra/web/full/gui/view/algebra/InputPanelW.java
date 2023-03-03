package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.dialog.text.TextEditPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Creates an InputPanel for GeoGebraWeb
 */
public class InputPanelW extends FlowPanel {

	private AutoCompleteTextFieldW textComponent;
	private boolean showSymbolPopup;
	private TextEditPanel textAreaComponent;

	/**
	 * @param app
	 *            application
	 * @param columns
	 *            number of columns
	 * @param autoComplete
	 *            whether to allow autocomplete
	 */
	public InputPanelW(App app, int columns,
	        boolean autoComplete) {
		super();
		addStyleName("InputPanel");

		textComponent = new AutoCompleteTextFieldW(columns, app);
		textComponent.setAutoComplete(autoComplete);
		add(textComponent);
		enableGGBKeyboard(app, false, textComponent);
	}

	/**
	 * @param initText
	 *            initial text
	 * @param app
	 *            application
	 * @param columns
	 *            number of columns
	 * @param rows
	 *            number of rows
	 * @param showSymbolPopupIcon
	 *            whether to show symbol icon
	 */
	public InputPanelW(String initText, App app, int rows, int columns,
			boolean showSymbolPopupIcon) {

		this.showSymbolPopup = showSymbolPopupIcon;

		// set up the text component:
		// either a textfield or HTML textpane
		if (rows > 1) {
			textAreaComponent = new TextEditPanel(app);
			if (initText != null) {
				textAreaComponent.setText(initText);
			}
			add(textAreaComponent);
		} else {
			textComponent = new AutoCompleteTextFieldW(columns, app);
			textComponent.prepareShowSymbolButton(showSymbolPopup);
			if (initText != null) {
				textComponent.setText(initText);
			}
			add(textComponent);
		}

		if (textComponent != null) {
			AutoCompleteTextFieldW atf = textComponent;
			atf.setAutoComplete(false);

			enableGGBKeyboard(app, showSymbolPopupIcon, atf);
		}
	}

	private void enableGGBKeyboard(App app, boolean showKeyboardButton,
			AutoCompleteTextFieldW atf) {
		if (!app.isWhiteboardActive()) {
			atf.prepareShowSymbolButton(showKeyboardButton);
			atf.enableGGBKeyboard();
		}
	}

	/**
	 * @return single line editable field
	 */
	public AutoCompleteTextFieldW getTextComponent() {
		return textComponent;
	}

	/**
	 * sets focus into textfield and selects the content
	 */
	public void setFocusAndSelectAll() {
		getTextComponent().setFocus(true);
		getTextComponent().selectAll();
	}

	/**
	 * @return multiline editable field
	 */
	public TextEditPanel getTextAreaComponent() {
		return textAreaComponent;
	}

	/**
	 * @return text
	 */
	public String getText() {
		if (textComponent != null) {
			return textComponent.getText();
		}
		return textAreaComponent.getText();
	}

	/**
	 * adds KeyUpHandler to TextComponent
	 */
	public void addTextComponentKeyUpHandler(KeyUpHandler k) {
		getTextComponent().addKeyUpHandler(k);
	}
	
	/**
	 * Move focus to textarea without sheduler
	 */
	protected void focusTextImmediate() {
		textAreaComponent.getTextArea().setFocus(true);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (textComponent != null) {
			textComponent.setVisible(visible);
		}
		if (textAreaComponent != null) {
			textAreaComponent.setVisible(visible);
		}
	}
	
	/**
	 * Sets the input field enabled/disabled
	 * @param b true iff input field should be enabled
	 */
	public void setEnabled(boolean b) {
		textComponent.setEditable(b);
	}
	
	/**
	 * @param app
	 *            application
	 * @return new AutoCompleteTextField
	 */
	public static AutoCompleteTextFieldW newTextComponent(App app) {
		return new InputPanelW(app, -1, false).getTextComponent();
	}
}
