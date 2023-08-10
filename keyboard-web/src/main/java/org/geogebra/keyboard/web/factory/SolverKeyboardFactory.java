package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.CharacterProvider;
import org.geogebra.keyboard.base.model.impl.factory.DefaultCharProvider;
import org.geogebra.keyboard.scientific.model.ScientificDefaultKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificFunctionKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificLettersKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.MowSpecialSymbolsKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.solver.SolverDefaultKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.solver.SolverFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.solver.SolverSpecialSymbolsKeyboardFactory;

public final class SolverKeyboardFactory extends KeyboardFactory {

	public static final SolverKeyboardFactory INSTANCE = new SolverKeyboardFactory();

	/**
	 * Keyboard layout for solver
	 */
	protected SolverKeyboardFactory() {
		this(new DefaultCharProvider());
	}

	public SolverKeyboardFactory(CharacterProvider charProvider) {
		super();
		setDefaultKeyboardFactory(new SolverDefaultKeyboardFactory(charProvider));
		setMathKeyboardFactory(new SolverDefaultKeyboardFactory(charProvider));
		setFunctionKeyboardFactory(new SolverFunctionKeyboardFactory());
		setSpecialSymbolsKeyboardFactory(new SolverSpecialSymbolsKeyboardFactory());
	}
}
