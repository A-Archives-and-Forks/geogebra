package org.geogebra.common.kernel.arithmetic.bernstein;

import org.geogebra.common.util.MyMath;

public class BinomialCache {
	private static BinomialCache INSTANCE = null;
	private double[][] table;
	private BinomialCache() {
		table = new double[100][100];
	}
	private static BinomialCache get() {
		if (INSTANCE == null) {
			INSTANCE = new BinomialCache();
		}
		return INSTANCE;
	}

	public static double get(int n, int k) {
		return get().binomial(n, k);
	}
	public double binomial(int n, int k) {
		double v = table[n][k];
		if (v == 0) {
			table[n][k] = MyMath.binomial(n, k);
		}
		return table[n][k];
	}
}
