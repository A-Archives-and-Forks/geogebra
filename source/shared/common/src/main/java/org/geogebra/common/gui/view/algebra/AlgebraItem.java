package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.CoordinatesFormat;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.IndexLaTeXBuilder;
import org.geogebra.common.util.SymbolicUtil;

/**
 * Utility class for AV items
 */
public class AlgebraItem {
	/**
	 * @param geo
	 *            element
	 * @return whether changing symbolic/numeric for this geo will have any
	 *         effect
	 */
	public static boolean isSymbolicDiffers(GeoElement geo) {
		if (!(geo instanceof HasSymbolicMode)) {
			return false;
		}
		if (geo instanceof GeoSymbolic) {
			GeoSymbolic symbolic = (GeoSymbolic) geo;
			if (symbolic.shouldWrapInNumeric()) {
				return true;
			} else if (SymbolicUtil.isSolve(symbolic)) {
				return SymbolicUtil.isSymbolicSolveDiffers(symbolic);
			}
		}

		if (SymbolicUtil.isOutputOfAlgoSolveOnly(geo)) {
			return !allRHSareIntegers((GeoList) geo);
		}
		if (geo.isGeoNumeric()) {
			ExpressionNode def = geo.getDefinition() == null ? null
					: geo.getDefinition().asFraction();
			return geo.isRecurringDecimal() || def != null && def.unwrap().isExpressionNode();
		}
		HasSymbolicMode sm = (HasSymbolicMode) geo;
		boolean orig = sm.isSymbolicMode();
		String text1 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);
		sm.setSymbolicMode(!orig, false);
		String text2 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);

		sm.setSymbolicMode(orig, false);
		if (text1 == null) {
			return text2 != null;
		}
		return !text1.equals(text2)
				&& !GeoFunction.isUndefined(text1) && !GeoFunction.isUndefined(text2);
	}

	public static boolean checkAllRHSareIntegers(GeoElementND geo) {
		return geo instanceof GeoList && allRHSareIntegers((GeoList) geo);
	}

	private static boolean allRHSareIntegers(GeoList geo) {
		for (int i = 0; i < geo.size(); i++) {
			if (geo.get(i) instanceof GeoLine) {
				if (!DoubleUtil.isInteger(((GeoLine) geo.get(i)).getZ())) {
					return false;
				}
			} else  if (geo.get(i) instanceof GeoPlaneND) {
				if (!DoubleUtil.isInteger(((GeoPlaneND) geo.get(i))
						.getCoordSys().getEquationVector().getW())) {
					return false;
				}
			} else if (geo.get(i) instanceof GeoList) {
				if (!allRHSareIntegers((GeoList) geo.get(i))) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param geo - element
	 * @return whether geo is a fraction that can be rationalized
	 */
	public static boolean isRationalizableFraction(GeoElement geo) {
		return geo instanceof GeoNumeric && geo.getDefinition() != null
				&& geo.getDefinition().isRationalizableFraction();
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element is a numeric that can be written as a sqrt
	 */
	public static boolean isGeoSurd(GeoElement geo) {
		return geo instanceof GeoNumeric && geo.getDefinition() != null
				&& geo.getDefinition().isSimplifiableSurd();
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element is part of packed output (including header)
	 */
	public static boolean needsPacking(GeoElement geo) {
		return geo != null && geo.getPackedIndex() >= 0;
	}

	/**
	 * @param element
	 *            element
	 * @return whether element is part of packed output; exclude header
	 */
	public static boolean isCompactItem(GeoElement element) {
		return element != null && element.getPackedIndex() > 0;
	}

	/**
	 * @param element
	 *            element
	 * @return formula for "Duplicate"
	 */
	public static String getDuplicateFormulaForGeoElement(GeoElement element) {
		String duplicate;
		if ("".equals(element.getDefinition(StringTemplate.defaultTemplate))) {
			duplicate = element.getValueForInputBar();
		} else {
			duplicate = element.getDefinitionNoLabel(StringTemplate.editorTemplate);
		}
		return duplicate;
	}

	/**
	 * @param element
	 *            element
	 * @return output text (LaTex or plain)
	 */
	public static String getOutputTextForGeoElement(GeoElement element) {
		String outputText;
		if (element.isLaTeXDrawableGeo()) {
			outputText = element.getLaTeXDescriptionRHS(true,
					getOutputStringTemplate(element));
		} else {
			if (needsPacking(element)) {
				outputText = element.getAlgebraDescriptionLaTeX();
			} else {
				outputText = element.getAlgebraDescriptionRHSLaTeX();
			}
		}
		return outputText;
	}

	/**
	 * Returns the definition string for the geo element in the input row of the Algebra View.
	 * @param element geo element
	 * @return definition text in LaTeX
	 */
	public static String getDefinitionLatexForGeoElement(GeoElement element) {
		return element.isAlgebraLabelVisible() ? element.getDefinitionForEditor() : element
				.getDefinitionNoLabel(StringTemplate.editorTemplate);
	}

	/**
	 * Returns the preview string for the geo element in the input row of the Algebra View.
	 * @param element geo element
	 * @return input preview string in LaTeX
	 */
	public static String getPreviewLatexForGeoElement(GeoElement element) {
		String latex = getPreviewFormula(element, StringTemplate.numericLatex);

		if (latex != null) {
			return latex;
		}

		//APPS-4553 Logic from RadioTreeItem.getTextForEditing() for consistency
		if (needsPacking(element)) {
			return element.getLaTeXDescriptionRHS(false, StringTemplate.numericLatex);
		} else if (!element.isAlgebraLabelVisible()) {
			if (isTextItem(element)) {
				return element.getLaTeXdescription();
			}
			return element.getDefinition(StringTemplate.numericLatex);
		}

		boolean substituteNumbers = element instanceof GeoNumeric && element.isSimple();
		return element.getLaTeXAlgebraDescriptionWithFallback(
				substituteNumbers
						|| (element instanceof GeoNumeric && element.isSimple()),
				StringTemplate.numericLatex, true);
	}

	/**
	 * @param geo1
	 *            element
	 * @param builder
	 *            index builder
	 * @param stringTemplate
	 *            string template
	 * @return whether we did append something to the index builder
	 */
	public static boolean buildPlainTextItemSimple(GeoElement geo1,
			IndexHTMLBuilder builder, StringTemplate stringTemplate) {
		int avStyle = geo1.getKernel().getAlgebraStyle();
		boolean showLabel =  geo1.getApp().getConfig().hasLabelForDescription();
		if (geo1.isIndependent() && geo1.isGeoPoint()
				&& avStyle == AlgebraStyle.DESCRIPTION) {
			builder.clear();
			builder.indicesToHTML(((GeoPointND) geo1).toStringDescription(stringTemplate));
			return true;
		}
		if (geo1.isIndependent() && geo1.getDefinition() == null) {
			geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
			return true;
		}
		switch (avStyle) {
		case AlgebraStyle.VALUE:
			if (geo1.isAllowedToShowValue()) {
				if (showLabel) {
					geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
				} else {
					geo1.getAlgebraDescriptionTextOrHTMLRHS(builder);
				}
			} else {
				buildDefinitionString(geo1, builder, stringTemplate);
			}
			return true;

		case AlgebraStyle.DESCRIPTION:
			if (needsPacking(geo1)) {
				geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
			} else {
				if (showLabel) {
					geo1.addLabelTextOrHTML(geo1
							.getDefinitionDescription(StringTemplate.defaultTemplate), builder);
				} else {
					builder.clear();
					builder.append(geo1.getDefinitionDescription(stringTemplate));
				}
			}
			return true;

		case AlgebraStyle.DEFINITION:
			buildDefinitionString(geo1, builder, stringTemplate);
			return true;
		default:
		case AlgebraStyle.DEFINITION_AND_VALUE:
			if (needsPacking(geo1)) {
				geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
				return true;
			}
			return false;
		}
	}

	/**
	 * @param geoElement
	 *            construction element
	 * @param stringBuilder
	 *            builder
	 * @param stringTemplate
	 *            template
	 */
	public static void buildDefinitionString(GeoElement geoElement,
			IndexHTMLBuilder stringBuilder, StringTemplate stringTemplate) {
		String desc = geoElement.getDefinition(stringTemplate);
		if (geoElement.isAlgebraLabelVisible()) {
			geoElement.addLabelTextOrHTML(desc, stringBuilder);
		} else {
			IndexHTMLBuilder.convertIndicesToHTML(desc, stringBuilder);
		}

	}

	/**
	 * @param geo1
	 *            element
	 * @param builder
	 *            index builder
	 * @return whether we did append something to the index builder
	 */
	public static boolean buildPlainTextItemSimple(GeoElement geo1,
			IndexHTMLBuilder builder) {
		return buildPlainTextItemSimple(geo1, builder,
				StringTemplate.defaultTemplate);
	}

	/**
	 * @param geoElement
	 *            element
	 * @param style
	 *            AlgebraStyle.*
	 * @param sb
	 *            builder
	 * @param stringTemplateForPlainText
	 *            string template for building simple plain text item
	 */
	private static void buildText(GeoElement geoElement, int style,
			IndexHTMLBuilder sb, StringTemplate stringTemplateForPlainText) {

		if (style == AlgebraStyle.DESCRIPTION
				&& needsPacking(geoElement)) {
			String value = geoElement
					.getDefinitionDescription(StringTemplate.editorTemplate);
			sb.clear();
			sb.append(value);
		} else {
			buildPlainTextItemSimple(geoElement, sb,
					stringTemplateForPlainText);
		}
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element should be represented by simple text item
	 */
	public static boolean isTextItem(GeoElementND geo) {
		return geo instanceof GeoText && !((GeoText) geo).isLaTeX()
				&& !((GeoText) geo).isTextCommand();
	}

	/**
	 * add geo to selection with its special points.
	 * TODO rename to selectGeo(WithSpecialPoints?)
	 * @param geo
	 *            The geo element to add.
	 * @param app
	 *            application
	 */
	public static void addSelectedGeoWithSpecialPoints(GeoElementND geo,
			App app) {
		if (!app.getConfig().hasPreviewPoints()) {
			return;
		}
		app.getSelectionManager().clearSelectedGeos(false, false);
		app.getSelectionManager().addSelectedGeo(geo, false, false);
	}

	/**
	 * @param geoElement
	 *            about we should decide if the outputrow should be shown or not
	 * @param style
	 *            current algebrastyle
	 * @return whether the output should be shown or not
	 */
	public static DescriptionMode getDescriptionModeForGeo(GeoElement geoElement, int style) {
		switch (style) {
			case AlgebraStyle.DEFINITION_AND_VALUE:
				return geoElement.getDescriptionMode();

			case AlgebraStyle.DESCRIPTION:
				if (geoElement.getPackedIndex() == 0) {
					return DescriptionMode.DEFINITION_VALUE;
				}
				if (geoElement.getPackedIndex() > 0) {
					return DescriptionMode.VALUE;
				}
				return geoElement instanceof GeoNumeric
						&& (!geoElement.isIndependent() || (geoElement
						.getDescriptionMode() == DescriptionMode.DEFINITION_VALUE
						&& geoElement.getParentAlgorithm() == null))
						|| geoElement.evaluatesToNumber(false)
						? DescriptionMode.DEFINITION_VALUE
						: DescriptionMode.DEFINITION;
			case AlgebraStyle.DEFINITION:
				return DescriptionMode.DEFINITION;
			case AlgebraStyle.VALUE:
			default:
				return DescriptionMode.VALUE;
		}
	}

	private static boolean shouldShowOutputRow(GeoElement geoElement, int algebraStyle) {
		switch (algebraStyle) {
		case AlgebraStyle.DESCRIPTION:
			return getDescriptionModeForGeo(geoElement, algebraStyle) != DescriptionMode.DEFINITION;
		case AlgebraStyle.VALUE:
		case AlgebraStyle.DEFINITION_AND_VALUE:
			return geoElement.isAllowedToShowValue();
		default:
			return false;
		}
	}

	/**
	 * Tells whether AV should show two rows for a geo element.
	 *
	 * @param element
	 *            the element
	 * @return true if both rows should be shown.
	 */
	public static boolean shouldShowBothRows(GeoElement element, AlgebraSettings algebraSettings) {
		boolean hasDifferentOutputFormats = !AlgebraOutputFormat.getPossibleFormats(
				element, algebraSettings.isEngineeringNotationEnabled()).isEmpty();
		boolean hasOutputRow = hasDifferentOutputFormats || hasDefinitionAndValueMode(element);
		return hasOutputRow && shouldShowOutputRow(element, algebraSettings.getStyle());
	}

	/**
	 * @param element - geo element
	 * @return whether has definition and value description mode
	 */
	public static boolean hasDefinitionAndValueMode(GeoElement element) {
		return element.getDescriptionMode() == DescriptionMode.DEFINITION_VALUE;
	}

	/**
	 *
	 * @param element
	 *            geo
	 * @param style
	 *            AV style
	 * @param stringTemplate
	 *            string template
	 * @return description string for element to show in AV row; null if element
	 *         prefers showing definition
	 */
	public static String getDescriptionString(GeoElement element, int style,
			StringTemplate stringTemplate) {

		if (element.mayShowDescriptionInsteadOfDefinition()) {
			IndexLaTeXBuilder builder = new IndexLaTeXBuilder();
			buildText(element, style, builder, stringTemplate);
			return getLatexText(builder.toString().replace("^", "\\^{\\;}"));
		}
		return null;
	}

	private static String getLatexText(String text) {
		return "\\text{" + text + '}';
	}

	/**
	 * @param geo1
	 *            geo
	 * @param limit
	 *            max length: fallback to plain text otherwise
	 * @param output
	 *            whether to substitute numbers
	 * @return LaTEX string
	 */
	public static String getLatexString(GeoElement geo1, Integer limit,
			boolean output) {
		Kernel kernel = geo1.getKernel();
		if (output && !geo1.isLaTeXDrawableGeo()) {
			return null;
		}
		if (geo1.getParentAlgorithm() instanceof AlgoFractionText) {
			return geo1.getAlgebraDescription(StringTemplate.latexTemplate);
		} else if (kernel.getAlgebraStyle() != AlgebraStyle.VALUE
				&& kernel
						.getAlgebraStyle() != AlgebraStyle.DEFINITION_AND_VALUE) {
			if (geo1.isIndependent()) {
				return getLatexStringValue(geo1, limit);
			} else if (Algos.isUsedFor(Algos.Expression, geo1)) {
				return geo1.getAssignmentLHS(StringTemplate.latexTemplate)
						+ geo1.getLabelDelimiter() + geo1.getDefinition(
						StringTemplate.latexTemplate);
			} else {
				return null;
			}
		}
		return getLatexStringValue(geo1, limit);
	}

	private static String getLatexStringValue(GeoElement geo1, Integer limit) {
		String text = geo1.getLaTeXAlgebraDescription(
				geo1.getDescriptionMode() != DescriptionMode.DEFINITION,
				StringTemplate.latexTemplate);

		if ((text != null) && (limit == null || (text.length() < limit))) {
			return text;
		}

		return null;
	}

	private static StringTemplate getOutputStringTemplate(GeoElement element) {
		return element.getApp().getConfig().getOutputStringTemplate();
	}

	/**
	 *
	 * @param element
	 *            the GeoElement for what we need to get the preview for AV
	 * @return the preview string for the given geoelement if there is any
	 */
	private static String getPreviewFormula(GeoElement element,
			StringTemplate stringTemplate) {
		Settings settings = element.getApp().getSettings();
		int algebraStyle = settings.getAlgebra().getStyle();
		int coordsFormat = settings.getGeneral().getCoordFormat();

		if (element.getParentAlgorithm() instanceof AlgoFractionText) {
			return element.getAlgebraDescription(stringTemplate);
		} else if (element.isPenStroke()) {
			return element.getLabelSimple();
		} else if ((AlgebraStyle.DESCRIPTION == algebraStyle || AlgebraStyle.VALUE == algebraStyle)
				&& !isTextItem(element)) {
			return getDescriptionString(element, algebraStyle, stringTemplate);
		} else if (CoordinatesFormat.COORD_FORMAT_AUSTRIAN == coordsFormat
				&& element.isGeoPoint()) {
			return element.toString(stringTemplate);
		} else {
			return null;
		}
	}

	/**
	 * Checks if the Algebra View should show a slider for this geo.
	 *
	 * @param geo geo element to test
	 * @return if Algebra View should show a slider for this geo
	 */
	public static boolean shouldShowSlider(GeoElement geo) {
		return geo instanceof GeoNumeric
				&& geo.getApp().getConfig().hasSlidersInAV()
				&& ((GeoNumeric) geo).isAVSliderOrCheckboxVisible() && geo.isSimple()
				&& Double.isFinite(((GeoNumeric) geo).value);
	}

	/**
	 * Initializes the element for the Algebra View.
	 * @param geo element to initialize
	 */
	public static void initForAlgebraView(GeoElement geo) {
		if (shouldShowSlider(geo) && !geo.isEuclidianVisible()) {
			((GeoNumeric) geo).initAlgebraSlider();
		}
	}

	/**
	 * Check if a geo element has an output value that is a fraction.
	 * @param geo element
	 * @return `true` if the geo element has an output value that is a fraction
	 */
	public static boolean evaluatesToFraction(GeoElementND geo) {
		if (geo instanceof GeoSymbolic) {
			GeoSymbolic symbolic = (GeoSymbolic) geo;
			ExpressionValue value = symbolic.getValue();
			if (value instanceof ExpressionNode) {
				return ((ExpressionNode) value).isSimpleFraction();
			}
		} else if (geo instanceof GeoNumeric) {
			return geo.getDefinition() != null
					&& geo.getDefinition().isFraction();
		}
		return false;
	}
}
