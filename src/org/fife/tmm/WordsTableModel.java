package org.fife.tmm;

import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


/**
 * Table model for <code>WordsTable</code> instances.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class WordsTableModel extends DefaultTableModel {

	private TableRowSorter<TableModel> sorter;


	public WordsTableModel() {
		sorter = new TableRowSorter<TableModel>(this);
	}


	public void addWords(List<String> words) {
		if (words!=null) {
			for (String word : words) {
				addRow(new String[] { word });
			}
		}
	}


	public void clear() {
		for (int i=getRowCount()-1; i>=0; i--) {
			removeRow(0);
		}
	}


	public TableRowSorter<TableModel> getRowSorter() {
		return sorter;
	}


	/**
	 * Prevents cells from being edited.
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}


	public void removeRow(int row) {
		// Since we've been sorted, hack it so correct row is removed.
		row = sorter.convertRowIndexToModel(row);
		super.removeRow(row);
	}


	public void setWords(List<String> words) {
		clear();
		addWords(words);
	}


}