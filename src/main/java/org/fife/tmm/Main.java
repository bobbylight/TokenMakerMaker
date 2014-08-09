/*
 * 07/19/2009
 *
 * Main.java - Application entry point.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.tmm;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.fife.ui.modifiabletable.ModifiableTable;


/**
 * Main class of TokenMakerMaker.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Main {

	private static final String JAR_FILE_NAME		= "tmm.jar";


	/**
	 * Program entry point.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty(
							ModifiableTable.PROPERTY_PANELS_NON_OPAQUE, "true");
					String laf = UIManager.getSystemLookAndFeelClassName();
					UIManager.setLookAndFeel(laf);
				} catch (RuntimeException re) {
					throw re; // FindBugs
				} catch (Exception e) {
					e.printStackTrace(); // Never happens
				}
				new TokenMakerMaker(JAR_FILE_NAME);
			}
		});
	}


}