package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionableProperty;

public abstract class AbstractActionableProperty extends AbstractProperty implements ActionableProperty {

	private boolean isFrozen = false;

	public AbstractActionableProperty(Localization localization, String name) {
		super(localization, name);
	}

	@Override
	public void performAction() {
		if (isFrozen) {
			return;
		}
		doPerformAction();
	}

	protected abstract void doPerformAction();

	@Override
	public void freeze() {
		isFrozen = true;
	}

	@Override
	public void unfreeze() {
		isFrozen = false;
	}
}
