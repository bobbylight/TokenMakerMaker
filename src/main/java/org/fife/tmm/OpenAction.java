package org.fife.tmm;

import java.awt.event.ActionEvent;

import org.fife.ui.app.AppAction;
import org.fife.ui.rtextfilechooser.RTextFileChooser;


/**
 * Loads a TokenMakerMaker specification from an XML file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class OpenAction extends AppAction<TokenMakerMaker> {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	OpenAction(TokenMakerMaker app) {
		super(app, app.getResourceBundle(), "Open");
		setIcon(app.getIcon("/open.gif"));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		TokenMakerMaker app = getApplication();
		RTextFileChooser chooser = app.getFileChooser();
		int rc = chooser.showOpenDialog(app);
		if (rc!=RTextFileChooser.APPROVE_OPTION) {
			return;
		}
		app.openFile(chooser.getSelectedFile().getAbsolutePath());
	}


}
