package com.himamis.retex.editor.share.util;

import com.himamis.retex.editor.share.editor.AddPlaceholders;
import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.TeXSerializer;

public class MathFormulaConverter {
	private final Parser parser;
	private final TeXSerializer texSerializer;
	private final AddPlaceholders placeholders;
	/**
	 * Constructor
	 */
	public MathFormulaConverter() {
		MetaModel model = new MetaModel();
		parser = new Parser(model);
		texSerializer = new TeXSerializer();
		placeholders = new AddPlaceholders();
	}

	/**
	 * Converst from GGB to MathML style latex.
	 * @param text ggb text.
	 * @return MathML styled text
	 */
	public String convert(String text) {
		MathFormula formula = null;
		try {
			formula = buildFormula(text);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
		return texSerializer.serialize(formula);
	}

	/**
	 *
	 * @param text to build a formura from.
	 * @return the built formula.
	 */
	public MathFormula buildFormula(String text) throws ParseException {
		MathFormula formula = parser.parse(text);
		placeholders.process(formula.getRootComponent().getArgument(0));
		return formula;
	}
}
