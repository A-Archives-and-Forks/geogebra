package org.geogebra.web.html5.euclidian.profiler.drawer;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.euclidian.profiler.coords.Coordinate;
import org.geogebra.web.html5.euclidian.profiler.coords.CoordinatesParser;
import org.geogebra.web.html5.util.file.FileLoader;
import org.gwtproject.timer.client.Timer;

/**
 * Autonomously draws onto the canvas from a file. The name of the file is specified
 * by the FILE_NAME field.
 */
public class DrawingEmulator {

	private static final String FILE_NAME = "coords.json";

	private MouseTouchGestureControllerW mouseTouchGestureController;
	private List<Coordinate> coordinates;
	private int sleepIntervalSum;

	public DrawingEmulator(MouseTouchGestureControllerW mouseTouchGestureController) {
		this.mouseTouchGestureController = mouseTouchGestureController;
	}

	/**
	 * Autonomously draws onto the canvas from a file. The name of the file is specified
	 * by the FILE_NAME field.
	 */
	public void draw() {
		if (coordinates == null) {
			initCoordinatesAndDraw();
		} else {
			drawWithFpsProfiling();
		}
	}

	private void initCoordinatesAndDraw() {
		FileLoader.loadFile(FILE_NAME, getFileLoaderCallback());
	}

	private FileLoader.Callback getFileLoaderCallback() {
		return fileContent -> {
			try {
				coordinates = CoordinatesParser.parseCoordinates(fileContent);
				drawWithFpsProfiling();
			} catch (JSONException e) {
				Log.debug(e);
			}
		};
	}

	private void drawWithFpsProfiling() {
		doDraw();
		endDrawing();
	}

	private void doDraw() {
		Coordinate previousCoordinate = null;
		for (Coordinate coordinate : coordinates) {
			Drawer drawer = new Drawer(mouseTouchGestureController, coordinate);
			if (previousCoordinate == null || previousCoordinate.isTouchEnd()) {
				drawer.initiateDrawingWithTouchStart();
			}
			drawer.schedule(getSleepInterval(previousCoordinate, coordinate));
			previousCoordinate = coordinate;
		}
	}

	private int getSleepInterval(
			@CheckForNull Coordinate previousCoordinate, Coordinate actualCoordinate) {
		if (previousCoordinate == null) {
			return 0;
		} else {
			int interval = (int) (actualCoordinate.getTime() - previousCoordinate.getTime());
			sleepIntervalSum += interval;
			return sleepIntervalSum;
		}
	}

	private void endDrawing() {
		new Timer() {
			@Override
			public void run() {
				sleepIntervalSum = 0;
				GeoGebraProfiler.printDragMeasurementData();
			}
		}.schedule(sleepIntervalSum);
	}
}
