package org.geogebra.common.kernel.implicit;

import java.util.HashMap;
import java.util.Map;

public enum EdgeConfig {
	/**
	 * All corners are inside / outside
	 */
	T0000(0),

	/**
	 * only bottom left corner is inside / outside
	 */
	T0001(1),

	/**
	 * bottom right corner is inside / outside
	 */
	T0010(2),

	/**
	 * both corners at the bottom are inside / outside
	 */
	T0011(3),

	/**
	 * top left corner is inside / outside
	 */
	T0100(4),

	/**
	 * opposite corners are inside / outside. NOTE: This configuration is
	 * regarded as invalid
	 */
	T0101(5),

	/**
	 * both the corners at the left are inside / outside
	 */
	T0110(6),

	/**
	 * only top left corner is inside / outside
	 */
	T0111(7),

	/**
	 * invalid configuration. expression value is undefined / infinity for at
	 * least one of the corner
	 */
	T_INV(-1),

	EMPTY(0),

	VALID(10);

	private final int flag;

	private static Map<Integer, EdgeConfig> map = new HashMap<>();

	static {
		for (EdgeConfig config: EdgeConfig.values()) {
			map.put(config.flag, config);
		}
	}
	EdgeConfig(int flag) {
		this.flag = flag;
	}

	public static EdgeConfig fromFlag(int config) {
		return map.get(config);
	}

	public int flag() {
		return flag;
	}
}

