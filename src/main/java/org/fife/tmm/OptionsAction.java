package org.fife.tmm;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.OptionsDialog;
import org.fife.ui.OptionsDialogPanel;
import org.fife.ui.app.AppAction;
import org.fife.ui.rtextfilechooser.FileChooserFavoritesOptionPanel;
import org.fife.ui.rtextfilechooser.RTextFileChooserOptionPanel;


/**
 * An action that displays the options dialog.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class OptionsAction extends AppAction<TokenMakerMaker> {

	private OptionsDialog optionsDialog;


	public OptionsAction(TokenMakerMaker app) {
		super(app, app.getResourceBundle(), "Options");
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		if (optionsDialog==null) {
			TokenMakerMaker app = getApplication();
			optionsDialog = createOptionsDialog();
			optionsDialog.setLocationRelativeTo(app);
		}

		optionsDialog.initialize();
		optionsDialog.setVisible(true);

	}


	private OptionsDialog createOptionsDialog() {

		TokenMakerMaker app = getApplication();
		List<OptionsDialogPanel> panels = new ArrayList<>();

		String title = app.getString("Options.General");
		panels.add(new GeneralOptionsPanel(app, title));

		RTextFileChooserOptionPanel panel = new RTextFileChooserOptionPanel();
		panel.addChildPanel(new FileChooserFavoritesOptionPanel());
		panels.add(panel);

		OptionsDialog od = new OptionsDialog(app);
		od.setOptionsPanels(panels);
		return od;

	}


}
