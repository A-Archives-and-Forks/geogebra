package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.impl.DefaultKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.CharacterProvider;
import org.geogebra.keyboard.base.model.impl.factory.DefaultCharProvider;
import org.geogebra.keyboard.web.factory.model.solver.SolverDefaultKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.solver.SolverFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.solver.SolverSpecialSymbolsKeyboardFactory;

public final class SolverKeyboardFactory extends DefaultKeyboardFactory {

	public static final SolverKeyboardFactory INSTANCE = new SolverKeyboardFactory();

	/**
	 * Keyboard layout for solver
	 */
	protected SolverKeyboardFactory() {
		this(new DefaultCharProvider());
	}

	/**
	 * Keyboard layout for solver
	 * @param charProvider - character provider
	 */
	public SolverKeyboardFactory(CharacterProvider charProvider) {
		super();
		defaultKeyboardModelFactory = new SolverDefaultKeyboardFactory(charProvider);
		mathKeyboardFactory = new SolverDefaultKeyboardFactory(charProvider);
		functionKeyboardFactory = new SolverFunctionKeyboardFactory();
		specialSymbolsKeyboardFactory = new SolverSpecialSymbolsKeyboardFactory();
	}
}
