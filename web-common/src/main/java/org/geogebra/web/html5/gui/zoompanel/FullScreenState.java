package org.geogebra.web.html5.gui.zoompanel;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.StyleInjector;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLStyleElement;

public class FullScreenState {
	private HTMLStyleElement transformOverride;
	private final HashMap<String, String> containerProps = new HashMap<>();
	protected boolean fullScreenActive = false;
	protected boolean emulated;
	private GDimension oldSize;
	private double cssScale = 0;

	/**
	 * @return css scale
	 */
	public double getCssScale() {
		return cssScale;
	}

	/**
	 * Resetting position and margins.
	 *
	 * @param container
	 *            to reset.
	 */
	protected void resetStyleAfterFullscreen(HTMLElement container, AppW app) {
		if (container != null) {
			for (Map.Entry<String, String> e : containerProps.entrySet()) {
				if (!StringUtil.empty(e.getValue())) {
					container.style.setProperty(e.getKey(), e.getValue());
				} else {
					container.style.setProperty(e.getKey(), null);
				}
			}
		}
		if (oldSize != null && app.isUnbundled()) {
			app.getGgbApi().setSize(oldSize.getWidth(), oldSize.getHeight());
		}
	}

	/**
	 * @param container
	 *            container
	 * @param propName
	 *            property name
	 * @param value
	 *            value of property
	 */
	public void setContainerProp(HTMLElement container, String propName,
			String value) {
		containerProps.put(propName,
				container.style.getPropertyValue(propName));
		container.style.setProperty(propName, value);
	}

	protected void store(HTMLElement container, AppW app, double scale) {
		String containerPositionBefore = container.style
				.position;
		if (StringUtil.empty(containerPositionBefore)) {
			containerPositionBefore = "static";
		}
		containerProps.clear();
		containerProps.put("position", containerPositionBefore);
		setContainerProp(container, "width", "100%");
		setContainerProp(container, "height", "100%");
		setContainerProp(container, "maxWidth", "100%");
		setContainerProp(container, "maxHeight", "100%");
		setContainerProp(container, "marginLeft", "0");
		setContainerProp(container, "marginTop", "0");
		oldSize = app.getPreferredSize();

		cssScale = scale;
		if (emulated) {
			overrideParentTransform();
			setContainerProp(container, "left", "0px");
			container.classList.add("GeoGebraFullscreenContainer");
		}
	}

	/**
	 * Remove the inline style for transform overriding
	 */
	protected void removeTransformOverride() {
		if (transformOverride != null) {
			transformOverride.remove();
		}
	}

	private void overrideParentTransform() {
		transformOverride = StyleInjector.injectStyleSheet(
				"*:not(.ggbTransform){transform: none !important;}");
	}
}
