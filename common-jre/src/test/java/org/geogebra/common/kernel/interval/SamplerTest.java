package org.geogebra.common.kernel.interval;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;
import org.geogebra.common.kernel.interval.samplers.IntervalAsymptotes;

public class SamplerTest extends BaseUnitTest {

	private static final int DEFAULT_NUMBER_OF_SAMPLES = 100;

	protected IntervalTupleList functionValues(String functionDescription,
			double xmin, double xmax, double ymin, double ymax) {
		return functionValues(functionDescription, xmin, xmax, ymin, ymax,
				DEFAULT_NUMBER_OF_SAMPLES);
	}

	protected IntervalTupleList functionValues(String functionDescription,
			double xmin, double xmax, double ymin, double ymax,
			int numberOfSamples) {
		GeoFunction function = add(functionDescription);
		IntervalTuple range = PlotterUtils.newRange(xmin, xmax, ymin, ymax);
		FunctionSampler sampler = PlotterUtils.newSampler(function, range,
				numberOfSamples, null);
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(sampler.tuples());
		asymptotes.process();

		return sampler.tuples();
	}

}
