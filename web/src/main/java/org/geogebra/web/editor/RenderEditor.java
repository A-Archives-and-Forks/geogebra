package org.geogebra.web.editor;

import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.html5.bridge.RenderGgbElement.RenderGgbElementFunction;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;

import com.himamis.retex.editor.web.MathFieldW;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;

public final class RenderEditor implements RenderGgbElementFunction {
	private final EditorKeyboard editorKeyboard;
	private EditorApi editorApi;

	public RenderEditor(EditorKeyboard editorKeyboard) {
		this.editorKeyboard = editorKeyboard;
	}

	@Override
	public void render(HTMLElement element, JsConsumer<Object> callback) {
		editorKeyboard.create(element);
		EditorListener listener = new EditorListener();
		MathFieldW mathField = initMathField(element, listener);
		DomGlobal.window.addEventListener("resize", evt -> onResize(mathField));
		editorApi = new EditorApi(mathField, editorKeyboard.getTabbedKeyboard(), listener);
		editorKeyboard.setListener(() -> editorApi.closeKeyboard());
		if (callback != null) {
			callback.accept(editorApi);
		}
	}

	private void onResize(MathFieldW mathField) {
		mathField.setPixelRatio(DomGlobal.window.devicePixelRatio);
	}

	private MathFieldW initMathField(HTMLElement el, EditorListener listener) {
		Canvas canvas = Canvas.createIfSupported();
		FlowPanel wrapper = new FlowPanel();
		wrapper.setWidth("100%");
		wrapper.getElement().style.overflow = "hidden";
		MathFieldW mathField = new MathFieldW(null, wrapper, canvas, listener);
		final EditorParams editorParams = new EditorParams(el, mathField);
		listener.setMathField(mathField);
		mathField.parse("");
		wrapper.add(mathField);

		mathField.setPixelRatio(DomGlobal.window.devicePixelRatio);
		mathField.getInternal().setSyntaxAdapter(new EditorSyntaxAdapter());
		mathField.setAriaLabel("Enter your equation or expression here");
		RootPanel editorPanel = newRoot(el);

		editorPanel.add(wrapper);
		String cssColor = mathField.getBackgroundColor().getCssColor();
		setBackgroundColor(canvas.getElement(), cssColor);

		MathFieldProcessing processing = new MathFieldProcessing(mathField);
		editorPanel.addDomHandler(evt -> onFocus(mathField, processing), ClickEvent.getType());

		if (!editorParams.isPreventFocus()) {
			onFocus(mathField, processing);
		}

		canvas.getElement().tabIndex = -1;;
		return mathField;
	}

	private void setBackgroundColor(HTMLElement element, String cssColor) {
		element.style.backgroundColor = cssColor;
	}

	private void onFocus(MathFieldW mathField, MathFieldProcessing processing) {
		mathField.requestViewFocus();
		editorKeyboard.setProcessing(processing);
	}

	private RootPanel newRoot(HTMLElement el) {
		HTMLElement detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.className = "GeoGebraFrame editor";
		String uid = DOM.createUniqueId();
		detachedKeyboardParent.id = uid;
		el.appendChild(detachedKeyboardParent);
		return RootPanel.get(uid);
	}
}
