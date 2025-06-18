package org.geogebra.common.gui.view.table.regression;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.statistics.Regression;

public final class StandardRegressionSpecification implements RegressionSpecification {

	private final Regression regression;
	private final int degree;
	private final String label;
	private final String formula;
	private final String coeffOrdering;

	StandardRegressionSpecification(Regression regression, int polynomialDegree, String formula,
			String coefficientOrdering) {
		this.regression = regression;
		this.degree = polynomialDegree;
		this.label = polynomialDegree > 1 ? getPolynomialLabel(degree) : regression.getLabel();
		this.formula = polynomialDegree > 0 ? getPolynomialFormula(degree) : formula;
		this.coeffOrdering = coefficientOrdering;
	}

	private static String getPolynomialLabel(int degree) {
		switch (degree) {
		case 2: return "Quadratic";
		case 3: return "Cubic";
		default: return "Quartic";
		}
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Command buildCommand(Kernel kernel, MyVecNode points) {
		Command cleanData = new Command(kernel, Commands.RemoveUndefined.getCommand(),
				false);
		cleanData.addArgument(points.wrap());
		return regression.buildCommand(kernel, degree, cleanData);
	}

	@Override
	public String getFormula() {
		return formula;
	}

	/**
	 * @param polynomialDegree polynomial degree (for polynomial regression)
	 * @return formula
	 */
	private static String getPolynomialFormula(int polynomialDegree) {
		StringBuilder sb = new StringBuilder();
		sb.append("y = ");
		char coeffName = 'a';
		for (int i = polynomialDegree; i >= 0; i--, coeffName++) {
			sb.append(coeffName);
			if (i == 1) {
				sb.append("\\ x+");
			} else if (i > 0) {
				sb.append("\\ x^{");
				sb.append(i);
				sb.append("}+");
			}
		}
		return sb.toString();
	}

	@Override
	public String getCoeffOrdering() {
		return coeffOrdering;
	}

	@Override
	public boolean hasCorrelationCoefficient() {
		return regression == Regression.LINEAR;
	}

	@Override
	public boolean canPlot() {
		return true;
	}

	@Override
	public boolean hasCoefficientOfDetermination() {
		return true;
	}
}
