package org.geogebra.common.jre.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.geogebra.common.util.debug.Log;

public class StreamUtil {

	/**
	 * Writes all contents of the given InputStream to a byte array.
	 * @param is input stream
	 * @return Byte array with the content of the input stream.
	 * @throws IOException when reading or writing fails
	 */
	public static byte[] loadIntoMemory(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copyStream(is, bos);
		return bos.toByteArray();
	}

	/**
	 * Writes all contents of the given InputStream to a String
	 * @return stream content
	 */
	public static String loadIntoString(InputStream is) {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is, StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			Log.debug(e);
		}

		return sb.toString();
	}

	private static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[4096];
		int len;
		while ((len = in.read(buf)) > -1) {
			out.write(buf, 0, len);
		}
	}
}
