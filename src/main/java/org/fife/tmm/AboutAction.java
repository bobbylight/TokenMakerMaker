/*
 * 04/14/2012
 *
 * AboutAction.java - Displays the About dialog.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.tmm;

import java.awt.event.ActionEvent;

import org.fife.ui.app.StandardAction;


/**
 * Displays the About dialog.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class AboutAction extends StandardAction {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public AboutAction(TokenMakerMaker app) {
		super(app, app.getResourceBundle(), "About");
	}


	/**
	 * Called when this action is performed.
	 *
	 * @param e The event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		TokenMakerMaker tmm = (TokenMakerMaker)getApplication();
		TmmAboutDialog dialog = new TmmAboutDialog(tmm);
		dialog.setVisible(true);
	}


}