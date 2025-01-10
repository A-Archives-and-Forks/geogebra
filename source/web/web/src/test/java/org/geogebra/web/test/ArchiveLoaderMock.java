package org.geogebra.web.test;

import java.util.Map;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.ArchiveEntry;
import org.geogebra.web.html5.util.ArchiveLoader;

public class ArchiveLoaderMock extends ArchiveLoader {

	/**
	 * @param app application
	 */
	public ArchiveLoaderMock(AppW app) {
		super(app);
	}

	/**
	 * @param zip GeoGebra file
	 * @return string representation (compatible with {@link #setFileFromJsonString})
	 */
	public static String toJson(GgbFile zip) {
		JSONArray archive = new JSONArray();
		for (Map.Entry<String, ArchiveEntry> entry: zip.entrySet()) {
			try {
				JSONObject archiveEntry = new JSONObject();
				archiveEntry.put("fileName", entry.getKey());
				archiveEntry.put("fileContent", entry.getValue().string);
				archive.put(archiveEntry);
			} catch (JSONException e) {
				Log.debug(e);
			}
		}
		return archive.toString();
	}

	@Override
	public void processJSON(String encoded) {
		GgbFile archiveContent = new GgbFile();
		setFileFromJsonString(encoded, archiveContent);
		maybeLoadFile(archiveContent);
	}

	@Override
	public void setFileFromJsonString(String encoded, GgbFile archiveContent) {
		try {
			JSONArray array = new JSONArray(encoded);
			for (int i = 0; i < array.length(); i++) {
				JSONObject content = array.getJSONObject(i);
				archiveContent.put(content.getString("fileName"),
						content.getString("fileContent"));
			}
		} catch (JSONException e) {
			Log.debug(e);
		}
	}
}
