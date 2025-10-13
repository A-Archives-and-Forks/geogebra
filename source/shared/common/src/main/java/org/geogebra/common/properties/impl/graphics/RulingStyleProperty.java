package org.geogebra.common.properties.impl.graphics;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * This property controls the style of the grid.
 */
public class RulingStyleProperty extends AbstractNamedEnumeratedProperty<BackgroundType>
		implements SettingsDependentProperty {
	private EuclidianSettings euclidianSettings;
	private EuclidianView euclidianView;

	/**
	 * Controls a grid style property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings.
	 * @param euclidianView euclidian view
	 */
	public RulingStyleProperty(Localization localization, EuclidianSettings euclidianSettings,
			EuclidianView euclidianView) {
		super(localization, "Ruling");
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;

		setNamedValues(List.of(
				entry(BackgroundType.NONE, "NoRuling"),
				entry(BackgroundType.RULER, "Ruled"),
				entry(BackgroundType.SQUARE_SMALL, "Squared5"),
				entry(BackgroundType.SQUARE_BIG, "Squared1"),
				entry(BackgroundType.ELEMENTARY12_HOUSE, "Elementary12WithHouse"),
				entry(BackgroundType.ELEMENTARY12_COLORED, "Elementary12Colored"),
				entry(BackgroundType.ELEMENTARY12, "Elementary12"),
				entry(BackgroundType.ELEMENTARY34, "Elementary34"),
				entry(BackgroundType.MUSIC, "Music"),
				entry(BackgroundType.ISOMETRIC, "Isometric"),
				entry(BackgroundType.POLAR, "Polar"),
				entry(BackgroundType.DOTS, "Dots")));
	}

	@Override
	public BackgroundType getValue() {
		return euclidianSettings.getBackgroundType();
	}

	@Override
	protected void doSetValue(BackgroundType value) {
		euclidianSettings.setBackgroundType(value);
		euclidianView.updateBackground();
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
