package org.geogebra.common.main;

import java.io.Serializable;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.exam.restrictions.ExamRestrictable;
import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.main.settings.updater.SettingsUpdater;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.factory.PropertiesFactory;

/**
 * Application configuration.
 */
public interface AppConfig extends ExamRestrictable, Serializable {

	/**
	 * Adjust panel positions.
	 * @param dp dock panel data
	 */
	void adjust(DockPanelData dp);

	@MissingDoc
	String getAVTitle();

	/**
	 * @return translation key for short app name (Scientific Calculator)
	 */
	String getAppTitle();

	/**
	 * @return translation key for full app name (GeoGebra Scientific Calculator)
	 */
	String getAppName();

	/**
	 * @return translation key for short app name (Sci Calc)
	 */
	String getAppNameShort();

	/**
	 * @return translation key for short app name (Graphing)
	 */
	String getAppNameWithoutCalc();

	@MissingDoc
	String getTutorialKey();

	@MissingDoc
	boolean showKeyboardHelpButton();

	@MissingDoc
	boolean isSimpleMaterialPicker();

	@MissingDoc
	boolean hasPreviewPoints();

	@MissingDoc
	boolean allowsSuggestions();

	/**
	 * @return whether zoom to fit should keep aspect ratio
	 */
	boolean shouldKeepRatioEuclidian();

	@MissingDoc
	int getDefaultPrintDecimals();

	@MissingDoc
	boolean hasSingleEuclidianViewWhichIs3D();

	/**
	 * @return the decimal places that this app uses.
	 */
	int[] getDecimalPlaces();

	/**
	 * @return the significant places that this app uses.
	 */
	int[] getSignificantFigures();

	/**
	 * @return whether the characters for the angle should be greek
	 */
	boolean isGreekAngleLabels();

	/**
	 * @return whether to allow CAS commands in AV
	 */
	boolean isCASEnabled();

	/**
	 * @return suffix for preferences (in web)
	 */
	String getPreferencesKey();

	/**
	 * @return preferred perspective ID or null if user setting should be used
	 */
	String getForcedPerspective();

	/**
	 * @return whether match structures (functions, equations, vectors) are
	 *         enabled
	 */
	boolean isEnableStructures();

	/**
	 * 
	 * @return the toolbar type of the current app.
	 */
	AppType getToolbarType();

    /**
     *
     * @return true if grid is shown at start on the active (main) euclidian view
     */
	boolean showGridOnFileNew();

    /**
     *
     * @return true if axes are shown at start on the active (main) euclidian view
     */
    boolean showAxesOnFileNew();

	/**
	 * @return whether table view is available
	 */
	boolean hasTableView();

	/**
	 * @return symbolic mode for algebra view
	 */
	SymbolicMode getSymbolicMode();

	/**
	 * @return whether sliders in AV are allowed
	 */
	boolean hasSlidersInAV();

	/**
	 * @return true if sliders are created automatically
	 */
	boolean hasAutomaticSliders();

	/**
	 * @return whether objects should be labeled a, b, ...
	 */
	boolean hasAutomaticLabels();

	/**
	 * @return algebra style
	 */
	@Nonnull AlgebraStyle getDefaultAlgebraStyle();

	/**
	 * @return search tag for Open Material screen
	 */
	String getDefaultSearchTag();

	/**
	 * @return labeling style
	 */
	LabelVisibility getDefaultLabelingStyle();

	/**
	 * @return the Command filter for the app.
	 */
	@CheckForNull CommandFilter getCommandFilter();

	/**
	 * @return new command filter for the app.
	 */
	@CheckForNull CommandFilter createCommandFilter();

	/**
	 * @return the Command Argument filter for the app.
	 */
	@CheckForNull CommandArgumentFilter getCommandArgumentFilter();

	/**
	 * @return command syntax filter
	 */
	@CheckForNull SyntaxFilter newCommandSyntaxFilter();

	/**
	 * @return whether the app should show the tools panel or not
	 */
	boolean showToolsPanel();

	/**
	 * @return with the app code which is also used in the url, like graphing,cas,
	 * classic etc..
	 */
	String getAppCode();

