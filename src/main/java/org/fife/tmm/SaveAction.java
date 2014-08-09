package org.fife.tmm;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.fife.ui.app.StandardAction;
import org.fife.ui.rtextfilechooser.RTextFileChooser;


/**
 * Action that saves the current <code>TokenMaker</code>.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class SaveAction extends StandardAction {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public SaveAction(TokenMakerMaker app) {
		super(app, app.getResourceBundle(), "Save");
		setIcon(app.getIcon("/save.gif"));
	}


	/**
	 * Called when this action is performed.
	 *
	 * @param e The event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		TokenMakerMaker app = (TokenMakerMaker)getApplication();
		RTextFileChooser chooser = app.getFileChooser();
		int rc = chooser.showSaveDialog(app);
		if (rc!=RTextFileChooser.APPROVE_OPTION) {
			return;
		}
		File file = chooser.getSelectedFile();

		TokenMakerInfo tmi = app.createTokenMakerInfo();
		try {
			tmi.saveToXML(file);
		} catch (IOException ioe) {
			app.displayException(ioe);
		}

	}


}