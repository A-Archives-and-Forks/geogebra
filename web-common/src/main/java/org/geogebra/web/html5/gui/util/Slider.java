package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.StringUtil;

public class Slider extends SliderAbstract<Integer> {

	/**
	 * Create a new slider.
	 * 
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 */
	public Slider(int min, int max) {
		super(min, max);
	}

	@Override
	protected Integer convert(String val) {
		return StringUtil.empty(val) ? 0 : (int) Double.parseDouble(val); // empty string happens in Mockito
	}

}
