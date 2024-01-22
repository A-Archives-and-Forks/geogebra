package org.geogebra.deslktop.gui.util;

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

	static AppCommon app;
	private UndoRedoTester undoRedoTester;

	public static final String sequenceBase64 = Base64.encodeToString(
			UtilD.loadFileIntoByteArray("src/test/resources/sequence-undo.ggb"), false);

	@Before
	public void setUp() {
		app = new AppDNoGui(new LocalizationD(3), false) {

			@Override
			public DrawEquation getDrawEquation() {
				return new DrawEquationStub();
			}

		};

		app.getGgbApi().setBase64(sequenceBase64);
		undoRedoTester = new UndoRedoTester(app);
		undoRedoTester.setupUndoRedo();
	}


	@Test
	public void testUndo() {
		add("(1,1)");
		app.storeUndoInfo();
		undoRedoTester.undo();
	}

}