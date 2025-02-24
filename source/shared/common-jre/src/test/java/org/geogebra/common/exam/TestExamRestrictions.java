package org.geogebra.common.exam;

import static org.geogebra.common.contextmenu.InputContextMenuItem.Expression;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POINT;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.HIDE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.IGNORE;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.exam.restrictions.PropertyRestriction;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.DisabledAlgorithms;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.filter.ComplexExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilterFactory;
import org.geogebra.common.kernel.arithmetic.filter.RadianExpressionFilter;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.BaseCommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;

final class TestExamRestrictions extends ExamRestrictions {

	TestExamRestrictions(ExamType examType) {
		super(examType,
				Set.of(SuiteSubApp.CAS),
				SuiteSubApp.GRAPHING,
				Set.of(ExamFeatureRestriction.DATA_TABLE_REGRESSION),
				createExpressionFilters(),
				null,
				createCommandFilters(),
				createCommandArgumentFilter(),
				null,
				createContextMenuItemFilters(),
				createSyntaxFilter(),
				createToolCollectionFilter(),
				createPropertyRestrictions(),
				createVisibilityRestrictions(),
				null,
				createDisabledAlgorithms());
	}

	private static Set<CommandFilter> createCommandFilters() {
		CommandNameFilter nameFilter = new CommandNameFilter(true,
				Commands.Derivative, Commands.NDerivative, Commands.Integral,
				Commands.IntegralSymbolic, Commands.IntegralBetween, Commands.NIntegral,
				Commands.Solve, Commands.SolveQuartic, Commands.SolveODE, Commands.SolveCubic,
				Commands.Solutions, Commands.NSolve, Commands.NSolveODE, Commands.NSolutions);
		return Set.of(nameFilter);
	}

	private static Set<ExpressionFilter> createExpressionFilters() {
		return Set.of(
				ExpressionFilterFactory.createOperationsExpressionFilter(Operation.OR,
						Operation.AND),
				new ComplexExpressionFilter(),
				new RadianExpressionFilter());
	}

	private static ToolCollectionFilter createToolCollectionFilter() {
		return new ToolCollectionSetFilter(MODE_POINT);
	}

	private static Set<ContextMenuItemFilter> createContextMenuItemFilters() {
		return Set.of(item -> !item.equals(Expression));
	}

	private static SyntaxFilter createSyntaxFilter() {
		LineSelectorSyntaxFilter filter = new LineSelectorSyntaxFilter();
		// Max [ <Function>, <Start x-Value>, <End x-Value> ]
		filter.addSelector(Commands.Max, 4);
		return filter;
	}

	private static Set<CommandArgumentFilter> createCommandArgumentFilter() {
		return Set.of(new BaseCommandArgumentFilter() {
			@Override
			public void checkAllowed(Command command, CommandProcessor commandProcessor)
					throws MyError {
				if (isCommand(command, Commands.Max)) {
					if (command.getArgumentNumber() != 3) {
						throw commandProcessor.argNumErr(command, command.getArgumentNumber());
					}
				}
			}
		});
	}

	private static Map<String, PropertyRestriction> createPropertyRestrictions() {
		return Map.of("AngleUnit", new PropertyRestriction(true, value ->
				value != Integer.valueOf(Kernel.ANGLE_DEGREES_MINUTES_SECONDS)));
	}

	static Set<VisibilityRestriction> createVisibilityRestrictions() {
		return Set.of(geoElement -> geoElement.isGeoPoint() ? HIDE : IGNORE,
				geoElement -> geoElement.isInequality() ? HIDE : IGNORE);
	}

	private static Set<DisabledAlgorithms> createDisabledAlgorithms() {
		return Set.of(DisabledAlgorithms.TangentPointConic,
				DisabledAlgorithms.TangentLineConic);
	}
}
