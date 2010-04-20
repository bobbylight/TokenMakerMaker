package org.fife.tmm;

import java.awt.BorderLayout;

import org.fife.ui.UIUtil;


/**
 * A panel for editing the language's data types.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class DataTypesPanel extends TmmPanel {

	private WordsTable dataTypesTable;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public DataTypesPanel(TokenMakerMaker app) {
		super(app);
		dataTypesTable = new WordsTable(app, "DataType");
		panel.setLayout(new BorderLayout());
		panel.setBorder(UIUtil.getEmpty5Border());
		panel.add(dataTypesTable);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		info.setDataTypes(dataTypesTable.getWords());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(TokenMakerInfo info) {
		dataTypesTable.setWords(info.getDataTypes());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {
		return true;
	}


}