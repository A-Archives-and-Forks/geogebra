package org.geogebra.common.kernel.arithmetic;

public enum BinomialCoefficientsSign {
	None,
	AllPositive,
	AllNegative,
	Mixed;

	public boolean monotonic() {
		return this == AllPositive || this == AllNegative;
	}
}
