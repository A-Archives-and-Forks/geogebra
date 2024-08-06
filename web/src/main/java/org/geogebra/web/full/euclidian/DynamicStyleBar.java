package org.geogebra.web.full.euclidian;

import java.util.function.BiConsumer;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Dynamic stylebar containing IconButtons
 */
public class DynamicStyleBar extends FlowPanel implements EuclidianStyleBar {
	private EuclidianView ev;
	private StylebarPositioner stylebarPositioner;
	private GPoint oldPos = null;
	private BiConsumer<Integer, Integer> positionSetter = (posX, posY) -> {
		getElement().getStyle().setLeft(posX, Unit.PX);
		getElement().getStyle().setTop(posY, Unit.PX);
	};

	/**
	 * @param ev
	 *            parent view
	 */
	public DynamicStyleBar(EuclidianView ev) {
		this.ev = ev;
		this.stylebarPositioner = new StylebarPositioner(ev.getApplication());

		addStyleName("matDynStyleBar");
		add(new IconButton(79, (AppW) ev.getApplication()));

		addHandlers();
		buildGUI();
	}

	private void buildGUI() {

	}

	private void addHandlers() {
		ev.getApplication().getSelectionManager()
				.addSelectionListener((geo, addToSelection) -> {
					if (addToSelection) {
						return;
					}
					updateStyleBar();
				});

		EventUtil.stopPointer(getElement());
		ClickStartHandler.initDefaults(asWidget(), false, true);
	}

	@Override
	public void setMode(int mode) {
		// nothing for now
	}

	@Override
	public void setLabels() {
		// nothing for now
	}

	@Override
	public void restoreDefaultGeo() {
		// nothing for now
	}

	@Override
	public void updateStyleBar() {
		if (!isVisible()) {
			return;
		}

		stylebarPositioner.positionDynStylebar(this, getOffsetWidth(), getOffsetHeight(),
				positionSetter);
	}

	@Override
	public void updateButtonPointCapture(int mode) {
		// nothing for now
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// nothing for now
	}

	@Override
	public int getPointCaptureSelectedIndex() {
		return 0;
	}

	@Override
	public void updateGUI() {
		// nothing for now
	}

	@Override
	public void hidePopups() {
		// nothing for now
	}

	@Override
	public void resetFirstPaint() {
		// nothing for now
	}

	@Override
	public void reinit() {
		// nothing for now
	}
}
