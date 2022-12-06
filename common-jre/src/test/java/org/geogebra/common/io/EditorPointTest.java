package org.geogebra.common.io;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorPointTest {
	private static final String point3D = "(1,2,3)";
	private static final String emptyPoint3D = "(,,)";
	private static EditorChecker checker;
	private static AppCommon app = AppCommonFactory.create();

	/**
	 * Reset LaTeX factory
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Before
	public void setUp() {
		checker = new EditorChecker(app);
	}

	@Test
	public void testInitialEmptyPoint() {
		checker.convertFormula(emptyPoint3D)
				.checkPlaceholders("|,_,_");
	}

	@Test
	public void testEmptyPointWithCursorInTheMiddle() {
		checker.convertFormula(emptyPoint3D)
				.right(1)
				.checkPlaceholders("_,|,_");
	}

	@Test
	public void testEmptyPointWithCursorLast() {
		checker.convertFormula(emptyPoint3D)
				.right(2)
				.checkPlaceholders("_,_,|");
	}

	@Test
	public void testPointOnDelete() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.checkPlaceholders("|,2,3");
	}


	@Test
	public void testPointOnDeleteAnRight() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("_,2,3");
	}

	@Test
	public void testPointOnDeleteAnRightDeleteAgain() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.checkPlaceholders("_,|,3");
	}
	@Test
	public void testPointOnDeleteAnRightDeleteAgainAndBack() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("|,_,3");
	}

	@Test
	public void testDeleteFromMultiChars2D() {
		checker.convertFormula("(123,789)")
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("_,789");

	}

	@Test
	public void testDeleteFromMultiCharsFromEnd2D() {
		checker.convertFormula("(123,789)")
				.right(4)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("123,_");
	}

	@Test
	public void testDeleteFromMultiCharsFromMiddle3D() {
		checker.convertFormula("(123,456,789)")
				.right(4)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("123,_,789");
	}

	@Test
	public void testDeleteFromMultiChars() {
		checker.convertFormula("(123,456,789)")
				.right(2)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("12,456,789");
	}

	@Test
	public void testDeleteFromMultiCharsFromBeginning() {
		checker.convertFormula("(123,456,789)")
				.typeKey(JavaKeyCodes.VK_DELETE)
				.checkPlaceholders("23,456,789");

	}
}
