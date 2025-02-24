package org.geogebra.common.exam.restrictions;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.ToolsProvider;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.ScheduledPreviewFromInputBar;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.DisabledAlgorithms;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.ExamCommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.GeoElementSetup;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.GeoElementPropertyFilter;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;

/**
 * Represents restrictions that apply during exams.
 * <p>
 * Restrictions that are specific to the different exam types are represented as subclasses
 * of this class.
 * Restrictions that apply to all exam types should be implemented in this class
 * (in {@link #applyTo(AlgoDispatcher, CommandDispatcher, AlgebraProcessor, PropertiesRegistry,
 * Object, Localization, Settings, AutocompleteProvider, ToolsProvider, GeoElementPropertiesFactory,
 * ScheduledPreviewFromInputBar, ContextMenuFactory)}).
 * </p><p>
 * Any restrictions to be applied during exams should be implemented in here (so that
 * everything is one place):
 * </p>
 * <ul>
 * <li>disabled subapps,</li>
 * <li>restricted commands,</li>
 * <li>restricted expressions,</li>
 * <li>frozen properties,</li>
 * <li>etc.</li>
 * </ul>
 */
public class ExamRestrictions implements PropertiesRegistryListener {

	private final @Nonnull ExamType examType;
	private final @Nonnull Set<SuiteSubApp> disabledSubApps;
	private final @Nonnull SuiteSubApp defaultSubApp;
	private final @Nonnull Set<ExamFeatureRestriction> featureRestrictions;
	private final @Nonnull Set<ExpressionFilter> inputExpressionFilters;
	private final @Nonnull Set<ExpressionFilter> outputExpressionFilters;
	private final @Nonnull Set<CommandFilter> commandFilters;
	private final @Nonnull Set<Operation> filteredOperations;
	private final @Nonnull Set<CommandArgumentFilter> commandArgumentFilters;
	private final @Nonnull Set<ContextMenuItemFilter> contextMenuItemFilters;
	private final @Nonnull Set<DisabledAlgorithms> disabledAlgorithms;
	// filter independent of exam region
	private final CommandArgumentFilter examCommandArgumentFilter =
			new ExamCommandArgumentFilter();
	private final SyntaxFilter syntaxFilter;
	private final ToolCollectionFilter toolsFilter;
	private final Map<String, PropertyRestriction> propertyRestrictions;
	private final GeoElementPropertyFilter restrictedGeoElementVisibilityPropertyFilter;
	private final GeoElementSetup restrictedGeoElementVisibilitySetup;
	private final @Nullable EquationBehaviour equationBehaviour;
	private @Nullable EquationBehaviour originalEquationBehaviour;
	private RestorableSettings savedSettings;
	private Settings restrictedSettings = null;
	private ConstructionDefaults restrictedDefaults;

	/**
	 * Factory for ExamRestrictions.
	 * @param examType The exam type.
	 * @return An {@link ExamRestrictions} subclass that contains all the restrictions for
	 * the given exam type.
	 */
	public static ExamRestrictions forExamType(ExamType examType) {
		switch (examType) {
		case BAYERN_CAS:
			return new BayernCasExamRestrictions();
		case CVTE:
			return new CvteExamRestrictions();
		case IB:
			return new IBExamRestrictions();
		case NIEDERSACHSEN:
			return new NiedersachsenExamRestrictions();
		case REALSCHULE:
			return new RealschuleExamRestrictions();
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		case MMS:
			return new MmsExamRestrictions();
		default:
			return new GenericExamRestrictions();
		}
	}

