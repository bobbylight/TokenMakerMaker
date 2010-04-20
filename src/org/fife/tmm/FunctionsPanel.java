package org.fife.tmm;

import java.awt.BorderLayout;

import org.fife.ui.UIUtil;


/**
 * A panel for editing the language's functions.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class FunctionsPanel extends TmmPanel {

	private WordsTable functionsTable;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public FunctionsPanel(TokenMakerMaker app) {
		super(app);
		functionsTable = new WordsTable(app, "Function");
		panel.setLayout(new BorderLayout());
		panel.setBorder(UIUtil.getEmpty5Border());
		panel.add(functionsTable);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		info.setFunctions(functionsTable.getWords());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(TokenMakerInfo info) {
		functionsTable.setWords(info.getFunctions());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {
		return true;
	}


}