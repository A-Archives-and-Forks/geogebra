package org.geogebra.web.full.main.activity;

import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

public class ScientificEvaluatables {
	private GeoEvaluatable functionF;
	private GeoEvaluatable functionG;


	public ScientificEvaluatables(Construction construction) {
		functionF = createFunction(construction, "f");
		functionG = createFunction(construction, "g");
	}

	private GeoEvaluatable createFunction(Construction construction, String label) {
		GeoFunction function = new GeoFunction(construction);
		function.rename(label);
		function.setAuxiliaryObject(true);
		return function;
	}

	/**
	 * Update the two evaluatables.
	 *
	 * @param functionF aka f(x).
	 * @param functionG aka g(x).
	 */
	public void update(GeoEvaluatable functionF, GeoEvaluatable functionG) {
		this.functionF = functionF;
		this.functionG = functionG;
	}

	/**
	 * Add evaluatables to the table of values.
	 *
	 * @param values
	 */
	public void addToTV(TableValues values) {
		values.addAndShow((GeoElement) functionF);
		values.addAndShow((GeoElement) functionG);
		try {
			values.setValues(-2, 2, 1);
		} catch (InvalidValuesException e) {
			throw new RuntimeException(e);
		}
	}
}
