package org.fife.tmm;

import java.awt.BorderLayout;


/**
 * Panel allowing the user to specify formats for number literals.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class NumbersPanel extends TmmPanel {


	public NumbersPanel(TokenMakerMaker app) {

		super(app);
		panel.setLayout(new BorderLayout());

	}


	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		// TODO Auto-generated method stub

	}


	@Override
	public void initializeFrom(TokenMakerInfo info) {
		// TODO Auto-generated method stub

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {
		return true;
	}


}