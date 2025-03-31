package org.geogebra.common.kernel;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSetup;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * Periodically tries evaluating current input and creates preview
 * 
 * @author Mathieu + Zbynek
 */
public class ScheduledPreviewFromInputBar implements Runnable {

	private static final int DEFAULT_MAX_LENGTH = 1000;
	@Weak
	private final Kernel kernel;
	private final int timeoutMs;
	private String input = "";
	// concurrent evaluation with CAS may set validInput to null
	private @CheckForNull String validInput = "";
	private ErrorHandler validation;
	private int maxLength = DEFAULT_MAX_LENGTH;
	private boolean notFirstInput;
	private GeoElement[] previewGeos;
	private String[] sliders;

	private final Set<GeoElementSetup> geoElementSetups = new HashSet<>();

	/**
	 * @param kernel
	 *            kernel
	 */
	ScheduledPreviewFromInputBar(Kernel kernel) {
		this(kernel, 200);
	}

	ScheduledPreviewFromInputBar(Kernel kernel, int timeoutMs) {
		this.kernel = kernel;
		notFirstInput = false;
		this.timeoutMs = timeoutMs;
	}

	/**
	 * Adds a {@link GeoElementSetup} which can modify the initial setup of elements
	 * for the preview.
	 *
	 * @param geoElementSetup The {@link GeoElementSetup} to be added
	 */
	public void addGeoElementSetup(GeoElementSetup geoElementSetup) {
		geoElementSetups.add(geoElementSetup);
	}

	/**
	 * Removes the previously added {@link GeoElementSetup} from this
	 * {@code ScheduledPreviewFromInputBar}. Once removed, it will no longer affect
	 * the initial setup of elements for the preview.
	 *
	 * @param geoElementSetup The {@link GeoElementSetup} to be removed
	 */
	public void removeGeoElementSetup(GeoElementSetup geoElementSetup) {
		geoElementSetups.remove(geoElementSetup);
	}

	private void setInput(String str, ErrorHandler validation) {
		this.input = str;
		this.validation = validation;
		if (StringUtil.emptyTrim(str)) {
			Log.debug("empty");
			validInput = "";
			maxLength = DEFAULT_MAX_LENGTH;
			return;
		}
		if (str.length() > maxLength) {
			validInput = null;
			Log.debug("Timeout at length " + maxLength);
			return;
		}
		long start = System.currentTimeMillis();
		try {
			ValidExpression ve = this.kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(input);

			if (kernel.getSymbolicMode() == SymbolicMode.SYMBOLIC_AV
					&& ve.any(Inspecting::isVectorDivision)) {
				throw new MyError(kernel.getLocalization(), MyError.Errors.IllegalDivision);
			}

			if (ve != null) {
				validInput = input;
			}
		} catch (MyError t) {
			ErrorHelper.handleError(t, null, kernel.getLocalization(),
					validation);
		} catch (Exception e) {
			ErrorHelper.handleException(e, kernel.getApplication(), validation);
		} catch (Error e) {
			ErrorHelper.handleException(new Exception(e),
					kernel.getApplication(), validation);
		}
		// maxLength is not written if the first preview computed
		// needed in Android with old phones, probably some other thread
		// makes the computation too long (so false positive)
		if (notFirstInput && System.currentTimeMillis() > start + timeoutMs) {
			maxLength = str.length();
			validInput = null;
		} else {
			notFirstInput = true;
			maxLength = DEFAULT_MAX_LENGTH;
		}
	}

	/**
	 * @param fallback
	 *            what to return if no input is valid
	 * @return last valid value of input
	 */
	public String getInput(String fallback) {
		if (fallback == null || fallback.length() == 0) {
			return "";
		}
		String ret = validInput;
		validInput = null;
		if (ret == null || ret.length() == 0) {
			return fallback;
		}
		return ret;
	}

