package org.geogebra.web.richtext;

/**
 * Listener for editor events.
 */
public interface EditorChangeListener {

	/**
	 * Called 0.5s after the last change in the editor state
	 * @param content the JSON encoded content of the editor
	 */
	void onContentChanged(String content);

	/**
	 * Called instantly on editor state change
	 */
	void onInput();

	/**
	 * Called on selection change
	 */
	void onSelectionChanged();

	/**
	 * Called on pressing the Escape key within the editor
	 */
	void onEscape();
}
