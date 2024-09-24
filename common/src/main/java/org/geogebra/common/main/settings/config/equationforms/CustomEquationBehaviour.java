package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationBehaviour;

public class CustomEquationBehaviour implements EquationBehaviour {
	private int defaultLineEquationForm;
	private int linearAlgebraInputEquationForm;
	private int conicAlgebraInputEquationForm;
	private int lineCommandEquationForm;
	private int rayCommandEquationForm;
	private int fitLineCommandEquationForm;

	public CustomEquationBehaviour(int defaultLineEquationForm,
			int linearAlgebraInputEquationForm,
			int conicAlgebraInputEquationForm,
			int lineCommandEquationForm,
			int rayCommandEquationForm,
			int fitLineCommandEquationForm) {
		this.defaultLineEquationForm = defaultLineEquationForm;
		this.linearAlgebraInputEquationForm = linearAlgebraInputEquationForm;
		this.conicAlgebraInputEquationForm = conicAlgebraInputEquationForm;
		this.lineCommandEquationForm = lineCommandEquationForm;
		this.rayCommandEquationForm = rayCommandEquationForm;
		this.fitLineCommandEquationForm = fitLineCommandEquationForm;
	}

	public CustomEquationBehaviour(
			EquationBehaviour baseBehaviour,
			int defaultLineEquationFormOverride,
			int linearAlgebraInputEquationFormOverride,
			int conicAlgebraInputEquationFormOverride,
			int lineCommandEquationFormOverride,
			int rayCommandEquationFormOverride,
			int fitLineCommandEquationFormOverride) {
		this.defaultLineEquationForm = overrideForm(
				baseBehaviour.getDefaultLineEquationForm(),
				defaultLineEquationFormOverride);
		this.linearAlgebraInputEquationForm = overrideForm(
				baseBehaviour.getLinearAlgebraInputEquationForm(),
				linearAlgebraInputEquationFormOverride);
		this.conicAlgebraInputEquationForm = overrideForm(
				baseBehaviour.getConicAlgebraInputEquationForm(),
				conicAlgebraInputEquationFormOverride);
		this.lineCommandEquationForm = overrideForm(
				baseBehaviour.getLineCommandEquationForm(),
				lineCommandEquationFormOverride);
		this.rayCommandEquationForm = overrideForm(
				baseBehaviour.getRayCommandEquationForm(),
				rayCommandEquationFormOverride);
		this.fitLineCommandEquationForm = overrideForm(
				baseBehaviour.getFitLineCommandEquationForm(),
				fitLineCommandEquationFormOverride);
	}

	private int overrideForm(int base, int override) {
		return override != -1 ? override : base;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return true;
	}

	@Override
	public int getDefaultLineEquationForm() {
		return defaultLineEquationForm;
	}

	@Override
	public int getLinearAlgebraInputEquationForm() {
		return linearAlgebraInputEquationForm;
	}

	@Override
	public int getConicAlgebraInputEquationForm() {
		return conicAlgebraInputEquationForm;
	}

	@Override
	public int getLineCommandEquationForm() {
		return lineCommandEquationForm;
	}

	@Override
	public int getRayCommandEquationForm() {
		return rayCommandEquationForm;
	}

	@Override
	public int getFitLineCommandEquationForm() {
		return fitLineCommandEquationForm;
	}
}
