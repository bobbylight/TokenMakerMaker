package org.fife.tmm;

import java.io.File;


/**
 * Obligatory utility methods.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Utils {


	private Utils() {
	}


	/**
	 * Returns a file with a different extension.
	 *
	 * @param file A file.
	 * @param newExtension The new extension.
	 * @return A file with its extension replaced by the new one.
	 */
	public static File getFileWithNewExtension(File file, String newExtension) {
		String path = file.getAbsolutePath();
		int lastDot = path.lastIndexOf('.');
		if (lastDot>-1) {
			path = path.substring(0, lastDot+1) + newExtension;
		}
		else {
			path = path + "." + newExtension;
		}
		return new File(path);
	}


}