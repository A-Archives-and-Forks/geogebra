package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.ViewportAdjusterDelegate;
import org.geogebra.common.spreadsheet.kernel.GeoElementCellRendererFactory;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;
import org.geogebra.gwtutil.NativePointerEvent;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.ScrollPanel;

import com.google.gwt.core.client.Scheduler;
import com.himamis.retex.editor.share.util.KeyCodes;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import jsinterop.base.Js;

public class SpreadsheetPanel extends FlowPanel implements RequiresResize {

	public static final int AUTOSCROLL_OFFSET = 30;
	private final Spreadsheet spreadsheet;
	private final GGraphics2DW graphics;
	private final AppW app;

	// The canvas itself cannot be wrapped in a scrollpanel,
	// otherwise there is jumping between scroll event and repaint
	// on high-res screens
	private final ScrollPanel scrollOverlay;
	private final MathTextFieldW mathField;
	private final elemental2.dom.Element spreadsheetElement;
	double moveTimeout;

	/**
	 * @param app application
	 */
	public SpreadsheetPanel(AppW app) {
		Canvas spreadsheetWidget = Canvas.createIfSupported();
		spreadsheetWidget.addStyleName("spreadsheetWidget");
		graphics = new GGraphics2DW(spreadsheetWidget);
		this.app = app;
		addStyleName("spreadsheetPanel");
		KernelTabularDataAdapter tabularData = new KernelTabularDataAdapter(
				app.getSettings().getSpreadsheet(), app.getKernel());
		app.getKernel().notifyAddAll(tabularData);
		spreadsheet = new Spreadsheet(tabularData, new GeoElementCellRendererFactory(
				new AwtReTexGraphicsBridgeW()), app.getUndoManager());

		app.getKernel().attach(tabularData);
		add(spreadsheetWidget);
		scrollOverlay = new ScrollPanel();
		mathField = new MathTextFieldW(app);

		spreadsheet.setControlsDelegate(initDelegate());

		FlowPanel scrollContent = new FlowPanel();
		scrollOverlay.setWidget(scrollContent);
		scrollOverlay.setStyleName("spreadsheetScrollOverlay");
		add(scrollOverlay);
		spreadsheetElement = Js.uncheckedCast(scrollContent.getElement());

		ViewportAdjusterDelegate viewportAdjusterDelegate = createScrollable();
		spreadsheet.setViewportAdjustmentHandler(viewportAdjusterDelegate);

		GlobalHandlerRegistry registry = app.getGlobalHandlers();

		registry.addEventListener(spreadsheetElement, "pointerdown", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			spreadsheet.handlePointerDown(getEventX(ptr), getEventY(ptr),
					getModifiers(ptr));
			setPointerCapture(event);
			if (ptr.getButton() == 2 || (NavigatorUtil.isMacOS() && ptr.getCtrlKey())) {
				event.preventDefault();
			}
		});
		registry.addEventListener(spreadsheetElement, "pointerup", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			spreadsheet.handlePointerUp(getEventX(ptr), getEventY(ptr),
					getModifiers(ptr));
			if (!spreadsheet.isEditorActive()) {
				app.hideKeyboard();
			}
		});
		registry.addEventListener(spreadsheetElement, "pointermove", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			int offsetX = getEventX(ptr);
			int offsetY = getEventY(ptr);
			Modifiers modifiers = getModifiers(ptr);
			DomGlobal.clearTimeout(moveTimeout);
			handlePointerMoved(offsetX, offsetY, modifiers, false);
		});
		registry.addEventListener(DomGlobal.window, "pointerup", event -> {
			elemental2.dom.Element target = Js.uncheckedCast(event.target);
			if (target.closest(".spreadsheetScrollOverlay,.gwt-PopupPanel") != null) {
				return;
			}
			spreadsheet.clearSelectionOnly();
		});

		ClickStartHandler.initDefaults(scrollContent, false, true);
		scrollContent.getElement().setTabIndex(0);
		scrollContent.addDomHandler(evt -> {
			spreadsheet.handleKeyPressed(KeyCodes.translateGWTcode(
					evt.getNativeKeyCode()).getJavaKeyCode(),
					getKey(evt.getNativeEvent()),
					getKeyboardModifiers(evt));
			evt.stopPropagation(); // do not let global event handler interfere
			evt.preventDefault(); // do not scroll the view
		}, KeyDownEvent.getType());
		updateTotalSize();
		DomGlobal.setInterval((ignore) -> {
			repaint();
		}, 200);
		DomGlobal.setInterval((ignore) -> {
			spreadsheet.scrollForPasteSelectionIfNeeded();
		}, 20);
		scrollOverlay.addScrollHandler(event -> {
			updateViewport();
			repaint();
		});
	}

	private void handlePointerMoved(int offsetX, int offsetY,
			Modifiers modifiers, boolean immediate) {
		DomGlobal.clearTimeout(moveTimeout);
		setCursor(spreadsheet.getCursor(offsetX, offsetY));
		Rectangle oldViewport = spreadsheet.getViewport();
		if (eventIsInside(offsetX, offsetY)) {
			spreadsheet.handlePointerMove(offsetX, offsetY,
					modifiers);
			return;
		} else if (immediate) {
			spreadsheet.handlePointerMove(offsetX, offsetY,
					modifiers);
		}

		if (!immediate || !oldViewport.equals(spreadsheet.getViewport())) {
			moveTimeout = DomGlobal.setTimeout((ignore) ->
					handlePointerMoved(offsetX, offsetY, modifiers, true), 300);
		}
	}

	private boolean eventIsInside(int offsetX, int offsetY) {
		return offsetX < scrollOverlay.getOffsetWidth() - AUTOSCROLL_OFFSET
				&& offsetY < scrollOverlay.getOffsetHeight() - AUTOSCROLL_OFFSET;
	}

	private void setPointerCapture(Event event) {
		Function capture = Js.uncheckedCast(Js.asPropertyMap(event.target)
				.get("setPointerCapture"));
		if (Js.isTruthy(capture)) {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			capture.call(event.target, ptr.getPointerId());
		}
	}

	private String getKey(NativeEvent nativeEvent) {
		String key = Js.asPropertyMap(nativeEvent).getAsAny("key").asString();
		return key.length() > 1  ? "" : key;
	}

	private SpreadsheetControlsDelegateW initDelegate() {
		return new SpreadsheetControlsDelegateW(app, this, mathField);
	}

	public void requestFocus() {
		Scheduler.get().scheduleDeferred(() -> spreadsheetElement.focus());
	}

	private Modifiers getKeyboardModifiers(KeyEvent<?> evt) {
		return new Modifiers(evt.isAltKeyDown(),
				NavigatorUtil.isMacOS() ? evt.isMetaKeyDown() : evt.isControlKeyDown(),
				evt.isShiftKeyDown(), false);
	}

	private int getEventX(NativePointerEvent ptr) {
		return (int) ptr.getOffsetX() - scrollOverlay.getElement()
				.getScrollLeft();
	}

	private int getEventY(NativePointerEvent ptr) {
		return (int) ptr.getOffsetY() - scrollOverlay.getElement()
				.getScrollTop();
	}

	private void setCursor(MouseCursor cursor) {
		setStyleName("cursor_resizeEW", cursor == MouseCursor.RESIZE_X);
		setStyleName("cursor_resizeNS", cursor == MouseCursor.RESIZE_Y);
		setStyleName("cursor_default", cursor == MouseCursor.DRAG_DOT);
	}

	private Modifiers getModifiers(NativePointerEvent ptr) {
		return new Modifiers(ptr.getAltKey(),
				NavigatorUtil.isMacOS() ? ptr.getMetaKey() : ptr.getCtrlKey(),
				ptr.getShiftKey(),
				ptr.getButton() == 2 || (NavigatorUtil.isMacOS() && ptr.getCtrlKey()));
	}

	private void updateTotalSize() {
		double width = spreadsheet.getTotalWidth();
		double height = spreadsheet.getTotalHeight();
		updateTotalSize(width, height);
	}

	private void updateTotalSize(double width, double height) {
		Style style = scrollOverlay.getWidget().getElement().getStyle();
		style.setWidth(width, Unit.PX);
		style.setHeight(height, Unit.PX);
		style.setProperty("maxHeight", height + "px");
		style.setProperty("maxWidth", width + "px");
	}

	@Override
	public void onResize() {
		graphics.setDevicePixelRatio(app.getPixelRatio());
		graphics.setCoordinateSpaceSize(getWidth(), getHeight());
		updateViewport();
		spreadsheet.scrollEditorIntoView();
		repaint();
	}

	private void repaint() {
		graphics.restoreTransform();
		spreadsheet.draw(graphics);
	}

	private void updateViewport() {
		int scrollTop = scrollOverlay.getElement().getScrollTop();
		int scrollLeft = scrollOverlay.getElement().getScrollLeft();
		spreadsheet.setViewport(new Rectangle(scrollLeft, scrollLeft + getWidth(),
				scrollTop, scrollTop + getHeight()));
	}

	private int getHeight() {
		return scrollOverlay.getOffsetHeight();
	}

	private int getWidth() {
		return scrollOverlay.getOffsetWidth();
	}

	/**
	 * @return The width of the scrollbar used for dragging content with the left mouse button
	 */
	private int getScrollBarWidth() {
		return getWidth() - scrollOverlay.getElement().getClientWidth();
	}

	public MathKeyboardListener getKeyboardListener() {
		return mathField.getKeyboardListener();
	}

	private ViewportAdjusterDelegate createScrollable() {
		return new ViewportAdjusterDelegate() {

			@Override
			public void setScrollPosition(int x, int y) {
				scrollOverlay.setHorizontalScrollPosition(x);
				scrollOverlay.setVerticalScrollPosition(y);
			}

			@Override
			public int getScrollBarWidth() {
				return SpreadsheetPanel.this.getScrollBarWidth();
			}

			@Override
			public void updateScrollableContentSize(Size size) {
				updateTotalSize(size.getWidth(), size.getHeight());
			}
		};
	}

	public void saveContentAndHideCellEditor() {
		spreadsheet.saveContentAndHideCellEditor();
	}

}
