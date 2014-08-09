package org.fife.tmm;


/**
 * Available number formats (int, hex, floating point) implement this
 * interface.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface NumberFormat {

	/**
	 * Returns the format string, as it will be inserted into the JFlex file.
	 *
	 * @return The format string.
	 */
	String getFormat();

	/**
	 * Returns a sample of literals that would be matched by this format.
	 *
	 * @return A sample of literals that would be matched.
	 */
	String getSample();

}