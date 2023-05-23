package org.geogebra.web.full.gui.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.web.MathFieldW;

import elemental2.dom.CSSProperties;
import elemental2.dom.CSSStyleDeclaration;

public abstract class MathFieldEditorDecoratorBase {
	protected static final int PADDING_LEFT = 2;
	private final MathFieldW mathField;
	private final CSSStyleDeclaration style;

	/**
	 *
	 * @param editor the mathfield editor to be decorated.
	 */
	public MathFieldEditorDecoratorBase(MathFieldEditor editor) {
		this.mathField = editor.getMathField();
		this.style = editor.getStyle();
	}

	/**
	 * Sets background color for the editor
	 *
	 * @param backgroundColor {@link GColor}
	 */
	protected void setBackgroundColor(GColor backgroundColor) {
		GColor color = backgroundColor != null
				? backgroundColor
				: GColor.WHITE;
		String cssColor = StringUtil.toHtmlColor(color);
		style.backgroundColor = cssColor;
	}

	/**
	 * Sets foreground color for the editor
	 *
	 * @param foregroundColor {@link GColor}
	 */
	protected void setForegroundColor(GColor foregroundColor) {
		mathField
				.setForegroundColor(StringUtil.toHtmlColor(foregroundColor));
	}

	/**
	 * Sets the font size of the editor.
	 *
	 * @param fontSize to set.
	 */
	public void setFontSize(double fontSize) {
		mathField.setFontSize(fontSize);
	}

	/**
	 * Sets left position of the editor.
	 *
	 * @param value to set.
	 */
	protected void setLeft(double value) {
		style.left = value + "px";
	}

	/**
	 * Sets top position of the editor.
	 *
	 * @param value to set.
	 */
	public void setTop(double value) {
		style.top = value + "px";
	}

	/**
	 * Sets width of the editor.
	 *
	 * @param value to set.
	 */
	protected void setWidth(double value) {
		style.width = CSSProperties.WidthUnionType.of(value + "px");
	}

	/**
	 * Sets height position of the editor.
	 *
	 * @param value to set.
	 */
	protected void setHeight(double value) {
		style.height = CSSProperties.HeightUnionType.of(value + "px");
	}

}
