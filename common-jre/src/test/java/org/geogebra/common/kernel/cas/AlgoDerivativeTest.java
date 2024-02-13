package org.geogebra.common.kernel.cas;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Test;

public class   AlgoDerivativeTest extends BaseUnitTest {

	@Test
	public void nDerivativeShouldNotMaskOtherExpressions() {
		t("fm(x,y,z)=x*y*z", "x * y * z");
		t("NDerivative[fm,z]", unicode("NDerivative(fm, z)"));
		t("fm", "x * y * z");
	}

	@Test
	public void fastDerivativeDirac() {
		t("f(x)=Dirac(x)", "Dirac(x)");
		t("g(x)=Heaviside(x)", "Heaviside(x)");
		AlgoDerivative der = getFastDerivative(lookup("f"));
		assertThat(der.getResult(), hasValue("Dirac(x)"));
		der = getFastDerivative(lookup("g"));
		assertThat(der.getResult(), hasValue("Dirac(x)"));
	}

	private AlgoDerivative getFastDerivative(GeoElement function) {
		EvalInfo info = new EvalInfo().withCAS(false);
		return new AlgoDerivative(getConstruction(),
				(GeoFunction) function, info);
	}

	@Test
	public void fastDerivativeLogBase() {
		add("b=3");
		AlgoDerivative logDerivative = getFastDerivative(add("log(b,x)"));
		assertThat(logDerivative.getResult(), hasValue("1 / x / ln(3)"));
		add("SetValue(b,4)");
		assertThat(logDerivative.getResult(), hasValue("1 / x / ln(4)"));
		AlgoDerivative logDerivativeConst = getFastDerivative(add("log(e^5,x)"));
		assertThat(logDerivativeConst.getResult(), hasValue("1 / x / 5"));
	}

	private void t(String s, String s1) {
		AlgebraTestHelper.checkSyntaxSingle(s, new String[]{s1},
				getApp().getKernel().getAlgebraProcessor(), StringTemplate.testTemplate);
	}
}
