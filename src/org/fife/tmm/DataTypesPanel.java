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
		panel.setLayout(new BorderLayout());

		JPanel temp = UIUtil.createTabbedPanePanel(new BorderLayout());
		temp.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		RButton fromFileButton = new RButton(new DataTypesFromFileAction());
		temp.add(fromFileButton, BorderLayout.LINE_START);
		panel.add(temp, BorderLayout.NORTH);

		dataTypesTable = new WordsTable(app, "DataType");
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


	/**
	 * Action that adds a list of data types from a file.
	 */
	private class DataTypesFromFileAction extends AbstractAction {

		public DataTypesFromFileAction() {
			putValue(NAME, app.getString("AddFromFile"));
		}

		public void actionPerformed(ActionEvent e) {
			LoadFromFileDialog lffd = new LoadFromFileDialog(app, "DataTypes");
			lffd.setLocationRelativeTo(DataTypesPanel.this.panel);
			lffd.setVisible(true);
			List<String> dataTypes = lffd.getWords();
			if (dataTypes!=null) {
				dataTypesTable.addWords(dataTypes);
			}
		}

	}


}