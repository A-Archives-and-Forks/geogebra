package org.geogebra.common.main.settings.config;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.NOTES_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SCIENTIFIC_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.factory.DefaultPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesFactory;

/**
 * Config for Classic and derived apps (MR)
 */
public class AppConfigDefault extends AbstractAppConfig {
	public AppConfigDefault() {
		super(GeoGebraConstants.CLASSIC_APPCODE);
	}

	AppConfigDefault(String appCode) {
		super(appCode, null);
	}

	@Override
	public void adjust(DockPanelData dp) {
		// do nothing
	}

	@Override
	public String getAVTitle() {
		return "Algebra";
	}

	@Override
	public String getAppTitle() {
		return "math_apps";
	}

	@Override
	public String getAppName() {
		return getAppTitle();
	}

	@Override
	public String getAppNameShort() {
		return getAppTitle();
	}

	@Override
	public String getAppNameWithoutCalc() {
		return getAppNameShort();
	}

	/**
	 * @param appName app name
	 * @return whether app name is one of the unbundled apps
	 */
	public static boolean isUnbundled(String appName) {
		return GRAPHING_APPCODE.equals(appName) || GEOMETRY_APPCODE.equals(appName)
				|| CAS_APPCODE.equals(appName) || G3D_APPCODE.equals(appName)
				|| SCIENTIFIC_APPCODE.equals(appName) || SUITE_APPCODE.equals(appName);
	}

	/**
	 * @param appName app name
	 * @return whether the app is not classic
	 */
	public static boolean isUnbundledOrNotes(String appName) {
		return isUnbundled(appName) || NOTES_APPCODE.equals(appName);
	}

	@Override
	public String getTutorialKey() {
		return "TutorialClassic";
	}

	@Override
	public boolean showKeyboardHelpButton() {
		return true;
	}

	@Override
	public boolean isSimpleMaterialPicker() {
		return false;
	}

	@Override
	public boolean hasPreviewPoints() {
		return false;
	}

	@Override
	public boolean allowsSuggestions() {
		return true;
	}

	@Override
	public boolean shouldKeepRatioEuclidian() {
		return false;
	}

	@Override
	public int getDefaultPrintDecimals() {
		return Kernel.STANDARD_PRINT_DECIMALS_SHORT;
	}

	@Override
	public boolean hasSingleEuclidianViewWhichIs3D() {
		return false;
	}

	@Override
	public int[] getDecimalPlaces() {
		return new int[]{0, 1, 2, 3, 4, 5, 10, 15};
	}

	@Override
	public int[] getSignificantFigures() {
		return new int[]{3, 5, 10, 15};
	}

	@Override
	public boolean isGreekAngleLabels() {
		return true;
	}

	@Override
	public boolean isCASEnabled() {
		return true;
	}

	@Override
	public String getPreferencesKey() {
		return "";
	}

	@Override
	public String getForcedPerspective() {
		return null;
	}

	@Override
	public boolean isEnableStructures() {
		return true;
	}

	@Override
	public AppType getToolbarType() {
		return AppType.CLASSIC;
	}

	@Override
	public boolean showGridOnFileNew() {
		return true;
	}

	@Override
	public boolean showAxesOnFileNew() {
		return true;
	}

	@Override
	public boolean hasTableView() {
		return false;
	}

	@Override
	public SymbolicMode getSymbolicMode() {
		return SymbolicMode.NONE;
	}

	@Override
	public boolean hasSlidersInAV() {
		return true;
	}

	@Override
	public boolean hasAutomaticLabels() {
		return true;
	}

	@Override
	public boolean hasAutomaticSliders() {
		return true;
	}

	@Override
	public int getDefaultAlgebraStyle() {
		return AlgebraStyle.DEFINITION_AND_VALUE;
	}

	@Override
	public String getDefaultSearchTag() {
		return "";
	}

	@Override
	public LabelVisibility getDefaultLabelingStyle() {
		return LabelVisibility.Automatic;
	}

	@Override
	public CommandFilter createCommandFilter() {
		return null;
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return null;
	}

	@Override
	public @CheckForNull SyntaxFilter newCommandSyntaxFilter() {
		return null;
	}

	@Override
	public boolean showToolsPanel() {
		return true;
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.CLASSIC;
	}

	@Override
	public boolean hasExam() {
		return false;
	}

	@Override
	public String getExamMenuItemText() {
		return "";
	}

	@Override
	public ExpressionFilter createExpressionFilter() {
		return null;
	}

	@Override
	public ParserFunctionsFactory createParserFunctionsFactory() {
		return ParserFunctionsFactory.createParserFunctionsFactory();
	}

	@Override
	public Set<FillType> getAvailableFillTypes() {
		return new HashSet<>(Arrays.asList(FillType.values()));
	}

	@Override
	public boolean isObjectDraggingRestricted() {
		return false;
	}

	@Override
	public int getDefaultAngleUnit() {
		return Kernel.ANGLE_DEGREE;
	}

	@Override
	public boolean isAngleUnitSettingEnabled() {
		return true;
	}

	@Override
	public boolean isCoordinatesObjectSettingEnabled() {
		return true;
	}

	@Override
	public PropertiesFactory createPropertiesFactory() {
		return new DefaultPropertiesFactory();
	}

	@Override
	public boolean disableTraceCM() {
		return false;
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		return AppKeyboardType.GRAPHING;
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

	@Override
	public boolean hasAnsButtonInAv() {
		return false;
	}

	@Override
	public StringTemplate getOutputStringTemplate() {
		return StringTemplate.latexTemplate;
	}

	@Override
	public boolean sendKeyboardEvents() {
		return false;
	}

	@Override
	public boolean hasLabelForDescription() {
		return true;
	}

	@Override
	public boolean hasEuclidianView() {
		return true;
	}

	@Override
	public boolean hasDistributionView() {
		return false;
	}

	@Override
	public boolean hasAlgebraView() {
		return true;
	}

	@Override
	public boolean hasSpreadsheetView() {
		return false;
	}
}
