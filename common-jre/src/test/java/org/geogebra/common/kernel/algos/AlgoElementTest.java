package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
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

	private TypeSafeMatcher<GeoElement> hasLaTeXDefinition(String def) {
		return hasProperty("definition", item ->
				item.getDefinition(StringTemplate.latexTemplate), def);
	}
}
