package org.geogebra.common.kernel.interval;

import java.util.stream.Stream;

public class TuplesQuery {
	private final IntervalTupleList tuples;

	public TuplesQuery(IntervalTupleList tuples) {
		this.tuples = tuples;
	}

	public Stream<IntervalTuple> invertedTuples() {
		return tuples.stream().filter(t -> t.y().isInverted());
	}

	public Stream<IntervalTuple> emptyTuples() {
		return tuples.stream().filter(t -> t.y().isEmpty());
	}
}
