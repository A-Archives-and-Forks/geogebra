package org.geogebra.web.editor;

import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.html5.bridge.RenderGgbElement.RenderGgbElementFunction;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;

import com.himamis.retex.editor.web.MathFieldW;

import elemental2.dom.DomGlobal;

public final class RenderEditor implements RenderGgbElementFunction {
	private final EditorKeyboard editorKeyboard;
	private EditorApi editorApi;

	public RenderEditor(EditorKeyboard editorKeyboard) {
		this.editorKeyboard = editorKeyboard;
	}

	@Override
	public void render(Element element, JsConsumer<Object> callback) {
		editorKeyboard.create(element);
		EditorListener listener = new EditorListener();
		MathFieldW mathField = initMathField(element, listener);
		DomGlobal.window.addEventListener("resize", evt -> onResize(mathField));
		editorApi = new EditorApi(mathField, editorKeyboard.getTabbedKeyboard(), listener);
		editorKeyboard.setListener(this::keyBoardNeeded);
		if (callback != null) {
			callback.accept(editorApi);
		}
	}

	private boolean keyBoardNeeded(boolean show, MathKeyboardListener textField) {
		if (!show) {
			editorApi.closeKeyboard();
		} else {
			editorApi.openKeyboard();
		}
		return false;
	}

	private void onResize(MathFieldW mathField) {
		mathField.setPixelRatio(DomGlobal.window.devicePixelRatio);
	}

	private MathFieldW initMathField(Element el, EditorListener listener) {
		Canvas canvas = Canvas.createIfSupported();
		FlowPanel wrapper = new FlowPanel();
		wrapper.setWidth("100%");
		wrapper.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		MathFieldW mathField = new MathFieldW(null, wrapper, canvas, listener);
		EditorParams editorParams = new EditorParams(el, mathField);
		listener.setMathField(mathField);
		mathField.parse("");
		wrapper.add(mathField);

		if (!editorParams.isPreventFocus()) {
			mathField.requestViewFocus();
		}

		mathField.setPixelRatio(DomGlobal.window.devicePixelRatio);
		mathField.getInternal().setSyntaxAdapter(new EditorSyntaxAdapter());
		RootPanel editorPanel = newRoot(el);

		editorPanel.add(wrapper);
		String cssColor = mathField.getBackgroundColor().getCssColor();
		setBackgroundColor(canvas.getElement(), cssColor);

		MathFieldProcessing processing = new MathFieldProcessing(mathField);
		editorPanel.addDomHandler(evt -> onFocus(mathField, processing), ClickEvent.getType());

		canvas.getElement().setTabIndex(-1);
		return mathField;
	}

	private void setBackgroundColor(Element element, String cssColor) {
		element.getStyle().setBackgroundColor(cssColor);
	}

	private void onFocus(MathFieldW mathField, MathFieldProcessing processing) {
		mathField.requestViewFocus();
		editorKeyboard.setProcessing(processing);
	}

	private RootPanel newRoot(Element el) {
		Element detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.setClassName("GeoGebraFrame editor");
		String uid = DOM.createUniqueId();
		detachedKeyboardParent.setId(uid);
		el.appendChild(detachedKeyboardParent);
		return RootPanel.get(uid);
	}
}
