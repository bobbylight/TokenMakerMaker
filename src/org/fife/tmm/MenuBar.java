package org.fife.tmm;

import java.util.ResourceBundle;

import javax.swing.JMenu;


/**
 * The menu bar of the application.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class MenuBar extends org.fife.ui.app.MenuBar {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public MenuBar(TokenMakerMaker app) {

		ResourceBundle msg = app.getResourceBundle();

		JMenu menu = createMenu(msg, "File");
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.OPEN_ACTION_KEY)));
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.SAVE_ACTION_KEY)));
		menu.addSeparator();
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.OPTIONS_ACTION_KEY)));
		menu.addSeparator();
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.EXIT_ACTION_KEY)));
		add(menu);

		menu = createMenu(msg, "Help");
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.HELP_ACTION_KEY)));
		menu.addSeparator();
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.ABOUT_ACTION_KEY)));
		add(menu);

	}


}