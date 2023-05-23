package org.geogebra.web.html5.gui.textbox;

import org.geogebra.common.util.TextObject;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.Event.NativePreviewEvent;
import org.gwtproject.user.client.Event.NativePreviewHandler;
import org.gwtproject.user.client.ui.TextBox;

import elemental2.dom.HTMLElement;
import elemental2.dom.KeyboardEvent;

/**
 * This class is created so that the bluetooth keyboard works in Safari iOS.
 * 
 * @author Balazs
 */
public class GTextBox extends TextBox
		implements NativePreviewHandler, MathKeyboardListener, TextObject {
	// On iOS when using a bluetooth keyboard, the onkeyup event reports
	// the charcode to be 0. To solve this, we save the character code
	// in the onkeydown event, and we use that for the onkeyup

	protected int keyCode;
	protected boolean isControlKeyDown;
	protected boolean isAltKeyDown;
	protected boolean isShiftKeyDown;
	protected boolean isMetaKeyDown;
	private  boolean isFocused = false;

	public GTextBox(HTMLElement e) {
		super(e);
	}

	public GTextBox() {
		this(false, null);
	}

	/**
	 * @param autocomplete
	 *            allow browser autocomplete ?
	 */
	public GTextBox(boolean autocomplete, GlobalHandlerRegistry globalHandlers) {
		HandlerRegistration handler = Event.addNativePreviewHandler(this);
		if (globalHandlers != null) {
			globalHandlers.add(handler);
		}

		if (!autocomplete) {
			// suggestion from here to disable autocomplete
			// https://code.google.com/p/google-web-toolkit/issues/detail?id=6065
			//
			// #3878
			getElement().setAttribute("autocomplete", "off");
			getElement().setAttribute("autocapitalize", "off");
		}
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		final KeyUpHandler finalHandler = handler;
		return super.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == 0) {
					elemental2.dom.Event nativeEvent = EventUtil.createKeyEvent("keyup",
					        isControlKeyDown, isAltKeyDown, isShiftKeyDown,
					        isMetaKeyDown, keyCode);
					event.setNativeEvent(nativeEvent);

				}
				finalHandler.onKeyUp(event);
			}
		});
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (event.getTypeInt() == Event.ONKEYDOWN) {
			KeyboardEvent nativeEvent = (KeyboardEvent) event.getNativeEvent();
			keyCode = DOM.getKeyCode(nativeEvent);
			isAltKeyDown = nativeEvent.altKey;
			isShiftKeyDown = nativeEvent.shiftKey;
			isControlKeyDown = nativeEvent.ctrlKey;
			isMetaKeyDown = nativeEvent.metaKey;
			if (GlobalKeyDispatcherW.isLeftAltDown()) {
				nativeEvent.preventDefault();
			}
		}
	}

	@Override
	public void ensureEditing() {
		this.setFocus(true);

	}

	@Override
	public void setFocus(boolean b) {
		super.setFocus(b);
		isFocused = b;
	}

	@Override
	public boolean needsAutofocus() {
		return false;
	}

	@Override
	public boolean hasFocus() {
		return isFocused;
	}

	@Override
	public void setEditable(boolean editable) {
		this.setReadOnly(!editable);
	}

	@Override
	public boolean acceptsCommandInserts() {
		return false;
	}
}
