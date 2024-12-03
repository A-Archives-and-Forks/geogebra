package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.LinearEquationFormDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Equation form property for objects described by linear equations (lines, planes).
 */
public class LinearEquationFormProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private final GeoElementDelegate delegate;

	/***/
	public LinearEquationFormProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Equation");
		delegate = new LinearEquationFormDelegate(element);
		setNamedValues(
				List.of(
						entry(LinearEquationRepresentable.Form.IMPLICIT.rawValue,
								"ImplicitLineEquation"),
						entry(LinearEquationRepresentable.Form.EXPLICIT.rawValue,
								"ExplicitLineEquation"),
						entry(LinearEquationRepresentable.Form.PARAMETRIC.rawValue,
								"ParametricForm"),
						entry(LinearEquationRepresentable.Form.GENERAL.rawValue,
								"GeneralLineEquation"),
						entry(LinearEquationRepresentable.Form.USER.rawValue,
								"InputForm")
				));
	}

	@Override
	protected void doSetValue(Integer value) {
		GeoElement element = delegate.getElement();
		if (element instanceof GeoVec3D) { // TODO what is this?
			GeoVec3D vec3d = (GeoVec3D) element;
			vec3d.setMode(value);
			vec3d.updateRepaint();
		}
	}

	@Override
	public Integer getValue() {
		return delegate.getElement().getToStringMode();
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
