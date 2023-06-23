package org.geogebra.web.editor;

import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;

public class EditorKeyboard {

	private TabbedKeyboard tabbedKeyboard;

	void create(HTMLElement element) {
		if (tabbedKeyboard != null) {
			return;
		}

		EditorKeyboardContext editorKeyboardContext = new EditorKeyboardContext(element);
		tabbedKeyboard = new TabbedKeyboard(editorKeyboardContext, false);
		tabbedKeyboard.addStyleName("detached");
		FlowPanel keyboardWrapper = new FlowPanel();
		keyboardWrapper.setStyleName("GeoGebraFrame");
		RootPanel.get().add(keyboardWrapper);
		tabbedKeyboard.clearAndUpdate();
		DomGlobal.window.addEventListener("resize", evt -> tabbedKeyboard.onResize());
		StyleInjector.onStylesLoaded(() -> {
			keyboardWrapper.add(tabbedKeyboard);
			tabbedKeyboard.show();
		});
	}

	public void setProcessing(KeyboardListener listener) {
		tabbedKeyboard.setProcessing(listener);
	}

	public TabbedKeyboard getTabbedKeyboard() {
		return tabbedKeyboard;
	}

	public void setListener(UpdateKeyBoardListener listener) {
		tabbedKeyboard.setListener(listener);
	}
}
