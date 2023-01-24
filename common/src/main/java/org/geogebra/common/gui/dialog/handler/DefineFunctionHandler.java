package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class DefineFunctionHandler implements ErrorHandler {
	private final App app;
	private boolean errorOccurred;

	public DefineFunctionHandler(App app) {
		this.app = app;
	}

	public void handle(String text, GeoEvaluatable geo) {
		errorOccurred = false;
		String input = text.isEmpty() ? undefinedText(geo) : text;

		if (geo instanceof GeoFunction) {
			EvalInfo info = new EvalInfo(!app.getKernel().getConstruction()
					.isSuppressLabelsActive(), false, false);
			app.getKernel().getAlgebraProcessor().changeGeoElementNoExceptionHandling(geo,
						input, info, false, null, this);
		}
	}

	private String undefinedText(GeoEvaluatable geo) {
		return geo.getLabel(StringTemplate.defaultTemplate) + "("
				+ ((VarString) geo).getVarString(StringTemplate.defaultTemplate) + ")=?";
	}

	@Override
	public void showError(String msg) {
		errorOccurred = true;
	}

	@Override
	public void showCommandError(String command, String message) {
		errorOccurred = true;
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		errorOccurred = true;
		return false;
	}

	@Override
	public void resetError() {
		errorOccurred = false;
	}

	public boolean hasErrorOccurred() {
		return errorOccurred;
	}
}