	/**
	 * Prevent instantiation, except by subclasses.
	 * @param examType The exam type.
	 * @param disabledSubApps An optional set of disabled subapps for this exam type. Passing in
	 * null is equivalent to passing in an empty set.
	 * @param defaultSubApp An optional subapp to activate at the start of an exam, if the
	 * current subapp is in the list of restricted subapps. If null, Graphing will be used as the
	 * default subapp.
	 * @param featureRestrictions An optional set of features to disable during the exam.
	 * @param inputExpressionFilters An optional set of expression filters (e.g., ||) to apply during
	 * exams to the algebra inputs.
	 * @param outputExpressionFilters An optional set of expression filters (e.g., ||) to apply during
	 * exams to the algebra outputs.
	 * @param commandFilters An optional command filter to apply during exams.
	 * @param commandArgumentFilters An optional command argument filter to apply during exams.
	 * @param filteredOperations An optional set of operations to filter out during exams.
	 * @param syntaxFilter An optional syntax filter to apply during exams.
	 * @param toolsFilter An optional filter for tools that should be unavailable during the exam.
	 * If this argument is null, the Image tool will still be filtered out (APPS-5214). When
	 * providing a non-null filter here, it should include the Image tool.
	 * @param propertyRestrictions An optional map of properties and restrictions
	 * to be applied to them during the exam.
	 */
	protected ExamRestrictions(
			@Nonnull ExamType examType,
			@Nullable Set<SuiteSubApp> disabledSubApps,
			@Nullable SuiteSubApp defaultSubApp,
			@Nullable Set<ExamFeatureRestriction> featureRestrictions,
			@Nullable Set<ExpressionFilter> inputExpressionFilters,
			@Nullable Set<ExpressionFilter> outputExpressionFilters,
			@Nullable Set<CommandFilter> commandFilters,
			@Nullable Set<CommandArgumentFilter> commandArgumentFilters,
			@Nullable Set<Operation> filteredOperations,
			@Nullable Set<ContextMenuItemFilter> contextMenuItemFilters,
			@Nullable SyntaxFilter syntaxFilter,
			@Nullable ToolCollectionFilter toolsFilter,
			@Nullable Map<String, PropertyRestriction> propertyRestrictions,
			@Nullable Set<VisibilityRestriction> visibilityRestrictions,
			@Nullable EquationBehaviour equationBehaviour,
			@Nullable Set<DisabledAlgorithms> disabledAlgorithms) {
		this.examType = examType;
		this.disabledSubApps = disabledSubApps != null ? disabledSubApps : Set.of();
		this.defaultSubApp = defaultSubApp != null ? defaultSubApp : SuiteSubApp.GRAPHING;
		this.featureRestrictions = featureRestrictions != null ? featureRestrictions : Set.of();
		this.inputExpressionFilters =
				inputExpressionFilters != null ? inputExpressionFilters : Set.of();
		this.outputExpressionFilters =
				outputExpressionFilters != null ? outputExpressionFilters : Set.of();
		this.commandFilters = commandFilters != null ? commandFilters : Set.of();
		this.commandArgumentFilters = commandArgumentFilters != null
				? commandArgumentFilters : Set.of();
		this.filteredOperations = filteredOperations != null ? filteredOperations : Set.of();
		this.contextMenuItemFilters =
				contextMenuItemFilters != null ? contextMenuItemFilters : Set.of();
		this.syntaxFilter = syntaxFilter;
		this.toolsFilter = toolsFilter != null ? toolsFilter
				: new ToolCollectionSetFilter(EuclidianConstants.MODE_IMAGE);
		this.propertyRestrictions = propertyRestrictions != null ? propertyRestrictions : Map.of();
		this.restrictedGeoElementVisibilityPropertyFilter =
				createRestrictedGeoElementVisibilityPropertyFilter(visibilityRestrictions != null
						? visibilityRestrictions : Set.of());
		this.restrictedGeoElementVisibilitySetup =
				createRestrictedGeoElementVisibilitySetup(visibilityRestrictions != null
						? visibilityRestrictions : Set.of());
		this.equationBehaviour = equationBehaviour;
		this.disabledAlgorithms = disabledAlgorithms != null ? disabledAlgorithms : Set.of();
	}

	/**
	 * @return The exam type.
	 */
	public final @Nonnull ExamType getExamType() {
		return examType;
	}

	/**
	 * @return The list of disabled (i.e., not allowed) subapps during exams, or an empty set
	 * if there is no restriction on the available subapps.
	 */
	public final @Nonnull Set<SuiteSubApp> getDisabledSubApps() {
		return disabledSubApps;
	}

	/**
	 * @return The default subapp to switch to if a disabled subapp is active when
	 * the exam starts.
	 */
	public final @Nonnull SuiteSubApp getDefaultSubApp() {
		return defaultSubApp;
	}

	/**
	 * @return The set of disabled features during exam, or an empty set if there's no
	 * restrictions on available features in the apps.
	 */
	public final @Nonnull Set<ExamFeatureRestriction> getFeatureRestrictions() {
		return featureRestrictions;
	}

