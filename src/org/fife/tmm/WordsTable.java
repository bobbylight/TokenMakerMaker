package org.fife.tmm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.fife.ui.modifiabletable.ModifiableTable;
import org.fife.ui.modifiabletable.RowHandler;


/**
 * A modifiable table that contains a simple list of words.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class WordsTable extends ModifiableTable {

	private TokenMakerMaker app;
	private String type;

	public WordsTable(TokenMakerMaker app, String root) {
		super(new DefaultTableModel()); // Unfortunate
		WordsTableModel model = new WordsTableModel();
		model.setColumnCount(1);
		getTable().setModel(model);
		getTable().setTableHeader(null);
		getTable().setShowGrid(false);
		getTable().setRowSorter(model.getRowSorter());
		getTable().getRowSorter().toggleSortOrder(0);
		this.app = app;
		setRowHandler(new KeywordRowHandler(app));
		type = app.getString("Dialog.ModifySomething." + root);
	}


	public void addWords(List<String> words) {
		if (words!=null) {
			Collections.sort(words);
		}
		WordsTableModel wtm = (WordsTableModel)getTable().getModel();
		wtm.addWords(words);
	}


	@SuppressWarnings("unchecked")
	public List<String> getWords() {
		List<String> words = new ArrayList<String>();
		Vector<Vector<Object>> vec = getDataVector();
		if (vec!=null && vec.size()>0) {
			for (int i=0; i<vec.size(); i++) {
				Vector<Object> temp = vec.get(i);
				String text = (String)temp.get(0);
				words.add(text);
			}
		}
		Collections.sort(words);
		return words;
	}


	public void setWords(List<String> words) {
		WordsTableModel wtm = (WordsTableModel)getTable().getModel();
		wtm.setWords(words);
	}


	private class KeywordRowHandler implements RowHandler {

		private TokenMakerMaker parent;

		private KeywordRowHandler(TokenMakerMaker parent) {
			this.parent = parent;
		}

		@Override
		public Object[] getNewRowInfo(Object[] old) {

			String key = "Dialog.ModifySomething.Prompt." +
				(old==null ? "NewSomething" : "ModifySomething");
			String text = app.getString(key, type);

			String title = app.getString("Dialog.ModifySomething.Prompt.Title",
											type);

			String input = (String)JOptionPane.showInputDialog(parent, text,
					title, JOptionPane.QUESTION_MESSAGE, null, null,
								old==null ? null : old[0]);
			return input==null || input.length()==0 ?
									null : new String[] { input };
		}

		@Override
		public boolean shouldRemoveRow(int row) {
			return true;
		}

		@Override
		public void updateUI() {
		}
		
	}


}