	/**
	 * @return The sub-app code if exists.
	 * E.g. in the Suite app the Graphing sub-app has "suite" app code and "graphing" sub-app code.
	 */
	@CheckForNull String getSubAppCode();

	/**
	 * @return The sub-app code if exists.
	 * E.g. in the Suite app the Graphing sub-app has "suite" app code and "graphing" sub-app code.
	 */
	@CheckForNull SuiteSubApp getSubApp();

	/**
	 * @return creates a settings updater
	 */
	SettingsUpdater createSettingsUpdater();

	/**
	 * Get the app version in enum.
	 * @return app version
	 */
	GeoGebraConstants.Version getVersion();

	/**
	 * @return weather has exam or not (currently only graphing and cas)
	 */
	boolean hasExam();

	/**
	 * @return the ggbtranskey for the exam starting menu item in the MainMenu
	 */
	String getExamMenuItemText();

	/**
	 * Create app specific operation argument filter.
	 * <code>null</code> is allowed.
	 *
	 * @return operation argument filter
	 */
	@CheckForNull ExpressionFilter createExpressionFilter();

	/**
	 * Unlike {@link #createExpressionFilter()} this always returns the same instance.
	 * @return expression filter
	 */
	ExpressionFilter getExpressionFilter();

	/**
	 * @return creates app specific parser functions
	 */
	ParserFunctionsFactory createParserFunctionsFactory();

	/**
	 * @return true if it has 'ans' button in the AV.
	 */
	boolean hasAnsButtonInAv();

	/**
	 * Get the available fill types.
	 *
	 * @return fill types
	 */
	Set<FillType> getAvailableFillTypes();

	/**
	 * This replaces the previous, semantically unclear {@code getLineDisplayStyle()},
	 * {@code getEnforcedLineEquationForm()}, {@code getEnforcedConicEquationForm()}.
	 * @return This app config's equation behaviour
	 */
	@Nonnull EquationBehaviour getEquationBehaviour();

	/**
	 * Initializes this app config's equation behaviour to its default value
	 */
	void initializeEquationBehaviour();

	/**
	 * Whether it shows the equation in AV.
	 *
	 * @return true if equation should be hidden in AV
	 */
	boolean shouldHideEquations();

	/**
	 * @return whether the apps uses restricted dragging for certain objects or not
	 */
	boolean isObjectDraggingRestricted();

	/**
	 * @return type of keyboard based on the app
	 */
	AppKeyboardType getKeyboardType();

	/**
	 * @return default angle unit
	 */
	int getDefaultAngleUnit();

	/**
	 * @return true if the angle unit setting is enabled
	 */
	boolean isAngleUnitSettingEnabled();

	/**
	 * @return true if the coordinates object setting is enabled
	 */
	boolean isCoordinatesObjectSettingEnabled();

	/**
	 * @return new PropertiesFactory instance
	 */
	PropertiesFactory createPropertiesFactory();

	/**
	 * @return true if trace is enabled in context menu
	 */
	boolean disableTraceCM();

	/**
	 * @return the template to serialize the output
	 */
	StringTemplate getOutputStringTemplate();

	/**
	 * @return if closing/opening keyboard should send event
	 * (only for evaluator for now)
	 */
	boolean sendKeyboardEvents();

	/**
	 * @return true if label should be shown for description AV style
	 */
	boolean hasLabelForDescription();

	/**
	 * @return true if the app has at least one Euclidian View
	 */
	boolean hasEuclidianView();

	/**
	 * @return true if the app has distribution view
	 */
	boolean hasDistributionView();

	/**
	 * @return true if the app has algebra view
	 */
	boolean hasAlgebraView();

	/**
	 * @return the translation key of the application
	 * (always the key of suite if in a subApp)
	 */
	String getAppTransKey();

	/**
	 * @return ID of the main graphics view (EV, EV 3D or probability)
	 */
	int getMainGraphicsViewId();

	@MissingDoc
	boolean hasOneVarStatistics();

	/**
	 * @return true, if app has spreadsheet view
	 */
	boolean hasSpreadsheetView();

	@MissingDoc
	boolean hasDataImport();
}
