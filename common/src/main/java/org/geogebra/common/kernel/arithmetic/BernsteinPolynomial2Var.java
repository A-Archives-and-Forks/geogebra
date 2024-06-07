package org.geogebra.common.kernel.arithmetic;


import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public class BernsteinPolynomial2Var implements BernsteinPolynomial {
	private final double xmin;
	private final double xmax;
	private final FunctionVariable fvX;
	private final FunctionVariable fvY;
	private int degree;
	private ExpressionNode output;
	private final Kernel kernel;
	private final FunctionVariable[] variables;
	private BernsteinPolynomial[] coeffs;
	private final int degreeX;
	private final int degreeY;
	private ExpressionNode[][] bernsteinCoeffs;
	public BernsteinPolynomial2Var(Kernel kernel, Polynomial polynomial,
			FunctionVariable[] variables, double xmin, double xmax, int degreeX, int degreeY) {
		this.kernel = kernel;
		this.variables = variables;
		this.xmin = xmin;
		this.xmax = xmax;
		this.degreeX = degreeX;
		this.degreeY = degreeY;
		this.degree = Math.max(degreeX, degreeY);
		fvX = variables[0];
		fvY = variables[1];
		ExpressionNode[] coeffNodes = getCoeffs(polynomial);
		coeffs = coeffsToBernsteinPolynomials(coeffNodes);
		construct();
	}

	void construct() {
		Log.debug("coeffs of x:\n" + coeffsToString());
		Log.debug("Berstein coeffs:\n");
		createBernsteinCoeffs();
		debugBernsteinCoeffs();
		createBernsteinPolynomial();
		Log.debug("Final output:");
		Log.debug(output.toString(StringTemplate.defaultTemplate));
 	}

	ExpressionNode[] getCoeffs(Polynomial polynomial) {
		ExpressionNode[] coeffs = new ExpressionNode[degree + 1];
		for (int i = 0; i <= degree; i++) {
			coeffs[i] = new MyDouble(kernel, 0).wrap();
		}

		for (int i = 0; i <= degree; i++) {
			Term term = i < polynomial.length() ? polynomial.getTerm(i): null;
			if (term != null) {
				int powerX = term.degree('x');
				int powerY = term.degree('y');
				ExpressionNode coeff = coeffs[powerX];
				if (powerY == 0) {
					coeffs[powerX] = coeff.plus(term.coefficient.wrap());
				} else {
					coeffs[powerX] = coeff.plus(fvY.wrap().power(powerY)
							.multiply(term.coefficient));
				}
			}
		}

		return coeffs;
	}

	private BernsteinPolynomial[] coeffsToBernsteinPolynomials(ExpressionNode[] coeffNodes) {
		BernsteinPolynomial[] polynomials = new BernsteinPolynomial[degree + 1];
		for (int i = 0; i <= degree; i++) {
			polynomials[i] = node2BernsteinPolynomial(coeffNodes[i]);
		}
		return polynomials;
	}

	private BernsteinPolynomial1Var node2BernsteinPolynomial(ExpressionNode node) {
		FunctionNVar functionNVar = new FunctionNVar(node, variables);
		functionNVar.initFunction();
		Polynomial poly = functionNVar.getPolynomial();
		return new BernsteinPolynomial1Var(kernel, poly, fvY, xmin, xmax,
				poly.degree());
	}

	private void createBernsteinCoeffs() {
		bernsteinCoeffs = new ExpressionNode[degree + 1][degree + 1];
		for (int i = 0; i <= degree; i++) {
			for (int j = 0; j <= i; j++) {
				bernsteinCoeffs[i][j] = bernsteinCoefficient(i, j);
			}
		}	}


	@Override
	public String coeffsToString() {
		StringBuilder sb = new StringBuilder();
		String fs = "";
		for (BernsteinPolynomial coeff : coeffs) {
			sb.append(fs);
			fs = ", ";
			sb.append(coeff);
		}
		return sb.toString();
	}

	private void debugBernsteinCoeffs() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= degree; i++) {
			sb.append("(");
			String fs = "";
			for (int j = 0; j <= i; j++) {
				sb.append(fs);
				fs=", ";
				sb.append(bernsteinCoeffs[i][j]);
			}
			sb.append(")\n");
		}
		Log.debug(sb);
	}

	private void createBernsteinPolynomial() {
		for (int i = 0; i <= degree; i++) {
			output = createB(i);
			Log.debug("B_" + i + " = " + output);
		}


		if (output != null) {
			output.simplifyLeafs();
		}
	}

	ExpressionNode createB(int i) {
		output = null;
		for (int j = i; j >= 0; j--) {
			ExpressionNode beta = bernsteinCoeffs[i][j];
			BernsteinBasisPolynomial basis = new BernsteinBasisPolynomial(fvX, i, j
			);
			addToOutput(basis.multiply(beta));
		}
 		return output;
	}

	private void addToOutput(ExpressionNode result) {
		output = output == null ? result : output.plus(result);
	}

	ExpressionNode bernsteinCoefficient(int i, int j) {
		double xl = xmin;
		double xh = xmax;
		if (i == 0 && j == 0) {
			return coeffs[degree].output();
		}

		ExpressionNode a_nMinusI = coeffs[degree - i].output();

		if (j == 0) {
			return a_nMinusI.plus(bernsteinCoeffs[i - 1][0].multiply(xl));
		}

		if (j == i) {
			return a_nMinusI.plus(bernsteinCoeffs[i - 1][i - 1].multiply(xh));
		}

		double binomial = MyMath.binomial(i, j);
		return a_nMinusI.multiply(binomial)
				.plus(bernsteinCoeffs[i - 1][j].multiply(xl))
				.plus(bernsteinCoeffs[i - 1][j - 1].multiply(xh));
	}

	public double evaluate(double value) {
		double y = (value - xmin) / (xmax - xmin);
//		variable.set(y);
		return output.evaluateDouble();
	}

	@Override
	public ExpressionNode output() {
		return output;
	}
}
