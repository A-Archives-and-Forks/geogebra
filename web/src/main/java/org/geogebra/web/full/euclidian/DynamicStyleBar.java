package org.geogebra.web.full.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoFunction;
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
	private final static int CONTEXT_MENU_WIDTH = 36;

	/**
	 * @param ev
	 *            parent view
	 */
	public DynamicStyleBar(EuclidianView ev) {
		this.ev = ev;
		this.stylebarPositioner = new StylebarPositioner(ev.getApplication());

		addStyleName("matDynStyleBar");
		add(new IconButton(79, getApp()));

		ev.getApplication().getSelectionManager()
				.addSelectionListener((geo, addToSelection) -> {
					if (addToSelection) {
						return;
					}

					updateStyleBar();
				});
		EventUtil.stopPointer(getElement());
		ClickStartHandler.initDefaults(this.asWidget(), false, true);
	}

	private AppW getApp() {
		return (AppW) ev.getApplication();
	}

	private GPoint calculatePosition(GRectangle2D gRectangle2D, boolean isPoint,
			boolean isFunction) {
		int height = this.getOffsetHeight();
		double left, top = -1;
		boolean functionOrLine = isFunction || gRectangle2D == null;
		if (functionOrLine) {
			GPoint mouseLoc = ev.getEuclidianController().getMouseLoc();
			if (mouseLoc == null) {
				return null;
			}
			top = mouseLoc.y + 10;
		} else if (!isPoint) {
			top = gRectangle2D.getMinY() - height - 10;
		}

		// if there is no enough place on the top of bounding box, dynamic
		// stylebar will be visible at the bottom of bounding box,
		// stylebar of points will be bottom of point if possible.
		if (top < 0 && gRectangle2D != null) {
			top = gRectangle2D.getMaxY() + 10;
		}

		int maxtop = getApp().getActiveEuclidianView().getHeight() - height - 5;
		if (top > maxtop) {
			if (isPoint) {
				// if there is no enough place under the point
				// put the dyn. stylebar above the point
				top = gRectangle2D.getMinY() - height - 10;
			} else {
				top = maxtop;
			}
		}

		// get left position
		if (functionOrLine) {
			left = ev.getEuclidianController().getMouseLoc().x + 10;
		} else {
			left = gRectangle2D.getMaxX() - getOffsetWidth() + CONTEXT_MENU_WIDTH;

			// do not hide rotation handler
			left = Math.max(left,
					gRectangle2D.getMinX() + gRectangle2D.getWidth() / 2 + 16);
		}

		if (left < 0) {
			left = 0;
		}
		int maxLeft = getApp().getActiveEuclidianView().getWidth()
				- this.getOffsetWidth();
		if (left > maxLeft) {
			left = maxLeft;
		}

		return new GPoint((int) left, (int) top);
	}

	@Override
	public void setMode(int mode) {

	}

	@Override
	public void setLabels() {

	}

	@Override
	public void restoreDefaultGeo() {

	}

	public void updateStyleBar() {
		if (!isVisible()) {
			return;
		}

		List<GeoElement> activeGeoList = stylebarPositioner.createActiveGeoList();
		if (activeGeoList == null || activeGeoList.size() == 0) {
			setVisible(false);
			return;
		}

		this.getElement().getStyle().setTop(-10000, Unit.PX);

		if (getApp().getMode() == EuclidianConstants.MODE_SELECT) {
			GRectangle selectionRectangle = getApp().getActiveEuclidianView().getSelectionRectangle();
			if (selectionRectangle != null) {
				setPosition(
						calculatePosition(selectionRectangle, false, false));
				return;
			}
		}

		GPoint newPos = null, nextPos;
		boolean hasVisibleGeo = false;

		for (int i = 0; i < activeGeoList.size(); i++) {
			GeoElement geo = activeGeoList.get(i);
			// it's possible if a non visible geo is in activeGeoList, if we
			// duplicate a geo, which has descendant.
			if (geo.isEuclidianVisible()) {
				hasVisibleGeo = true;
				if (geo instanceof GeoFunction || (geo.isGeoLine()
						&& !geo.isGeoSegment())) {
					if (ev.getHits().contains(geo)) {
						nextPos = calculatePosition(null, false, true);
						oldPos = nextPos;
					} else {
						nextPos = null;
					}
				} else {
					nextPos = fromDrawable(geo);
				}

				if (newPos == null) {
					newPos = nextPos;
				} else if (nextPos != null) {
					newPos.x = Math.max(newPos.x, nextPos.x);
					newPos.y = Math.min(newPos.y, nextPos.y);
				}
			}
		}

		// function selected, but dyn stylebar hit
		// do not calculate the new position of stylebar
		// set the current position instead
		if (hasVisibleGeo && newPos == null && oldPos != null) {
			newPos = oldPos;
		}

		setPosition(newPos);
	}

	@Override
	public void updateButtonPointCapture(int mode) {

	}

	@Override
	public void updateVisualStyle(GeoElement geo) {

	}

	@Override
	public int getPointCaptureSelectedIndex() {
		return 0;
	}

	@Override
	public void updateGUI() {

	}

	@Override
	public void hidePopups() {

	}

	@Override
	public void resetFirstPaint() {

	}

	@Override
	public void reinit() {

	}

	private GPoint fromDrawable(GeoElement geo) {
		DrawableND dr = ev.getDrawableND(geo);
		List<GeoElement> activeGeoList = stylebarPositioner.createActiveGeoList();
		if (dr != null && (!(geo instanceof AbsoluteScreenLocateable
				&& ((AbsoluteScreenLocateable) geo).isFurniture())
				|| geo instanceof GeoEmbed)) {
			return calculatePosition(dr.getBoundsForStylebarPosition(),
					dr instanceof DrawPoint && activeGeoList.size() < 2, false);
		}
		return null;
	}

	/**
	 * Sets the position of dynamic style bar. for newPos
	 */
	private void setPosition(GPoint newPos) {
		if (newPos == null) {
			return;
		}
		this.getElement().getStyle().setLeft(newPos.x, Unit.PX);
		this.getElement().getStyle().setTop(newPos.y, Unit.PX);
	}

	@Override
	public void setVisible(boolean v) {
		// Close label popup if opened when dynamic stylebar visiblity changed
		if (isVisible()) {
			//closeLabelPopup();
		}
		super.setVisible(v);
	}

	protected boolean hasVisibleGeos(ArrayList<GeoElement> geoList) {
		if (ev.checkHitForStylebar()) {
			for (GeoElement geo : geoList) {
				if (ev.isVisibleInThisView(geo) && geo.isEuclidianVisible()
						&& !geo.isAxis()
						&& ev.getHits().contains(geo)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}
}
