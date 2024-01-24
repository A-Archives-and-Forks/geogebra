package org.geogebra.deslktop.gui.util;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.DrawEquationStub;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;
import org.geogebra.test.UndoRedoTester;
import org.junit.Before;
import org.junit.Test;

public class SequenceLoadTest extends BaseUnitTest {

	public static final String M2 =
			"{{2, 0, 0, 0}, {2, 1, 0, 0}, {2, 0, 0, 0}, {2, 1, 0, 0}, {8, 1, 0, 0}, {8, 4, 1, 0},"
					+ " {8, 4, 0, 0}}";
	static AppCommon app;
	private UndoRedoTester undoRedoTester;

	public static final String sequenceBase64 = Base64.encodeToString(
			UtilD.loadFileIntoByteArray("src/test/resources/sequence-undo.ggb"), false);

	@Override
	public AppCommon createAppCommon() {
		return new AppDNoGui(new LocalizationD(3), false) {

			@Override
			public DrawEquation getDrawEquation() {
				return new DrawEquationStub();
			}

		};
	}

	@Before
	public void setUp() {
		app = getApp();
		app.enableCAS(false);
		app.getGgbApi().setBase64(sequenceBase64);
		undoRedoTester = new UndoRedoTester(app);
		undoRedoTester.setupUndoRedo();
	}

	@Test
	public void testMatrixIsLoaded() {
		assertThat(app.getKernel().lookupLabel("m2"), hasValue(M2));
	}

	@Test
	public void testMatrixFromCommandAfterLoad() {
		add("m4 = Sequence(Sequence(Element(m1, lig, col) - Element(m1, lig, col + 1), col, 1, dimjeu2),"
				+ " lig, 1, Dimension(jeunonnul))");
		assertThat(app.getKernel().lookupLabel("m4"), hasValue(M2));
	}

	@Test
	public void testUndo() {
		add("(1,1)");
		app.storeUndoInfo();
		undoRedoTester.undo();
	}

}