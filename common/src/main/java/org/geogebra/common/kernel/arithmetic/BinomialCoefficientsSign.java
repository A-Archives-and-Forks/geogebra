package org.geogebra.common.kernel.arithmetic;

public enum BinomialCoefficientsSign {
	None,
	AllPositive,
	AllNegative,
	Mixed;


	public static BinomialCoefficientsSign from1Var(double[] bernsteinCoeffs, int degree) {
		double count = 0;
		for (int i = 0; i < degree + 1; i++) {
			count += Math.signum(bernsteinCoeffs[i]);
		}

		if (Math.abs(count) == degree + 1) {
			return count < 0 ? AllNegative : AllPositive;
		}

		return Mixed;
	}

	public static BinomialCoefficientsSign from2Var(BernsteinPolynomial[] bernsteinCoeffs) {
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
