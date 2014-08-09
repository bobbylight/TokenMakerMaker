package org.fife.tmm;


/**
 * The different types of output that can be written to the output console
 * when a process is running.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public enum ProcessOutputType {
	HEADER_INFO,
	STDOUT,
	STDERR,
	FOOTER_INFO,
	TERMINAL_ERROR;
}