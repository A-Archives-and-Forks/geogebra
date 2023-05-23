package com.himamis.retex.editor.web;

import com.himamis.retex.renderer.web.JlmApi;

import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsType;

@JsType
public class JlmEditorApi extends JlmApi {

	@SuppressWarnings("unusable-by-js")
	public JlmEditorApi(JlmEditorLib library) {
		super(library);
	}

	public void edit(HTMLElement element) {
		((JlmEditorLib) this.library).edit(element);
	}

}
