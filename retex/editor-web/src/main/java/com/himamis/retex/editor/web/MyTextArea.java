package com.himamis.retex.editor.web;

import org.gwtproject.user.client.ui.FocusWidget;
import org.gwtproject.user.client.ui.RootPanel;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;

public class MyTextArea extends FocusWidget {

	/**
	 * @param element
	 *            wrapped element
	 */
	public MyTextArea(HTMLElement element) {
		super(element);
	}

	/**
	 * Factory method
	 * 
	 * @param element
	 *            textarea element
	 * @return textarea widget
	 */
	public static MyTextArea wrap(HTMLElement element) {
		// Assert that the element is attached.
		assert DomGlobal.document.body.contains(element);

		MyTextArea textArea = new MyTextArea(element);

		// Mark it attached and remember it for cleanup.
		textArea.onAttach();
		RootPanel.detachOnWindowClose(textArea);

		return textArea;
	}

	/**
	 * @param handler
	 *            composition event handler
	 */
	public void addCompositionUpdateHandler(EditorCompositionHandler handler) {
		addDomHandler(handler, CompositionUpdateEvent.getType());
	}

	public void addCompositionEndHandler(EditorCompositionHandler handler) {
		addDomHandler(handler, CompositionEndEvent.getType());
	}
}
