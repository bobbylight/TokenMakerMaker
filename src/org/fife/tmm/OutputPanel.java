package org.fife.tmm;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

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
		setTabSize(4);
		RScrollPane sp = new RScrollPane(textArea);
		panel.add(sp);
		panel.setBorder(UIUtil.getEmpty5Border());

	}


	public void appendOutput(String output, ProcessOutputType outputType) {

		//System.out.println("Appending: " + output);

		AbstractDocument doc = (AbstractDocument)textArea.getDocument();
		try {
			Style style = StyleManager.get().getStyle(outputType);
			doc.insertString(doc.getLength(), output + "\n", style);
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

		StyleManager.get().install(textPane);
		return textPane;
	}


	@Override
	public void initializeFrom(TokenMakerInfo info) {
		// Do nothing
	}


	/**
	 * Sets the tab size in this output text pane.
	 *
	 * @param tabSize The new tab size, in characters.
	 */
	public void setTabSize(int tabSize) {

		FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
		int charWidth = fm.charWidth('m');
		int tabWidth = charWidth * tabSize;

		// NOTE: Array length is arbitrary, represents the maximum number of
		// tabs handled on a single line.
		TabStop[] tabs = new TabStop[50];
		for (int j=0; j<tabs.length; j++) {
			tabs[j] = new TabStop((j+1)*tabWidth);
		}

		TabSet tabSet = new TabSet(tabs);
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setTabSet(attributes, tabSet);

		StyledDocument doc = textArea.getStyledDocument();
		int length = doc.getLength();
		doc.setParagraphAttributes(0, length, attributes, true);

	}


	@Override
	public boolean verifyInput() {
		return true;
	}


}