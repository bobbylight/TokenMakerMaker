package org.fife.tmm;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.OptionsDialog;
import org.fife.ui.OptionsDialogPanel;
import org.fife.ui.app.StandardAction;
import org.fife.ui.rtextfilechooser.FileChooserFavoritesOptionPanel;
import org.fife.ui.rtextfilechooser.RTextFileChooserOptionPanel;


/**
 * An action that displays the options dialog.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class OptionsAction extends StandardAction {

	private OptionsDialog optionsDialog;


	public OptionsAction(TokenMakerMaker app) {
		super(app, app.getResourceBundle(), "Options");
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		if (optionsDialog==null) {
			TokenMakerMaker app = (TokenMakerMaker)getApplication();
			optionsDialog = createOptionsDialog();
			optionsDialog.setLocationRelativeTo(app);
		}

		optionsDialog.initialize();
		optionsDialog.setVisible(true);

	}


	private OptionsDialog createOptionsDialog() {

		TokenMakerMaker app = (TokenMakerMaker)getApplication();
		OptionsDialog dialog = new OptionsDialog(app);
		List<OptionsDialogPanel> panels = new ArrayList<OptionsDialogPanel>();

		String title = app.getString("Options.General");
		panels.add(new GeneralOptionsPanel(app, title));

		RTextFileChooserOptionPanel panel = new RTextFileChooserOptionPanel();
		panel.addChildPanel(new FileChooserFavoritesOptionPanel());
		panels.add(panel);

		dialog.setOptionsPanels(panels.toArray(new OptionsDialogPanel[2]));
		return dialog;

	}


}