package org.geogebra.web.full.cas.view;

import org.gwtproject.event.dom.client.ScrollEvent;
import org.gwtproject.event.dom.client.ScrollHandler;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.Event.NativePreviewEvent;
import org.gwtproject.user.client.Event.NativePreviewHandler;
import org.gwtproject.user.client.ui.ScrollPanel;

import elemental2.dom.EventTarget;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

/**
 * Widget representing the CAS View
 *
 */
public class CASComponentW extends ScrollPanel implements ScrollHandler,
        NativePreviewHandler {

	private boolean scrollHappened;

	/**
	 * New CAS component
	 */
	public CASComponentW() {
		this.getElement().className = "casView";
		addScrollHandler(this);
		Event.addNativePreviewHandler(this);
	}

	@Override
	public void onScroll(ScrollEvent event) {
		scrollHappened = true;
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		EventTarget target = event.getNativeEvent().target;
		if (!DOM.isElement(target)) {
			return;
		}
		HTMLElement element = Js.uncheckedCast(target);
		if (this.getElement().contains(element)
				&& event.getTypeInt() == Event.ONTOUCHEND && scrollHappened) {
					event.cancel();
					scrollHappened = false;
		}
	}

}