	@Override
	public void run() {
		cleanOldSliders();
		if (StringUtil.emptyTrim(input)) {
			if (validation != null) {
				validation.resetError();
			}
			this.kernel.notifyUpdatePreviewFromInputBar(null);
			return;
		}
		if (StringUtil.empty(validInput)) {
			if (validation != null) {
				// timeout -- assume OK as we don't know if it's wrong
				validation.showError(maxLength != DEFAULT_MAX_LENGTH ? null
						: kernel.getLocalization().getInvalidInputError());
			}
			this.kernel.notifyUpdatePreviewFromInputBar(null);
			return;
		}
		EvalInfo info = new EvalInfo(false, true)
				.withScripting(false)
				.withCAS(false)
				.addDegree(kernel.getAngleUnitUsesDegrees())
				.withSymbolic(true)
				.withCopyingPlainVariables(true);
		Log.debug("preview for: " + validInput);
		boolean silentModeOld = this.kernel.isSilentMode();
		boolean suppressLabelsOld = this.kernel.getConstruction().isSuppressLabelsActive();
		previewGeos = null;
		long start = System.currentTimeMillis();
		try {
			this.kernel.setSilentMode(true);
			ValidExpression ve = this.kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(validInput);

			if (!isCASeval(ve)
					&& kernel.getSymbolicMode() != SymbolicMode.SYMBOLIC_AV) {
				GeoElement existingGeo = this.kernel.lookupLabel(ve.getLabel());
				if (existingGeo == null) {

					GeoElementND[] inputGeos = evalValidExpression(ve, info);
					previewGeos = null;
					if (inputGeos != null) {
						// TODO use thisif we want text centering
						// InputHelper.updateProperties(inputGeos, kernel
						// .getApplication().getActiveEuclidianView(), -2);
						int unlabeled = 0;
						for (GeoElementND geo : inputGeos) {
							if (geo instanceof GeoFunction) {
								boolean b = ((GeoFunction) geo)
										.validate(ve.getLabel() == null, false);
								if (!b) {
									geo.setUndefined();
								}
							}
							if (!geo.isLabelSet()) {
								geo.setSelectionAllowed(false);
								unlabeled++;
							}
						}
						previewGeos = new GeoElement[unlabeled];
						int i = 0;
						for (GeoElementND geo : inputGeos) {
							if (!geo.isLabelSet()) {
								GeoElement geoElement = geo.toGeoElement();
								geoElementSetups.forEach(setup -> setup.applyTo(geoElement));
								previewGeos[i++] = geoElement;
							}
						}
					}

					this.kernel.notifyUpdatePreviewFromInputBar(previewGeos);
				} else if (PreviewFeature.isAvailable(PreviewFeature.MOB_PREVIEW_WHEN_EDITING)
						&& !existingGeo.hasChildren() && existingGeo.isIndependent()) {
					previewRedefine(ve, existingGeo, info);
				} else {
					Log.debug("existing geo: " + existingGeo);
					kernel.notifyUpdatePreviewFromInputBar(null);
					resetIfValid();
				}
			} else {
				Log.debug("cas cell ");
				resetIfValid();
				kernel.notifyUpdatePreviewFromInputBar(null);
			}

			if (previewGeos != null) {
				resetIfValid();
			}
		} catch (Throwable ee) {
			Log.debug("-- invalid input" + ee + ":" + validInput);
			this.kernel.setSilentMode(true);
			this.kernel.notifyUpdatePreviewFromInputBar(null);
		} finally {
			this.kernel.setSilentMode(silentModeOld);
			this.kernel.getConstruction().setSuppressLabelCreation(suppressLabelsOld);
		}
		if (System.currentTimeMillis() > start + timeoutMs) {
			maxLength = validInput == null ? 0 : validInput.length();
			validInput = null;
		}
	}

	private void previewRedefine(ValidExpression ve, GeoElement existingGeo, EvalInfo info) {
		ve.setLabels(null);
		resetIfValid(); // reset if no syntax error, command errors triggered below
		GeoElementND[] inputGeos = evalValidExpression(ve, info);
		if (inputGeos != null && inputGeos.length == 1) {
			GeoElementND redefined = inputGeos[0];
			if (redefined.getGeoClassType() == existingGeo.getGeoClassType()) {
				existingGeo.set(redefined);
				geoElementSetups.forEach(setup -> setup.applyTo(existingGeo));
				kernel.notifyUpdatePreviewFromInputBar(new GeoElement[] {existingGeo});
			}
		}
	}

	private void resetIfValid() {
		if (validation != null && validInput != null && validInput.equals(input)) {
			validation.resetError();
		}
	}

	private GeoElementND[] evalValidExpression(ValidExpression ve, EvalInfo info) {
		return kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(ve, false,
						validation, null, info.withSliders(
								kernel.getApplication().isHTML5Applet()
										&& kernel.getApplication().getConfig()
												.hasSlidersInAV()));
	}

	private static boolean isCASeval(ValidExpression ve) {
		String label = ve.getLabel();
		if (label != null && label.startsWith("$")) {
			int row = -1;
			try {
				row = Integer.parseInt(label.substring(1)) - 1;
			} catch (Exception e) {
				// spreadsheet reference
			}
			return row > 0;
		}
		return false;
	}

	private void cleanOldSliders() {
		if (sliders != null) {
			for (String sliderLabel : sliders) {
				GeoElement slider = kernel.lookupLabel(sliderLabel.trim());
				slider.setFixed(false);
				slider.remove();
			}
			kernel.notifyRepaint();
			sliders = null;
		}
	}

	/**
	 * try to create/update preview for input typed
	 * 
	 * @param newInput
	 *            current algebra input
	 * @param validate
	 *            validation callback
	 */
	public void updatePreviewFromInputBar(String newInput,
			ErrorHandler validate) {
		if (this.input.equals(newInput)) {
			Log.debug("no update needed (same input)");
			return;
		}
		setInput(newInput, validate);
		kernel.getApplication().schedulePreview(this);
	}

	/**
	 * preview is not recalculated if input has not changed since last
	 * calculation
	 *
	 * @param newInput
	 *            input
	 * @return GeoElement[] preview for this input
	 */
	public GeoElement[] getPreview(String newInput) {

		if (this.input.equals(newInput)) {
			Log.debug("no update needed (same input)");
			return previewGeos;
		}

		// create new preview immediately
		kernel.getApplication().cancelPreview();
		setInput(newInput, validation);
		run();
		return previewGeos;
	}

	/**
	 * @param string
	 *            slider names
	 */
	public void addSliders(String string) {
		cleanOldSliders();
		sliders = string.split(",");
	}

	/**
	 * @return whether last input parses OK
	 */
	public boolean isValid() {
		if (validInput == null && input != null) {
			setInput(input, validation);
		}
		return input != null && input.equals(validInput);
	}

	/**
	 * Clears preview.
	 */
	public void clear() {
		input = "";
		validInput = "";
		previewGeos = null;
		setInput("", null);
	}
}