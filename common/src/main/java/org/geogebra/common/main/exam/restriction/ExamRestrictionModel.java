package org.geogebra.common.main.exam.restriction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.plugin.Operation;

/**
 * Model containing the exam restrictions.
 */
public class ExamRestrictionModel {
	private List<String> subAppCodes = Collections.emptyList();
	private String defaultAppCode;
	private CommandFilter commandFilter;
	private List<FeatureRestriction> featureRestrictions = Collections.emptyList();

	private ExpressionFilter expressionFilter;

	void setSubAppCodes(String... list) {
		subAppCodes = Arrays.asList(list);
	}

	/**
	 *
	 * @param subAppCode Sub application code to query.
	 * @return if the subApp specified is restricted during exam or not.
	 */
	public boolean isAppRestricted(String subAppCode) {
		return subAppCodes.contains(subAppCode);
	}

	/**
	 *
	 * @return the default subApp code if current one
	 * (before starting exam) is restricted
	 */
	public String getDefaultAppCode() {
		return defaultAppCode;
	}

	/**
	 * define default sub application code on exam mode start
	 * @param subAppCode - default sub app code
	 */
	public void setDefaultAppCode(String subAppCode) {
		defaultAppCode = subAppCode;
	}

	/**
	 *
	 * @return if model has restricted subApps.
	 */
	public boolean hasSubApps() {
		return !subAppCodes.isEmpty();
	}

	public CommandFilter getCommandFilter() {
		return commandFilter;
	}

	public void setCommandFilter(CommandFilter graphingCommandFilter) {
		this.commandFilter = graphingCommandFilter;
	}

	/**
	 * UI elements which should be disabled
	 * @param featureRestrictions - restriction to be added
	 */
	public void setFeatureRestrictions(FeatureRestriction... featureRestrictions) {
		this.featureRestrictions = Arrays.asList(featureRestrictions);
	}

	/**
	 * is certain feature restricted
	 * @param featureRestriction - UI element
	 * @return true, if UI element should be restricted
	 */
	public boolean isFeatureRestricted(FeatureRestriction featureRestriction) {
		return featureRestrictions.contains(featureRestriction);
	}

	/**
	 *
	 * @param expressionFilter to restrict expressions.
	 */
	public void setExpressionFilter(
			ExpressionFilter expressionFilter) {
		this.expressionFilter = expressionFilter;
	}

	/**
	 *
	 * @param node to check
	 * @return if it is allowed.
	 */
	public boolean isExpressionRestricted(ExpressionNode node) {
		return hasExpressionFilter() && !expressionFilter.isAllowed(node);
	}

	/**
	 *
	 * @return if the model have restrictions for expressions.
	 */
	public boolean hasExpressionFilter() {
		return expressionFilter != null;
	}

	/**
	 *
	 * @param operation to check.
	 * @return if operation is restricted.
	 */
	public boolean isOperationRestricted(Operation operation) {
		return hasExpressionFilter() && !expressionFilter.isAllowed(operation);
	}
}
