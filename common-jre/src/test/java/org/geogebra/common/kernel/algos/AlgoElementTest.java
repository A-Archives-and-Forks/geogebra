package org.geogebra.common.kernel.algos;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class AlgoElementTest extends BaseUnitTest {

	@Test
	public void latexIntegral() {
		add("f(n)=n^2");
		assertThat(add("Integral(x,1,2)"), hasLaTeXDefinition(
				"\\int\\limits_{1}^{2}x\\,\\mathrm{d}x"));
		assertThat(add("Integral(x)"), hasLaTeXDefinition("\\int x\\,\\mathrm{d}x"));
		assertThat(add("Integral(f,1,2)"), hasLaTeXDefinition(
				"\\int\\limits_{1}^{2}f\\,\\mathrm{d}n"));
		assertThat(add("Integral(f)"), hasLaTeXDefinition("\\int f\\,\\mathrm{d}n"));
		assertThat(add("Sequence(Integral(x^k),k,1,2)"), hasLaTeXDefinition(
				"Sequence\\left(\\int x^{k}\\,\\mathrm{d}x, k, 1, 2 \\right)"));
	}

	@Test
	public void latexIntegralShouldHaveCorrectDerivativeVariable() {
		getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		assertThat(add("Integral(x-d,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}x - d\\,\\mathrm{d}x"));
		assertThat(add("Integral(t-d,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}t - d\\,\\mathrm{d}d"));
		assertThat(add("Integral(s-d,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}s - d\\,\\mathrm{d}d"));
		assertThat(add("Integral(s-r,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}s - r\\,\\mathrm{d}r"));
		assertThat(add("Integral(t-x,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}t - x\\,\\mathrm{d}x"));
	}

	@Test
	public void testSequenceInequality() {
		add("a: y<=x^(2) && y>= 0 && x>=0 && x<=1");
		GeoList list = add("{a(x+1,y), a(x+2,y)}");
		GeoList seq = add("l1=Sequence(a(x+i,y),i,1,2)");
		GeoList elements = add("{Element[l1,1], Element[l1,2]}");
		String listValue = list.toValueString(StringTemplate.defaultTemplate);
		assertThat(seq, hasValue(listValue));
		assertThat(elements, hasValue(listValue));
	}

	@Test
	public void testSequenceIneqTree() {
		add("a: y<=x^(2) && y>= 0 && x>=0 && x<=1");
		GeoList sequence = add("Sequence(a(x+i,y),i,1,2)");
		IneqTree inequalities1 = ((GeoFunctionNVar) sequence.get(0)).getIneqs();
		IneqTree inequalities2 = ((GeoFunctionNVar) sequence.get(1)).getIneqs();
		assertThat(inequalities1.getLeft().getLeft().getLeft().getIneq().getBorder(),
				hasValue(unicode("((x + 1)^2) / 1")));
		assertThat(inequalities2.getLeft().getLeft().getLeft().getIneq().getBorder(),
				hasValue(unicode("((x + 2)^2) / 1")));
	}

	private TypeSafeMatcher<GeoElement> hasLaTeXDefinition(String def) {
		return hasProperty("definition", item ->
				item.getDefinition(StringTemplate.latexTemplate), def);
	}
}
