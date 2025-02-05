package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.exam.restrictions.CvteExamRestrictions;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

public final class CvteCommandArgumentFilter implements CommandArgumentFilter {

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor)
			throws MyError {
		String internalCommandName = command.getName();
		if (Commands.Circle.name().equals(internalCommandName)) {
			checkCircle(command, commandProcessor);
		} else if (Commands.Extremum.name().equals(internalCommandName)) {
			checkExtremum(command, commandProcessor);
		} else if (Commands.Root.name().equals(internalCommandName)) {
			checkRoot(command, commandProcessor);
		} else if (Commands.Intersect.name().equals(internalCommandName)) {
			checkIntersect(command, commandProcessor);
		}
	}

	private void checkCircle(Command command, CommandProcessor commandProcessor) throws MyError {
		GeoElement[] arguments = commandProcessor.resArgs(command);
		// only Circle(<Center>, <Radius>) allowed
		if (arguments.length != 2) {
			throw commandProcessor.argNumErr(command, arguments.length);
		}
		GeoElement firstArgument = arguments[0];
		if (!firstArgument.isGeoPoint()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		GeoElement secondArgument = arguments[1];
		if (!secondArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, secondArgument);
		}
	}

	private void checkExtremum(Command command, CommandProcessor commandProcessor) throws MyError {
		// only Extremum(<Function>, <Start x-Value>, <End x-Value>) allowed
		GeoElement[] arguments = commandProcessor.resArgs(command);
		if (arguments.length != 3) {
			throw commandProcessor.argNumErr(command, arguments.length);
		}
		GeoElement firstArgument = arguments[0];
		if (!firstArgument.isGeoFunction()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		GeoElement secondArgument = arguments[1];
		if (!secondArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, secondArgument);
		}
		GeoElement thirdArgument = arguments[2];
		if (!thirdArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, thirdArgument);
		}
	}

	private void checkRoot(Command command, CommandProcessor commandProcessor) throws MyError {
		// only Root( <Function>, <Start x-Value>, <End x-Value> ) allowed
		GeoElement[] arguments = commandProcessor.resArgs(command);
		if (arguments.length != 3) {
			throw commandProcessor.argNumErr(command, arguments.length);
		}
		GeoElement firstArgument = arguments[0];
		if (!firstArgument.isGeoFunction()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		GeoElement secondArgument = arguments[1];
		if (!secondArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, secondArgument);
		}
		GeoElement thirdArgument = arguments[2];
		if (!thirdArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, thirdArgument);
		}
	}

	private void checkIntersect(Command command, CommandProcessor commandProcessor) throws MyError {
		// Intersection commands are only allowed if the visibility of the arguments are not
		// restricted. There are always at least 2 arguments,
		// and the first 2 are always the geo elements that are potentially restricted:
		// Intersect(<Object>, <Object>)
		// Intersect(<Object>, <Object>, <Unrelated parameter>)
		// Intersect(<Object>, <Object>, <Unrelated parameter>, <Unrelated parameter>)
		GeoElement[] arguments = commandProcessor.resArgs(command);
		if (arguments.length >= 2) {
			GeoElement firstArgument = arguments[0];
			if (!CvteExamRestrictions.isVisibilityEnabled(firstArgument)) {
				throw commandProcessor.argErr(command, firstArgument);
			}
			GeoElement secondArgument = arguments[1];
			if (!CvteExamRestrictions.isVisibilityEnabled(secondArgument)) {
				throw commandProcessor.argErr(command, secondArgument);
			}
		}
	}
}