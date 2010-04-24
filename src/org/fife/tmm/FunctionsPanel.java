package org.fife.tmm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.fife.ui.RButton;
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
		panel.setLayout(new BorderLayout());

		JPanel temp = UIUtil.createTabbedPanePanel(new BorderLayout());
		temp.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		RButton fromFileButton = new RButton(new FunctionsFromFileAction());
		temp.add(fromFileButton, BorderLayout.LINE_START);
		panel.add(temp, BorderLayout.NORTH);

		functionsTable = new WordsTable(app, "Function");
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


	/**
	 * Action that adds a list of functions from a file.
	 */
	private class FunctionsFromFileAction extends AbstractAction {

		public FunctionsFromFileAction() {
			putValue(NAME, app.getString("AddFromFile"));
		}

		public void actionPerformed(ActionEvent e) {
			LoadFromFileDialog lffd = new LoadFromFileDialog(app, "Functions");
			lffd.setLocationRelativeTo(FunctionsPanel.this.panel);
			lffd.setVisible(true);
			List<String> dataTypes = lffd.getWords();
			if (dataTypes!=null) {
				functionsTable.addWords(dataTypes);
			}
		}

	}


}