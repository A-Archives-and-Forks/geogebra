package org.geogebra.common.kernel.arithmetic;

public enum BinomialCoefficientsSign {
	None,
	AllPositive,
	AllNegative,
	Mixed;

	public static BinomialCoefficientsSign from2VarCoeffs(BernsteinPolynomial[] bernsteinCoeffs) {
		int positive = 0;
		int negative = 0;
		for (BernsteinPolynomial bcoeff : bernsteinCoeffs) {
			if (bcoeff != null) {
				switch (bcoeff.getSign()) {
				case AllPositive:
					positive++;
					break;
				case AllNegative:
					negative++;
					break;
				case None:
				case Mixed:
				}
			}
		}

		if (positive == bernsteinCoeffs.length) {
			return AllPositive;
		}

		if (negative == bernsteinCoeffs.length) {
			return AllNegative;
		}

		return Mixed;

	}

	public boolean monotonic() {
		return this == AllPositive || this == AllNegative;
	}
}
