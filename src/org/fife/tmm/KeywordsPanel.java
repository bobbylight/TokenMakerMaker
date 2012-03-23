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
 * A panel for editing the language's keywords.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class KeywordsPanel extends TmmPanel {

	protected WordsTable keywordsTable;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public KeywordsPanel(TokenMakerMaker app) {

		super(app);
		panel.setLayout(new BorderLayout());

		JPanel temp = UIUtil.createTabbedPanePanel(new BorderLayout());
		temp.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		RButton fromFileButton = new RButton(new KeywordsFromFileAction());
		temp.add(fromFileButton, BorderLayout.LINE_START);
		panel.add(temp, BorderLayout.NORTH);

		keywordsTable = new WordsTable(app, "Keyword");
		panel.setBorder(UIUtil.getEmpty5Border());
		panel.add(keywordsTable);

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		info.setKeywords(keywordsTable.getWords());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(TokenMakerInfo info) {
		keywordsTable.setWords(info.getKeywords());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {
		return true;
	}


	/**
	 * Action that adds a list of keywords from a file.
	 */
	private class KeywordsFromFileAction extends AbstractAction {

		public KeywordsFromFileAction() {
			putValue(NAME, app.getString("AddFromFile"));
		}

		public void actionPerformed(ActionEvent e) {
			LoadFromFileDialog lffd = new LoadFromFileDialog(app, "Keywords");
			lffd.setLocationRelativeTo(KeywordsPanel.this.panel);
			lffd.setVisible(true);
			List<String> keywords = lffd.getWords();
			if (keywords!=null) {
				keywordsTable.addWords(keywords);
			}
		}

	}


}