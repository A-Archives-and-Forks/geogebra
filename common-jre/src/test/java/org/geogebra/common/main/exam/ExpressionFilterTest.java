package org.geogebra.common.main.exam;

import static org.junit.Assert.assertFalse;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilterFactory;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class ExpressionFilterTest extends BaseUnitTest {
	@Test
	public void testMmsExpressionFilter() {
		ExpressionFilter filter = ExpressionFilterFactory.createMmsExpressionFilter();
		GeoFunction function = add("x + fractionalPart[5/2]");
		assertFalse(filter.isAllowed(function.getFunctionExpression()));
	}
}
