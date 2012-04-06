package org.fife.tmm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;


/**
 * Panel for editing comment features of the language.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class CommentsPanel extends TmmPanel {

	private JCheckBox lineCommentsEnabledCB;
	private JTextField lineCommentStartField;
	private JCheckBox mlcsEnabledCB;
	private JTextField mlcStartDelimField;
	private JTextField mlcEndDelimField;
	private JCheckBox docCommentsEnabledCB;
	private JTextField docStartDelimField;
	private JTextField docEndDelimField;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public CommentsPanel(TokenMakerMaker app) {

		super(app);
		Listener listener = new Listener();

		lineCommentsEnabledCB = createCheckBox(app.getString("Enabled"), true);
		lineCommentsEnabledCB.addActionListener(listener);
		lineCommentStartField = new JTextField();
		mlcsEnabledCB = createCheckBox(app.getString("Enabled"), true);
		mlcsEnabledCB.addActionListener(listener);
		mlcStartDelimField = new JTextField();
		mlcEndDelimField = new JTextField();
		docCommentsEnabledCB = createCheckBox(app.getString("Enabled"), true);
		docCommentsEnabledCB.addActionListener(listener);
		docStartDelimField = new JTextField();
		docEndDelimField = new JTextField();

		panel.setLayout(new MigLayout("wrap 2", "[][grow,fill]"));
		panel.add(new Divider(app.getString("LineComments")), "span2,growx");
		panel.add(lineCommentsEnabledCB, "span2,growx");
		panel.add(new JLabel(app.getString("LineCommentStart")), "gap 20");
		panel.add(lineCommentStartField);
		panel.add(new Divider(app.getString("MultilineComments")), "span 2,growx");
		panel.add(mlcsEnabledCB, "span2,growx");
		panel.add(new JLabel(app.getString("StartDelimiter")), "gap 20");
		panel.add(mlcStartDelimField);
		panel.add(new JLabel(app.getString("EndDelimiter")), "gap 20");
		panel.add(mlcEndDelimField);
		panel.add(new Divider(app.getString("DocComments")), "span 2,growx");
		panel.add(docCommentsEnabledCB, "span2,growx");
		panel.add(new JLabel(app.getString("StartDelimiter")), "gap 20");
		panel.add(docStartDelimField);
		panel.add(new JLabel(app.getString("EndDelimiter")), "gap 20");
		panel.add(docEndDelimField);

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		info.setDocCommentEnd(docEndDelimField.getText());
		info.setDocCommentsEnabled(docCommentsEnabledCB.isSelected());
		info.setDocCommentStart(docStartDelimField.getText());
		info.setLineCommentsEnabled(lineCommentsEnabledCB.isSelected());
		info.setLineCommentStart(lineCommentStartField.getText());
		info.setMultilineCommentEnd(mlcEndDelimField.getText());
		info.setMultilineCommentsEnabled(mlcsEnabledCB.isSelected());
		info.setMultilineCommentStart(mlcStartDelimField.getText());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(TokenMakerInfo info) {
		lineCommentsEnabledCB.setSelected(info.getLineCommentsEnabled());
		updateCheckBoxChildrenEnabledStates(lineCommentsEnabledCB);
		lineCommentStartField.setText(info.getLineCommentStart());
		mlcsEnabledCB.setSelected(info.getMultilineCommentsEnabled());
		updateCheckBoxChildrenEnabledStates(mlcsEnabledCB);
		mlcStartDelimField.setText(info.getMultilineCommentStart());
		mlcEndDelimField.setText(info.getMultilineCommentEnd());
		docCommentsEnabledCB.setSelected(info.getDocCommentsEnabled());
		updateCheckBoxChildrenEnabledStates(docCommentsEnabledCB);
		docStartDelimField.setText(info.getDocCommentStart());
		docEndDelimField.setText(info.getDocCommentEnd());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {

		if (lineCommentsEnabledCB.isSelected()) {
			String lineCommentStart = lineCommentStartField.getText().trim();
			if (lineCommentStart.length()==0) {
				app.validateError(lineCommentStartField, "Error.NoEolCommentStart");
				return false;
			}
		}

		if (mlcsEnabledCB.isSelected()) {
			String mlcStart = mlcStartDelimField.getText().trim();
			if (mlcStart.length()==0) {
				app.validateError(mlcStartDelimField, "Error.NoMlcStart");
				return false;
			}
			String mlcEnd = mlcEndDelimField.getText().trim();
			if (mlcEnd.length()==0) {
				app.validateError(mlcEndDelimField, "Error.NoMlcEnd");
				return false;
			}
		}

		if (docCommentsEnabledCB.isSelected()) {
			String docStart = docStartDelimField.getText().trim();
			if (docStart.length()==0) {
				app.validateError(docStartDelimField, "Error.NoDocCommentStart");
				return false;
			}
			String docEnd = docEndDelimField.getText().trim();
			if (docEnd.length()==0) {
				app.validateError(docEndDelimField, "Error.NoDocCommentEnd");
				return false;
			}
		}

		return true;

	}


	public class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox)e.getSource();
				updateCheckBoxChildrenEnabledStates(cb);
			}
		}

	}


}