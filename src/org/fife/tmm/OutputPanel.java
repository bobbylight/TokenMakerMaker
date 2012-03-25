package org.fife.tmm;

import java.awt.BorderLayout;

import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

import org.fife.ui.RScrollPane;
import org.fife.ui.UIUtil;
import org.fife.ui.rtextarea.RTextArea;


/**
 * A panel that displays the output from generating the TokenMaker, rather
 * than providing more options to configure it.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class OutputPanel extends TmmPanel {

	private JTextPane textArea;


	public OutputPanel(TokenMakerMaker app) {

		super(app);
		panel.setLayout(new BorderLayout());

		textArea = createTextArea();
		RScrollPane sp = new RScrollPane(textArea);
		panel.add(sp);
		panel.setBorder(UIUtil.getEmpty5Border());

	}


	public void appendOutput(String output, boolean stderr) {
		System.out.println("Appending: " + output);
		AbstractDocument doc = (AbstractDocument)textArea.getDocument();
		try {
			doc.insertString(doc.getLength(), output + "\n", null);
		} catch (BadLocationException ble) {
			ble.printStackTrace(); // Never happens
		}
		textArea.setCaretPosition(doc.getLength());
	}


	public void clear() {
		textArea.setText(null);
	}


	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		// Do nothing
	}


	private JTextPane createTextArea() {
		JTextPane textPane = new JTextPane() {
			public boolean getScrollableTracksViewportWidth() {
				return getUI().getPreferredSize(this).width<=getParent().getSize().width;
			}
		};
		textPane.setFont(RTextArea.getDefaultFont()); // Better system-default monospaced.
		textPane.setEditable(false);
		textPane.setText("Output from each generation will go here.");
		return textPane;
	}


	@Override
	public void initializeFrom(TokenMakerInfo info) {
		// Do nothing
	}


	@Override
	public boolean verifyInput() {
		return true;
	}


}