package org.geogebra.web.html5.awt;

import org.gwtproject.canvas.client.Canvas;

import elemental2.dom.CSSProperties;
import elemental2.dom.CSSStyleDeclaration;

public class LayeredGGraphicsW extends GGraphics2DW {

	private int currentLayer = 0;
	private final CSSStyleDeclaration parentStyle;

	/**
	 * @param canvas Primary canvas
	 */
	public LayeredGGraphicsW(Canvas canvas) {
		super(canvas);
		CSSStyleDeclaration style = canvas.getCanvasElement().style;
		style.position = "relative";
		parentStyle = canvas.getParent().getElement().style;
	}

	/**
	 * @return z-index for embedded item
	 */
	@Override
	public int embed() {
		parentStyle.zIndex = CSSProperties.ZIndexUnionType.of(currentLayer + 1);
		return currentLayer++;
	}

	@Override
	public void resetLayer() {
		currentLayer = 0;
	}
}
