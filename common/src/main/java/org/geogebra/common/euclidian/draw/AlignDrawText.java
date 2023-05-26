package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.kernel.geos.GeoText;

public class AlignDrawText {

	private final GeoText text;
	private final DrawText drawText;

	private int oldHorizontal;
	private int oldVertical;
	private final static int MARGIN = 6;
	private GGraphics2D g2;
	AlignDrawText(GeoText text, DrawText drawText, GGraphics2D g2) {
		this.g2 = g2;
		this.text = text;
		this.drawText = drawText;
	}

	void apply(double width, double height) {
		drawText.xLabel = (int) getXLabelAligned(width);
		drawText.yLabel = (int) (text.isLaTeX()
				? getYLabelAlignedForLatex(height)
				: getYLabelAlignedForPlain(height));
	}

	private double getXLabelAligned(double width) {
		switch (getHorizontalAlignment()) {
		case -1:
			return getXLabelForLeft(width);
		case 0:
			return getXLabelForCenter(width) ;
		case 1:
		default:
			return getXLabelForRight();

		}
	}

	private int getXLabelForRight() {
		return drawText.xLabel + MARGIN;
	}

	private double getXLabelForCenter(double width) {
		return drawText.xLabel + MARGIN / 2.0 - Math.floor(width / 2);
	}

	private double getXLabelForLeft(double width) {
		return drawText.xLabel - width + 1;
	}

	private double getYLabelAlignedForLatex(double height) {
		switch (getVerticalAlignment()) {
		case -1:
			return drawText.yLabel + MARGIN;
		case 0:
			return drawText.yLabel - height / 2 + MARGIN + 1;
		case 1:
		default:
			return drawText.yLabel - height;
		}
	}

	private double getYLabelAlignedForPlain(double height) {
		GTextLayout layout = getTextLayout();
		double maxHeightInText = layout.getAscent() + layout.getDescent();
		double heightDifference = height - maxHeightInText;
		switch (getVerticalAlignment()) {
		case -1:
			return drawText.yLabel + maxHeightInText + MARGIN;
		case 0:
			return drawText.yLabel + (heightDifference / 2);
		case 1:
		default:
			return drawText.yLabel - heightDifference;
		}
	}

	private GTextLayout getTextLayout() {
		return CanvasDrawable.getLayout(g2, text.getTextString(), drawText.getTextFont());
	}

	boolean hasChanged() {
		return oldVertical != getVerticalAlignment()
				|| oldHorizontal != getVerticalAlignment();
	}

	private int getVerticalAlignment() {
		return text.getVerticalAlignment() != null
				? (int) text.getVerticalAlignment().getValue()
				: 1;
	}

	private int getHorizontalAlignment() {
		return text.getHorizontalAlignment() != null
				? (int) text.getHorizontalAlignment().getValue()
				: 1;
	}

	void update() {
		oldHorizontal = getHorizontalAlignment();
		oldVertical = getVerticalAlignment();
	}
}
