package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.SolverSerializer;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class SolverSerializationTest {

	/**
	 * Reset LaTeX factory
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	private static MathFormula parseForEditor(String input) {
		try {
			Parser parser = new Parser(new MetaModel());
			return parser.parse(input);
		} catch (ParseException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void parsesToSolverInput(String input, String serialized) {
		MathFormula formula = parseForEditor(input);
		SolverSerializer serializer = new SolverSerializer();
		String result = serializer.serialize(formula);
		assertEquals(serialized, result);
	}

	@Test
	public void solverInputs() {
		parsesToSolverInput("223^3", "[223^3]");
		parsesToSolverInput("(4/8)", "[4/8]");
		parsesToSolverInput("(4.5/8)", "[4.5/8]");
		parsesToSolverInput("log(4)", "log[10,4]");
		parsesToSolverInput("log(8,4)", "log[8,4]");
		parsesToSolverInput("nroot(16,4)", "root[16,4]");
		parsesToSolverInput("sqrt(2)", "sqrt[2]");
		parsesToSolverInput("|x|", "abs[x]");
		parsesToSolverInput("cbrt(4)", "root[4,3]");
		parsesToSolverInput("2 + 22243^3", "2 + [22243^3]");
		parsesToSolverInput("22^33", "[22^33]");
		parsesToSolverInput("sin(8)", "sin[8]");
		parsesToSolverInput("ln(8)", "ln[8]");
		parsesToSolverInput("(4 / 5)", "[4/5]");
		parsesToSolverInput("(1 +2/3 + sin(5))", "(1 +[2/3] + sin[5])");
		parsesToSolverInput("1+2-3*4/5 + sqrt(16) + 234^3",
				"1+2-3*[4/5] + sqrt[16] + [234^3]");
		parsesToSolverInput("(1+2-3*4/5) + sqrt(16) + 234^3",
				"(1+2-3*[4/5]) + sqrt[16] + [234^3]");
	}

	@Test
	public void testExpr() {
		parsesToSolverInput("1 * 2", "1 * 2");
		parsesToSolverInput("1 == 2", "1 == 2");
		parsesToSolverInput("1 " + Unicode.PARALLEL + " 2",
				"1 " + Unicode.PARALLEL + " 2");
		parsesToSolverInput("1 = 2", "1 = 2");
		parsesToSolverInput("[1 * 2]", "[.1 * 2.]");
		parsesToSolverInput("(1 * 2)", "(1 * 2)");
	}

	@Test
	public void testSqrt() {
		parsesToSolverInput("sqrt(x/(2+x))1^3", "sqrt[[x/2+x]][1^3]");
		parsesToSolverInput("sqrt(x/(2+x))  1^3", "sqrt[[x/2+x]]  [1^3]");
		parsesToSolverInput("sqrt(x/(2+x))+1^3", "sqrt[[x/2+x]]+[1^3]");
		parsesToSolverInput("sqrt(x + 1)", "sqrt[x + 1]");
		parsesToSolverInput("x sqrt(x + 1)", "x sqrt[x + 1]");
		parsesToSolverInput("nroot(x - 1,3)", "root[x - 1,3]");
	}

	@Test
	public void testBrackets() {
		parsesToSolverInput("{1+2}^2+(1+2)^2+[1+2]^2", "[{.1+2.}^2]+[(1+2)^2]+[[.1+2.]^2]");
	}

	@Test
	public void testMixedNumbers() {
		parsesToSolverInput("1/2 + 3\u2064(1)/(2)", "[1/2] + [3 1/2]");
		parsesToSolverInput("sqrt(x/(2+x))1\u2064(2)/(3)", "sqrt[[x/2+x]][1 2/3]");
		parsesToSolverInput("sqrt(x/(2+x))*1\u2064(2)/(3)", "sqrt[[x/2+x]]*[1 2/3]");
	}

	@Test
	public void testDiv() {
		parsesToSolverInput("1/n^2", "[1/[n^2]]");
		parsesToSolverInput("1/2", "[1/2]");
		parsesToSolverInput("1/2+3", "[1/2]+3");
		parsesToSolverInput("1/2" + Unicode.SUPERSCRIPT_3, "[1/[2^3]]");
		parsesToSolverInput("1/[2/3]", "[1/[.[2/3].]]");
		parsesToSolverInput("2/cos( x )", "[2/cos[ x ]]");
		parsesToSolverInput("x^2/ 3", "[[x^2]/3]");
		parsesToSolverInput("1/(2^3)", "[1/[2^3]]");
		parsesToSolverInput("1/2^3", "[1/[2^3]]");
		parsesToSolverInput("1/(2+3)", "[1/2+3]");
		parsesToSolverInput("1/ ((2+3)+4)", "[1/(2+3)+4]");
	}

	@Test
	public void testPower() {
		parsesToSolverInput("[x ^ 2] ^3", "[[.[x ^2].] ^3]");
		parsesToSolverInput("x ^ 2", "[x ^2]");
		parsesToSolverInput("x^2^3", "[x^[2^3]]");
		parsesToSolverInput("x ^ 2 + 1", "[x ^2] + 1");
		parsesToSolverInput("x" + Unicode.SUPERSCRIPT_2 + Unicode.SUPERSCRIPT_3,
				"[x^23]");
		parsesToSolverInput("x" + Unicode.SUPERSCRIPT_MINUS + Unicode.SUPERSCRIPT_2
				+ Unicode.SUPERSCRIPT_3, "[x^-23]");
		parsesToSolverInput("1 + x" + Unicode.SUPERSCRIPT_MINUS + Unicode.SUPERSCRIPT_2
				+ Unicode.SUPERSCRIPT_3, "1 + [x^-23]");
		parsesToSolverInput("e^x*sin(x)", "[e^x]*sin[x]");
		parsesToSolverInput("e^(-10/x)*sin(x)", "[e^-[10/x]]*sin[x]");
	}

	@Test
	public void testMultiply() {
		parsesToSolverInput("t (1,2)", "t (1,2)");
		parsesToSolverInput("t [1,2]", "t [.1,2.]");
		parsesToSolverInput("x x x", "x x x");
	}

	@Test
	public void testLog() {
		parsesToSolverInput("log(10,x)", "log[10,x]");
		parsesToSolverInput("log(x)", "log[10,x]");
	}

	@Test
	public void testParse() {
		parsesToSolverInput("[x^2]/(m^2)+y^2/n^2", "[[.[x^2].]/[m^2]]+[[y^2]/[n^2]]");
		parsesToSolverInput("4+x", "4+x");
		parsesToSolverInput("4-x", "4-x");
		parsesToSolverInput("4/x", "[4/x]");
		parsesToSolverInput("4*x", "4*x");

		parsesToSolverInput("(x+y)/(x-y)", "[x+y/x-y]");
		parsesToSolverInput("sqrt(x+y)", "sqrt[x+y]");
		parsesToSolverInput("sqrt(x)+2", "sqrt[x]+2");
	}

	@Test
	public void latexParserUnitTestsSumsAndProducts() {
		parsesToSolverInput("1+2.3+3", "1+2.3+3");
		parsesToSolverInput("1+2*3", "1+2*3");
		parsesToSolverInput("1+2×3", "1+2*3"); // '1+2\\cdot3',
		parsesToSolverInput("1-2×3", "1-2*3"); //  '1-2 \\times 3
		parsesToSolverInput("1-2÷3", "1-2:3"); //  '1-2 \\div 3
	}

	@Test
	public void latexParserUnitTestsVariablesAndNumbers() {
		parsesToSolverInput("a+b×C", "a+b*C"); //a+b \\times C
		parsesToSolverInput("aaa", "aaa"); //isn't serialized to a a a
		parsesToSolverInput("2a2", "2a2"); //isn't serialized to 2 a 2
		//parsesToSolverInput("2.1Segment 33", "2.1[33]");
	}

	@Test
	public void latexParserUnitTestsBrackets() {
		parsesToSolverInput("(a+b)×c", "(a+b)*c"); // '\\left(a+b\\right) \\times c',
		parsesToSolverInput("((a+b))", "((a+b))"); //((a+b))
		//\\left\\{\\left[\\left(x\\right)\\right]\\right\\}
		parsesToSolverInput("{[(x)]}", "{.[.(x).].}");
		parsesToSolverInput("2 (a)", "2 (a)"); //2 (a)
	}

	@Test
	public void latexParserUnitTestsFractionsAndMixedNumbers() {
		parsesToSolverInput("222\u2064(1)/(2)", "[222 1/2]");
		parsesToSolverInput("2\u2064(1)/(2)", "[2 1/2]");
		parsesToSolverInput("(1)/(2)", "[1/2]"); //\\frac{1}{2}
		parsesToSolverInput("(a)/(b)", "[a/b]"); //frac ab
		parsesToSolverInput("2+(1)/(2)", "2+[1/2]"); //2+\\frac{1}{2}
		parsesToSolverInput("((a)/(b))/(c)", "[[a/b]/c]"); //\\frac{\\frac{a}{b}}{c}
	}

	@Test
	public void latexParserUnitTestsPowers() {
		parsesToSolverInput("-3^4", "-[3^4]"); //-3^4'
		parsesToSolverInput("(-3)^-4", "[(-3)^-4]"); //(-3)^{-4}'
		parsesToSolverInput("3^4^5", "[3^[4^5]]"); //3^4^5'
		parsesToSolverInput("3^((1)/(2))", "[3^[1/2]]"); //3^\\frac{1}{2}'
		parsesToSolverInput("2+3*4^5*6+7", "2+3*[4^5]*6+7"); //2+3*4^5*6+7'
	}

	@Test
	public void latexParserUnitTestsRoots() {
		parsesToSolverInput("sqrt(4)", "sqrt[4]"); // \\sqrt 4
		parsesToSolverInput("2nroot(4,3)", "2root[4,3]"); // 2 \\sqrt[3] 4
		parsesToSolverInput("2^(nroot(1+2,3))", "[2^root[1+2,3]]"); // 2^\\sqrt[3]{1+2}
	}
}
