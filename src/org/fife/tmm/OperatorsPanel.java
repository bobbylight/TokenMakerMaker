package org.fife.tmm;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.fife.ui.RButton;
import org.fife.ui.UIUtil;


/**
 * A panel for editing the language's operators.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class OperatorsPanel extends TmmPanel {

	private WordsTable operatorsTable;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public OperatorsPanel(TokenMakerMaker app) {
		super(app);
		operatorsTable = new WordsTable(app, "Operator");
		panel.setLayout(new BorderLayout());
		JPanel temp = UIUtil.createTabbedPanePanel();
		temp.setLayout(new BorderLayout());
		temp.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		RButton useCOperatorsButton = new RButton(
						new UseCOperatorsAction(app, operatorsTable));
		temp.add(useCOperatorsButton, BorderLayout.LINE_START);
		panel.add(temp, BorderLayout.NORTH);
		panel.setBorder(UIUtil.getEmpty5Border());
		panel.add(operatorsTable);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		info.setOperators(operatorsTable.getWords());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(TokenMakerInfo info) {
		operatorsTable.setWords(info.getOperators());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {
		return true;
	}


}