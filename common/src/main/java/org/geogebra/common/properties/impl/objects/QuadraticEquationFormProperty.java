package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.QuadraticEquationFormDelegate;

public class QuadraticEquationFormProperty extends AbstractNamedEnumeratedProperty<Integer> {

		private final GeoElementDelegate delegate;

		/***/
		public QuadraticEquationFormProperty(Localization localization, GeoElement element)
				throws NotApplicablePropertyException {
			super(localization, "Equation");
			delegate = new QuadraticEquationFormDelegate(element);
			setNamedValues(
					List.of(
							entry(QuadraticEquationRepresentable.Form.IMPLICIT.rawValue,
									"ImplicitLineEquation"),
							entry(QuadraticEquationRepresentable.Form.EXPLICIT.rawValue,
									"ExplicitLineEquation"),
							entry(QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue,
									"ParametricForm"),
							entry(QuadraticEquationRepresentable.Form.GENERAL.rawValue,
									"GeneralLineEquation"),
							entry(QuadraticEquationRepresentable.Form.USER.rawValue,
									"InputForm")
					));
		}

		@Override
		protected void doSetValue(Integer value) {
			GeoElement element = delegate.getElement();
			if (element instanceof GeoVec3D) {
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
