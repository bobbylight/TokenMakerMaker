package org.fife.tmm;

import java.awt.event.ActionEvent;

import org.fife.ui.app.StandardAction;
import org.fife.ui.rtextfilechooser.RTextFileChooser;


/**
 * Loads a TokenMakerMaker specification from an XML file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class OpenAction extends StandardAction {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public OpenAction(TokenMakerMaker app) {
		super(app, app.getResourceBundle(), "Open");
		setIcon(app.getIcon("/open.gif"));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		TokenMakerMaker app = (TokenMakerMaker)getApplication();
		RTextFileChooser chooser = app.getFileChooser();
		int rc = chooser.showOpenDialog(app);
		if (rc!=RTextFileChooser.APPROVE_OPTION) {
			return;
		}
		app.openFile(chooser.getSelectedFile().getAbsolutePath());
	}


}