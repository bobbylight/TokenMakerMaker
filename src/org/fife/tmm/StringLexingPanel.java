package org.fife.tmm;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;


/**
 * A panel containing string lexing options.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class StringLexingPanel extends TmmPanel {

	private JCheckBox stringCB;
	private JTextField stringDelimField;
	private JCheckBox stringMultiLineCB;
	private JCheckBox charCB;
	private JTextField charDelimField;
	private JCheckBox charMultiLineCB;
	private JCheckBox verbatimCB;
	private JTextField verbatimDelimField;


	public StringLexingPanel(TokenMakerMaker app) {

		super(app);
		Listener listener = new Listener();

		stringCB = createCheckBox(app.getString("EnableStrings"));
		stringCB.addActionListener(listener);
		stringDelimField = new JTextField();
		stringMultiLineCB = createCheckBox(app.getString("StringsMultiLine"));
		charCB = createCheckBox(app.getString("EnableCharLiterals"));
		charCB.addActionListener(listener);
		charDelimField = new JTextField();
		charMultiLineCB = createCheckBox(app.getString("CharLiteralsMultiLine"));
		verbatimCB = createCheckBox(app.getString("EnableVerbatimStrings"));
		verbatimCB.addActionListener(listener);
		verbatimDelimField = new JTextField();

		panel.setLayout(new MigLayout("wrap 2", "[][grow,fill]"));

		panel.add(new Divider(app.getString("Strings")), "span 2,growx");
		panel.add(stringCB, "span 2,growx");
		JLabel label = new JLabel(app.getString("Delimiter"));
		panel.add(label, "gap 20");
		panel.add(stringDelimField);
		stringDelimField.setText("\"");
		stringDelimField.setEnabled(false);
		stringDelimField.putClientProperty(DONT_ENABLE, Boolean.TRUE);
		panel.add(stringMultiLineCB, "gap 20,span 2,growx");

		panel.add(new Divider(app.getString("CharLiterals")), "span 2,growx");
		panel.add(charCB, "span 2,growx");
		label = new JLabel(app.getString("Delimiter"));
		panel.add(label, "gap 20");
		panel.add(charDelimField);
		charDelimField.setText("'");
		charDelimField.setEnabled(false);
		charDelimField.putClientProperty(DONT_ENABLE, Boolean.TRUE);
		panel.add(charMultiLineCB, "gap 20,span 2,growx");

		panel.add(new Divider(app.getString("VerbatimStrings")), "span 2,growx");
		panel.add(verbatimCB, "span 2,growx");
		JLabel mlsLabel = new JLabel(app.getString("Delimiter"));
		panel.add(mlsLabel, "gap 20");
		panel.add(verbatimDelimField);

		stringCB.setSelected(true);
		charCB.setSelected(true);
		verbatimCB.setSelected(false);
		verbatimDelimField.setEnabled(false);

		// TODO: Add support for verbatim strings.
		verbatimCB.setEnabled(false);
		mlsLabel.setEnabled(false);

	}


	/**
	 * {@inheritDoc}
	 */
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		info.setBackticksEnabled(false);
		info.setCharsEnabled(charCB.isSelected());
		info.setCharsMultiLine(charMultiLineCB.isSelected());
		info.setStringsEnabled(stringCB.isSelected());
		info.setStringsMultiLine(stringMultiLineCB.isSelected());
	}


	/**
	 * {@inheritDoc}
	 */
	public void initializeFrom(TokenMakerInfo info) {
		stringCB.setSelected(info.getStringsEnabled());
		stringMultiLineCB.setSelected(info.getStringsMultiLine());
		charCB.setSelected(info.getCharsEnabled());
		charMultiLineCB.setSelected(info.getCharsMultiLine());
		verbatimCB.setSelected(false);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {
		return true;
	}


	public class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox)e.getSource();
				boolean enabled = cb.isSelected();
				Container parent = cb.getParent();
				int index = getIndexOf(parent, cb);
				if (index>-1) { // Always true
					for (int i=index+1; i<parent.getComponentCount(); i++) {
						Component next = parent.getComponent(i);
						if (next instanceof Divider) {
							break;
						}
						else {
							if (next instanceof JComponent) {
								JComponent jc = (JComponent)next;
								if (Boolean.TRUE==jc.getClientProperty(DONT_ENABLE)) {
									continue;
								}
							}
							next.setEnabled(enabled);
						}
					}
				}
			}
		}

		private final int getIndexOf(Container parent, Component c) {
			for (int i=0; i<parent.getComponentCount(); i++) {
				if (parent.getComponent(i)==c) {
					return i;
				}
			}
			return -1;
		}

	}


}