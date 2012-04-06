package org.fife.tmm;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


/**
 * Manages text styles for the output console.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see ProcessOutputType
 */
public class StyleManager {

	private Map<ProcessOutputType, Style> map;

	/**
	 * The singleton instance of this class.
	 */
	private static StyleManager INSTANCE = new StyleManager();


	/**
	 * Private constructor to prevent instantiation.
	 */
	private StyleManager() {
	}


	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The singleton instance.
	 */
	public static StyleManager get() {
		return INSTANCE;
	}


	public Style getStyle(ProcessOutputType outputType) {
		return map.get(outputType);
	}


	public void install(JTextPane textArea) {

		map = new HashMap<ProcessOutputType, Style>();
		Style defaultStyle = textArea.getStyle(StyleContext.DEFAULT_STYLE);

		Style blue = textArea.addStyle("meta", defaultStyle);
		StyleConstants.setForeground(blue, Color.blue);
		map.put(ProcessOutputType.FOOTER_INFO, blue);
		map.put(ProcessOutputType.HEADER_INFO, blue);

		Style black = textArea.addStyle("stdout", defaultStyle);
		StyleConstants.setForeground(black, Color.black);
		map.put(ProcessOutputType.STDOUT, black);

		Style error = textArea.addStyle("stderr", defaultStyle);
		StyleConstants.setForeground(error, Color.red);
		map.put(ProcessOutputType.STDERR, error);

		Style terminalError = textArea.addStyle("terminalError", error);
		StyleConstants.setItalic(terminalError, true);
		map.put(ProcessOutputType.TERMINAL_ERROR, terminalError);

	}


}