	/**
	 * Apply the exam restrictions.
	 */
	public void applyTo(
			@Nullable AlgoDispatcher algoDispatcher,
			@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context,
			@Nullable Localization localization,
			@Nullable Settings settings,
			@Nullable AutocompleteProvider autoCompleteProvider,
			@Nullable ToolsProvider toolsProvider,
			@Nullable GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nullable ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
			@Nullable ContextMenuFactory contextMenuFactory,
			@Nullable Construction construction) {
		if (algoDispatcher != null) {
			algoDispatcher.addDisabledAlgorithms(disabledAlgorithms);
		}
		if (commandDispatcher != null) {
			for (CommandFilter commandFilter : commandFilters) {
				commandDispatcher.addCommandFilter(commandFilter);
			}
			commandDispatcher.addCommandArgumentFilter(examCommandArgumentFilter);
			for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
				commandDispatcher.addCommandArgumentFilter(commandArgumentFilter);
			}
		}
		if (algebraProcessor != null) {
			for (ExpressionFilter expressionFilter : inputExpressionFilters) {
				algebraProcessor.addInputExpressionFilter(expressionFilter);
			}
			for (ExpressionFilter expressionFilter : outputExpressionFilters) {
				algebraProcessor.addOutputExpressionFilter(expressionFilter);
			}
			algebraProcessor.reinitCommands();
			if (equationBehaviour != null) {
				originalEquationBehaviour = algebraProcessor.getKernel().getEquationBehaviour();
				algebraProcessor.getKernel().setEquationBehaviour(equationBehaviour);
			}
		}
		if (syntaxFilter != null) {
			if (autoCompleteProvider != null) {
				autoCompleteProvider.addSyntaxFilter(syntaxFilter);
			}
			if (localization != null) {
				localization.getCommandSyntax().addSyntaxFilter(syntaxFilter);
			}
		}
		if (autoCompleteProvider != null) {
			autoCompleteProvider.setFilteredOperations(filteredOperations);
		}
		if (propertiesRegistry != null) {
			propertyRestrictions.forEach((name, restriction) -> {
				Property property = propertiesRegistry.lookup(name, context);
				if (property != null) {
					restriction.applyTo(property);
				}
			});
		}
		if (toolsProvider != null && toolsFilter != null) {
			toolsProvider.addToolsFilter(toolsFilter);
		}
		if (geoElementPropertiesFactory != null) {
			geoElementPropertiesFactory.addFilter(restrictedGeoElementVisibilityPropertyFilter);
			propertyRestrictions.forEach(geoElementPropertiesFactory::addRestriction);
		}
		if (algebraProcessor != null) {
			algebraProcessor.addGeoElementSetup(restrictedGeoElementVisibilitySetup);
		}
		if (scheduledPreviewFromInputBar != null) {
			scheduledPreviewFromInputBar.addGeoElementSetup(restrictedGeoElementVisibilitySetup);
		}
		if (contextMenuFactory != null) {
			contextMenuItemFilters.forEach(contextMenuFactory::addFilter);
		}
		if (settings != null && construction != null) {
			this.restrictedSettings = settings;
			this.restrictedDefaults = construction.getConstructionDefaults();
			saveSettings(settings, restrictedDefaults);
			applySettingsRestrictions(settings, restrictedDefaults);
			construction.initUndoInfo();
		}
	}

	/**
	 * Remove the exam restrictions (i.e., undo the changes from
	 * {@link #applyTo(AlgoDispatcher, CommandDispatcher, AlgebraProcessor, PropertiesRegistry,
	 * Object, Localization, Settings, AutocompleteProvider, ToolsProvider,
	 * GeoElementPropertiesFactory, ScheduledPreviewFromInputBar, ContextMenuFactory)}).
	 */
	public void removeFrom(
			@Nullable AlgoDispatcher algoDispatcher,
			@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context,
			@Nullable Localization localization,
			@Nullable Settings settings,
			@Nullable AutocompleteProvider autoCompleteProvider,
			@Nullable ToolsProvider toolsProvider,
			@Nullable GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nullable ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
			@Nullable ContextMenuFactory contextMenuFactory,
			@Nullable Construction construction) {
		if (algoDispatcher != null) {
			algoDispatcher.removeDisabledAlgorithms(disabledAlgorithms);
		}
		if (commandDispatcher != null) {
			for (CommandFilter commandFilter : commandFilters) {
				commandDispatcher.removeCommandFilter(commandFilter);
			}
			commandDispatcher.removeCommandArgumentFilter(examCommandArgumentFilter);
			for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
				commandDispatcher.removeCommandArgumentFilter(commandArgumentFilter);
			}
		}
		if (algebraProcessor != null) {
			for (ExpressionFilter expressionFilter : inputExpressionFilters) {
				algebraProcessor.removeInputExpressionFilter(expressionFilter);
			}
			for (ExpressionFilter expressionFilter : outputExpressionFilters) {
				algebraProcessor.removeOutputExpressionFilter(expressionFilter);
			}
			algebraProcessor.reinitCommands();
			if (equationBehaviour != null) { // only restore it if we overwrote it
				algebraProcessor.getKernel().setEquationBehaviour(originalEquationBehaviour);
			}
		}
		if (syntaxFilter != null) {
			if (autoCompleteProvider != null) {
				autoCompleteProvider.removeSyntaxFilter(syntaxFilter);
			}
			if (localization != null) {
				localization.getCommandSyntax().removeSyntaxFilter(syntaxFilter);
			}
		}
		if (autoCompleteProvider != null) {
			autoCompleteProvider.setFilteredOperations(null);
		}
		if (propertiesRegistry != null) {
			propertyRestrictions.forEach((name, restriction) -> {
				Property property = propertiesRegistry.lookup(name, context);
				if (property != null) {
					restriction.removeFrom(property);
				}
			});
		}
		if (toolsProvider != null && toolsFilter != null) {
			toolsProvider.removeToolsFilter(toolsFilter);
		}
		if (geoElementPropertiesFactory != null) {
			geoElementPropertiesFactory.removeFilter(restrictedGeoElementVisibilityPropertyFilter);
			propertyRestrictions.forEach(geoElementPropertiesFactory::removeRestriction);
		}
		if (algebraProcessor != null) {
			algebraProcessor.removeGeoElementSetup(restrictedGeoElementVisibilitySetup);
		}
		if (scheduledPreviewFromInputBar != null) {
			scheduledPreviewFromInputBar.removeGeoElementSetup(restrictedGeoElementVisibilitySetup);
		}
		if (contextMenuFactory != null) {
			contextMenuItemFilters.forEach(contextMenuFactory::removeFilter);
		}
		if (settings != null && construction != null) {
			removeSettingsRestrictions(settings, construction.getConstructionDefaults());
		}
	}

	// Settings

	/**
	 * Creates an object that settings can be saved in exam start, and can be easily restored
	 * at exam exit.
	 * @return {@link RestorableSettings}
	 */
	protected RestorableSettings createSavedSettings() {
		return null;
	}

	/**
	 * Re-apply settings changes for this exam type (for ClearAll during exam).
	 */
	public void reapplySettingsRestrictions() {
		if (restrictedSettings != null) {
			applySettingsRestrictions(restrictedSettings, restrictedDefaults);
		}
	}

	/**
	 * Apply settings changes for this exam type.
	 * @param settings {@link Settings}
	 * @apiNote Override this only if the given exam needs custom settings.
	 */
	public void applySettingsRestrictions(@Nonnull Settings settings,
			@Nonnull ConstructionDefaults defaults) {
		// empty by default
	}

	private void saveSettings(Settings settings, ConstructionDefaults defaults) {
		savedSettings = createSavedSettings();
		if (savedSettings != null) {
			savedSettings.save(settings, defaults);
		}
	}

	/**
	 * Revert changes applied in {@link #applySettingsRestrictions(Settings)}, restoring the
	 * previously saved settings.
	 * @param settings {@link Settings}
	 * @apiNote An override is not needed by default.
	 */
	protected void removeSettingsRestrictions(@Nonnull Settings settings,
			ConstructionDefaults defaults) {
		if (savedSettings != null) {
			savedSettings.restore(settings, defaults);
			savedSettings = null;
			restrictedSettings = null;
		}
	}

	// - PropertiesRegistryListener

	/**
	 * Handles freezing properties on lazy property instantiation/registration.
	 * @param property A property that just got registered.
	 */
	@Override
	public void propertyRegistered(@Nonnull Property property, Object context) {
		if (propertyRestrictions.containsKey(property.getRawName())) {
			propertyRestrictions.get(property.getRawName()).applyTo(property);
		}
	}

	/**
	 * Handles unfreezing any frozen properties on deregistration.
	 * @param property A property that just got unregistered.
	 */
	@Override
	public void propertyUnregistered(@Nonnull Property property, Object context) {
		if (propertyRestrictions.containsKey(property.getRawName())) {
			propertyRestrictions.get(property.getRawName()).removeFrom(property);
		}
	}

	private static GeoElementSetup createRestrictedGeoElementVisibilitySetup(
			Set<VisibilityRestriction> visibilityRestrictions) {
		return geoElementND -> {
			if (VisibilityRestriction.isVisibilityRestricted(geoElementND.toGeoElement(),
					visibilityRestrictions)) {
				geoElementND.toGeoElement().setRestrictedEuclidianVisibility(true);
			}
		};
	}

	private static GeoElementPropertyFilter createRestrictedGeoElementVisibilityPropertyFilter(
			Set<VisibilityRestriction> visibilityRestrictions) {
		return (property, geoElement) -> {
			if (property instanceof ShowObjectProperty) {
				return !VisibilityRestriction.isVisibilityRestricted(geoElement,
						visibilityRestrictions);
			}
			return true;
		};
	}
}
