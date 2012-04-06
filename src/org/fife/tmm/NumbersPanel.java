package org.fife.tmm;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.fife.ui.SelectableLabel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import net.miginfocom.swing.MigLayout;


/**
 * Panel allowing the user to specify formats for number literals.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class NumbersPanel extends TmmPanel {

	private Listener listener;
	private JCheckBox intsEnabledCB;
	private JCheckBox hexEnabledCB;
	private JCheckBox floatsEnabledCB;
	private JComboBox intLiteralFormatCombo;
	private JComboBox hexLiteralFormatCombo;
	private JComboBox floatLiteralFormatCombo;
	private SelectableLabel intLiteralSampleLabel;
	private SelectableLabel hexLiteralSampleLabel;
	private SelectableLabel floatLiteralSampleLabel;


	public NumbersPanel(TokenMakerMaker app) {

		super(app);
		panel.setLayout(new BorderLayout());
		listener = new Listener();
		Font mono = RSyntaxTextArea.getDefaultFont(); // Better than Java's Monospaced
		mono = mono.deriveFont(new JLabel().getFont().getSize2D());

		intsEnabledCB = createCheckBox(app.getString("Enabled"), true);
		intsEnabledCB.addActionListener(listener);
		hexEnabledCB = createCheckBox(app.getString("Enabled"), true);
		hexEnabledCB.addActionListener(listener);
		floatsEnabledCB = createCheckBox(app.getString("Enabled"), true);
		floatsEnabledCB.addActionListener(listener);
		intLiteralFormatCombo = createCombo(IntLiteralFormat.values(), mono);
		hexLiteralFormatCombo = createCombo(HexLiteralFormat.values(), mono);
		floatLiteralFormatCombo = createCombo(FloatLiteralFormat.values(), mono);

		intLiteralSampleLabel = new SelectableLabel();
		intLiteralSampleLabel.setFont(mono);
		hexLiteralSampleLabel = new SelectableLabel();
		hexLiteralSampleLabel.setFont(mono);
		floatLiteralSampleLabel = new SelectableLabel();
		floatLiteralSampleLabel.setFont(mono);
		refreshSamples();

		panel.setLayout(new MigLayout("wrap 2", "[][grow,fill]"));
		panel.add(new Divider(app.getString("IntLiterals")), "span2,growx");
		panel.add(intsEnabledCB, "span2,growx");
		panel.add(new JLabel(app.getString("Format")), "gap 20");
		panel.add(intLiteralFormatCombo);
		panel.add(new JLabel(app.getString("Sample")), "gap 20");
		panel.add(intLiteralSampleLabel);

		panel.add(new Divider(app.getString("HexLiterals")), "span 2,growx");
		panel.add(hexEnabledCB, "span2,growx");
		panel.add(new JLabel(app.getString("Format")), "gap 20");
		panel.add(hexLiteralFormatCombo);
		panel.add(new JLabel(app.getString("Sample")), "gap 20");
		panel.add(hexLiteralSampleLabel);

		panel.add(new Divider(app.getString("FloatLiterals")), "span 2,growx");
		panel.add(floatsEnabledCB, "span2,growx");
		panel.add(new JLabel(app.getString("Format")), "gap 20");
		panel.add(floatLiteralFormatCombo);
		panel.add(new JLabel(app.getString("Sample")), "gap 20");
		panel.add(floatLiteralSampleLabel);

	}


	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {

		IntLiteralFormat ilf = intsEnabledCB.isSelected() ?
				(IntLiteralFormat)intLiteralFormatCombo.getSelectedItem() : null;
		info.setIntLiteralFormat(ilf);

		HexLiteralFormat hlf = hexEnabledCB.isSelected() ?
				(HexLiteralFormat)hexLiteralFormatCombo.getSelectedItem() : null;
		info.setHexLiteralFormat(hlf);

		FloatLiteralFormat flf = floatsEnabledCB.isSelected() ?
				(FloatLiteralFormat)floatLiteralFormatCombo.getSelectedItem() : null;
		info.setFloatLiteralFormat(flf);

	}


	/**
	 * Creates a combo box for use in this panel.
	 *
	 * @param values The values to display.
	 * @param font The font for the combo box.
	 * @return The combo box.
	 */
	private JComboBox createCombo(Object[] values, Font font) {
		JComboBox combo = new JComboBox(values);
		combo.setFont(font);
		combo.addActionListener(listener);
		return combo;
	}


	@Override
	public void initializeFrom(TokenMakerInfo info) {

		IntLiteralFormat ilf = info.getIntLiteralFormat();
		intsEnabledCB.setSelected(ilf!=null);
		updateCheckBoxChildrenEnabledStates(intsEnabledCB);
		if (ilf!=null) {
			intLiteralFormatCombo.setSelectedItem(ilf);
		}

		FloatLiteralFormat flf = info.getFloatLiteralFormat();
		floatsEnabledCB.setSelected(flf!=null);
		updateCheckBoxChildrenEnabledStates(floatsEnabledCB);
		if (flf!=null) {
			floatLiteralFormatCombo.setSelectedItem(flf);
		}

		HexLiteralFormat hlf = info.getHexLiteralFormat();
		hexEnabledCB.setSelected(hlf!=null);
		updateCheckBoxChildrenEnabledStates(hexEnabledCB);
		if (hlf!=null) {
			hexLiteralFormatCombo.setSelectedItem(hlf);
		}

		refreshSamples();

	}


	/**
	 * Refreshes the "sample" labels.
	 */
	private void refreshSamples() {
		refreshSampleImpl(intLiteralSampleLabel, (NumberFormat)intLiteralFormatCombo.getSelectedItem());
		refreshSampleImpl(hexLiteralSampleLabel, (NumberFormat)hexLiteralFormatCombo.getSelectedItem());
		refreshSampleImpl(floatLiteralSampleLabel, (NumberFormat)floatLiteralFormatCombo.getSelectedItem());
	}


	private void refreshSampleImpl(SelectableLabel label, NumberFormat format) {
		label.setText(format.getSample());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {
		return true;
	}


	/**
	 * Listens for events in this panel.
	 */
	private class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox)e.getSource();
				updateCheckBoxChildrenEnabledStates(cb);
			}
			else if (source instanceof JComboBox) {
				// Be lazy, refresh all of 'em since it's so cheap.
				refreshSamples();
			}
		}

	}


}