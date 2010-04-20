package org.fife.tmm;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.app.StandardAction;


/**
 * Action that sets the operators table to contain only the C language's
 * operators.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class UseCOperatorsAction extends StandardAction {

	private WordsTable table;


	public UseCOperatorsAction(TokenMakerMaker tmm, WordsTable table) {
		super(tmm, tmm.getResourceBundle(), "Button.COperators");
		this.table = table;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		table.setWords(createOperatorList());
	}


	/**
	 * Creates a list of all C operators, except trigraphs.
	 *
	 * @return The list of operators.
	 */
	public List<String> createOperatorList() {

		List<String> list = new ArrayList<String>();

		// Don't add trigraphs, what are the odds they want those?
		list.add("=");
		list.add("+");
		list.add("-");
		list.add("*");
		list.add("/");
		list.add("%");
		list.add("~");
		list.add("<");
		list.add(">");
		list.add("<<");
		list.add(">>");
		list.add("==");
		list.add("+=");
		list.add("-=");
		list.add("*=");
		list.add("/=");
		list.add("%=");
		list.add(">>=");
		list.add("<<=");
		list.add("^");
		list.add("&");
		list.add("&&");
		list.add("|");
		list.add("||");
		list.add("?");
		list.add(":");
		list.add(",");
		list.add("!");
		list.add("++");
		list.add("--");

		return list;

	}


}