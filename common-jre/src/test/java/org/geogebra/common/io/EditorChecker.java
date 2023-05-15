package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.common.util.debug.Log;
import org.junit.Assert;

import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.serializer.TeXBuilder;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.MathFormulaConverter;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.ResizeAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SymbolAtom;

class EditorChecker {
	private final MathFieldCommon mathField;
	private final EditorTyper typer;
	private final App app;

	protected EditorChecker(App app) {
		this(app, new MetaModel());
	}

	protected EditorChecker(App app, MetaModel model) {
		this.app = app;
		mathField = new MathFieldCommon(model, null);
		typer = new EditorTyper(mathField);
	}

	public void checkAsciiMath(String output) {
		MathSequence rootComponent = getRootComponent();
		assertEquals(output,
				GeoGebraSerializer.serialize(rootComponent));
		// clean the checker after typing
		fromParser("");
	}

	public void checkGGBMath(String output) {
		MathSequence rootComponent = getRootComponent();

		String exp = GeoGebraSerializer.serialize(rootComponent);

		try {
			ExpressionNode en = parse(exp);
			assertEquals(output, en.toString(StringTemplate.defaultTemplate));
		} catch (ParseException e) {
			Log.debug(e);
			assertEquals(output, "Exception: " + e);
		}

	}

	public EditorChecker checkPlaceholders(String expected) {
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		mathFieldInternal.update();
		EditorState editorState = mathFieldInternal.getEditorState();
		MathSequence currentField = editorState.getCurrentField();
		TeXBuilder builder = new TeXBuilder();
		RowAtom atom =
				(RowAtom) builder.build(currentField, currentField, editorState.getCurrentOffset(),
						false);
		assertEquals(expected, serializeRow(atom));
		return this;
	}

	private String serializeRow(RowAtom row) {
		StringBuilder sb = new StringBuilder();
		for (Atom atom: row.getElements()) {
			sb.append(serializeAtom(atom));
		}
		return sb.toString();
	}

	private String serializeAtom(Atom atom) {
		if (atom instanceof PhantomAtom) {
			return "|";
		}

		if (atom instanceof ColorAtom) {
			return "_";
		}

		if (atom instanceof CharAtom) {
			return ((CharAtom) atom).getCharacter() + "";
		}

		if (atom instanceof SymbolAtom) {
			return ((SymbolAtom) atom).getUnicode() + "";
		}

		if (atom instanceof ResizeAtom) {
			return serializeAtom(((ResizeAtom) atom).getTrueBase());
		}
		return null;
	}

	public void checkRaw(String output) {
		MathSequence rootComponent = getRootComponent();
		assertEquals(output, rootComponent + "");
	}

	public void checkLength(int length) {
		assertEquals(length, getRootComponent().size());
	}

	private MathSequence getRootComponent() {
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		EditorState editorState = mathFieldInternal.getEditorState();
		return editorState.getRootComponent();
	}

	/**
	 * Types input to the checker.
 	 * Example ('|' is the cursor):
	 *  - content before: "xyz|"
	 *  - type("ABC");
	 *  - content after : "xyzABC|"
	 *
 	 * @param input to type.
	 * @return the modified checker.
	 */
	public EditorChecker type(String input) {
		typer.type(input);
		return this;
	}

	public EditorChecker typeKey(int key) {
		typer.typeKey(key);
		return this;
	}

	/**
	 * Moves the cursor left to given times.
	 * Example ('|' is the cursor):
	 *  - content before: "xyz|"
	 *  - left("2");
	 *  - content after : "x|yz"
	 *
	 * @param count of the cursor moves
	 * @return the modified checker.
	 */
	public EditorChecker left(int count) {
		return repeatKey(JavaKeyCodes.VK_LEFT, count);
	}

	/**
	 * Moves the cursor right to given times.
	 * Example ('|' is the cursor):
	 *  - content before: "|xyz"
	 *  - right("2");
	 *  - content after : "xy|z"
	 *
	 * @param count of the cursor moves
	 * @return the modified checker.
	 */
	public EditorChecker right(int count) {
		return repeatKey(JavaKeyCodes.VK_RIGHT, count);
	}

	public EditorChecker setModifiers(int modifiers) {
		typer.setModifiers(modifiers);
		return this;
	}

	public EditorChecker repeatKey(int key, int count) {
		typer.repeatKey(key, count);
		return this;
	}

	public EditorChecker insert(String input) {
		typer.insert(input);
		return this;
	}

	public EditorChecker fromParser(String input) {
		Parser parser = new Parser(mathField.getMetaModel());
		MathFormula formula;
		try {
			formula = parser.parse(input);
			mathField.getInternal().setFormula(formula);
		} catch (Exception e) {
			Assert.fail("Problem parsing: " + input);
		}
		return this;
	}

	public EditorChecker convertFormula(String input) {
		try {
			MathFormulaConverter converter =
					new MathFormulaConverter(mathField.getMetaModel());
			mathField.getInternal().setFormula(converter.buildFormula(input));
			mathField.getInternal().getFormula().getRootComponent().setProtected();
			mathField.getInternal().setLockedCaretPath();
		} catch (com.himamis.retex.editor.share.io.latex.ParseException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public EditorChecker matrixFromParser(String input) {
		Parser parser = new Parser(mathField.getMetaModel());
		MathFormula formula;
		try {
			formula = parser.parse(input);
			mathField.getInternal().setFormula(formula);
			mathField.getInternal().getFormula().getRootComponent().setProtected();
			mathField.getInternal().setLockedCaretPath();
		} catch (Exception e) {
			Assert.fail("Problem parsing: " + input);
		}
		return this;
	}

	/**
	 * Protect top level sequence
	 * @return this
	 */
	public EditorChecker protect() {
		mathField.getInternal().getFormula().getRootComponent().setProtected();
		return this;
	}

	public EditorChecker checkPath(Integer... indexes) {
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		mathField.requestViewFocus();
		mathFieldInternal.update();
		ArrayList<Integer> actual = CursorController.getPath(mathFieldInternal
				.getEditorState());
		Assert.assertArrayEquals(indexes, actual.toArray());
		return this;
	}

	public void serializeAs(String latex) {
		TeXSerializer teXSerializer = new TeXSerializer();
		assertEquals(latex,
				teXSerializer.serialize(mathField.getInternal().getFormula()));
	}

	protected void checkEditorInsert(String input, String output) {
		new EditorChecker(app).insert(input).checkAsciiMath(output);
	}

	public ExpressionNode parse(String exp) throws ParseException {
		return app.getKernel().getParser().parseExpression(exp);
	}

	public void setFormatConverter(SyntaxAdapterImpl formatConverter) {
		mathField.setFormatConverter(formatConverter);
	}

	public void setForceBracketsAfterFunction() {
		mathField.getMetaModel().setForceBracketAfterFunction(true);
	}

	public void setAllowAbs(boolean allowAbs) {
		mathField.getInternal().setAllowAbs(allowAbs);
	}

	public EditorChecker click(int x, int y) {
		mathField.getInternal().onPointerUp(x, y);
		return this;
	}

	/**
	 * Asserts if the cursor is in super- or subscript.
	 * Examples ('|' is the cursor):
	 *  xy^z|, xy_|z, xy^(1/x|)
	 */
	public void cursorInScript() {
		assertTrue(mathField.getInternal().getEditorState().isInScript());
	}

	/**
	 * Asserts if the cursor is NOT in super- or subscript.
	 * Note: opposite of cursorInScript().
	 */
 	public void cursorInNoScript() {
		assertFalse(mathField.getInternal().getEditorState().isInScript());
	}
}