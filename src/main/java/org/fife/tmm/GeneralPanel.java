package org.fife.tmm;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.fife.ui.SpecialValueComboBox;

import net.miginfocom.swing.MigLayout;


/**
 * Panel for general information about the lexer class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class GeneralPanel extends TmmPanel {

	private JTextField packageField;
	private JTextField classNameField;
	private SpecialValueComboBox extendedClassCombo;
	private JTextArea classCommentArea;
	private JCheckBox caseSensitiveCB;
	private JCheckBox booleanLiteralCB;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public GeneralPanel(TokenMakerMaker app) {

		super(app);

		packageField = new JTextField();
		classNameField = new JTextField();
		String[] baseClasses = {
				app.getString("TokenMakerType.CDerivedSyntax"),
				app.getString("TokenMakerType.AllOthers"),
		};
		extendedClassCombo = new SpecialValueComboBox();
		extendedClassCombo.addSpecialItem(baseClasses[0], "AbstractJFlexCTokenMaker");
		extendedClassCombo.addSpecialItem(baseClasses[1], "AbstractJFlexTokenMaker");
		classCommentArea = new JTextArea(10, 50);
		caseSensitiveCB = createCheckBox(app.getString("CaseSensitive"), true);
		booleanLiteralCB = createCheckBox(app.getString("BooleanLiterals"), false);

		panel.setLayout(new MigLayout("wrap 2", "[][grow,fill]"));
		panel.add(new JLabel(app.getString("Package")));
		panel.add(packageField);
		panel.add(new JLabel(app.getString("ClassName")));
		panel.add(classNameField);
		panel.add(new JLabel(app.getString("TokenMakerType")));
		panel.add(extendedClassCombo);
		panel.add(new JLabel(app.getString("ClassComment")));
		panel.add(new JScrollPane(classCommentArea));
		panel.add(caseSensitiveCB, "span 2,growx");
		panel.add(booleanLiteralCB, "span 2,growx");

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		info.setClassDoc(classCommentArea.getText());
		info.setClassName(classNameField.getText().trim());
		info.setExtendedClass(extendedClassCombo.getSelectedSpecialItem());
		info.setIgnoreCase(!caseSensitiveCB.isSelected());
		info.setBooleanLiterals(booleanLiteralCB.isSelected());
		info.setPackage(packageField.getText().trim());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(TokenMakerInfo info) {
		packageField.setText(info.getPackage());
		classNameField.setText(info.getClassName());
		extendedClassCombo.setSelectedItem(info.getExtendedClass());
		classCommentArea.setText(info.getClassDoc());
		caseSensitiveCB.setSelected(!info.getIgnoreCase());
		booleanLiteralCB.setSelected(info.getBooleanLiterals());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyInput() {

		String pkgName = packageField.getText().trim();
		if (pkgName.length()>0 && !pkgName.matches("\\w+(?:\\.\\w+)*")) {
			app.validateError(packageField, "Error.InvalidPackage");
			return false;
		}

		String className = classNameField.getText().trim();
		if (!className.matches("\\p{Alpha}\\p{Alnum}*")) {
			app.validateError(classNameField, "Error.InvalidClassName");
			return false;
		}

		return true;

	}


}