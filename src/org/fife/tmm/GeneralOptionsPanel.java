package org.fife.tmm;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.fife.ui.FSATextField;
import org.fife.ui.OptionsDialogPanel;
import org.fife.ui.RButton;
import org.fife.ui.UIUtil;
import org.fife.ui.rtextfilechooser.RDirectoryChooser;


/**
 * General options for TokenMakerMaker.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class GeneralOptionsPanel extends OptionsDialogPanel implements ActionListener {

	private TokenMakerMaker app;
	private FSATextField sourceDirField;
	private FSATextField classDirField;
	private RDirectoryChooser dirChooser;
	private Listener listener;

	private static final String PROP_DIR		= "Directory";


	public GeneralOptionsPanel(TokenMakerMaker app, String title) {

		super(title);
		this.app = app;
		setLayout(new BorderLayout());
		setBorder(UIUtil.getEmpty5Border());
		listener = new Listener();

		JPanel temp = new JPanel(new MigLayout("wrap 3", "[][grow,fill][]"));
		JLabel label = new JLabel(app.getString("Options.General.SourceOutputDir"));
		temp.add(label);
		sourceDirField = new FSATextField();
		sourceDirField.setText(app.getSourceOutputDirectory().getAbsolutePath()); // For size
		sourceDirField.getDocument().addDocumentListener(listener);
		temp.add(sourceDirField);
		RButton browseButton = new RButton(app.getString("Browse"));
		browseButton.setActionCommand("SourceOutputDir.Browse");
		browseButton.addActionListener(this);
		temp.add(browseButton);

		label = new JLabel(app.getString("Options.General.ClassOutputDir"));
		temp.add(label);
		classDirField = new FSATextField();
		classDirField.setText(app.getClassOutputDirectory().getAbsolutePath()); // For size
		classDirField.getDocument().addDocumentListener(listener);
		temp.add(classDirField);
		browseButton = new RButton(app.getString("Browse"));
		browseButton.setActionCommand("ClassOutputDir.Browse");
		browseButton.addActionListener(this);
		temp.add(browseButton);

		add(temp, BorderLayout.NORTH);

	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("SourceOutputDir.Browse".equals(command)) {
			if (dirChooser == null) {
				dirChooser = new RDirectoryChooser(getOptionsDialog());
			}
			dirChooser.setChosenDirectory(new File(sourceDirField.getText()));
		}
		else if ("ClassOutputDir.Browse".equals(command)) {
			if (dirChooser == null) {
				dirChooser = new RDirectoryChooser(getOptionsDialog());
			}
			dirChooser.setChosenDirectory(new File(classDirField.getText()));
		}

		dirChooser.setLocationRelativeTo(getOptionsDialog());
		dirChooser.setVisible(true);
		String dir = dirChooser.getChosenDirectory();

		if (dir != null) {
			FSATextField field = "SourceOutputDir.Browse".equals(command) ?
					sourceDirField : classDirField;
			field.setFileSystemAware(false);
			field.setText(dir);
			field.setFileSystemAware(true);
		}

	}


	@Override
	protected void doApplyImpl(Frame f) {
		TokenMakerMaker app = (TokenMakerMaker)f;
		app.setSourceOutputDirectory(new File(sourceDirField.getText()));
		app.setClassOutputDirectory(new File(classDirField.getText()));
	}


	private OptionsPanelCheckResult ensureValidDir(FSATextField field) {
		OptionsPanelCheckResult res = null;
		File dir = new File(field.getText());
		if (!dir.isDirectory()) {
			res = new OptionsPanelCheckResult(this);
			res.component = field;
			res.errorMessage = app.getString("Error.InvalidDirectory",
											dir.getAbsolutePath());
		}
		return res;
	}


	@Override
	public OptionsPanelCheckResult ensureValidInputsImpl() {

		OptionsPanelCheckResult res = null;

		res = ensureValidDir(sourceDirField);
		if (res==null) {
			res = ensureValidDir(classDirField);
		}

		return res;

	}


	@Override
	public JComponent getTopJComponent() {
		return sourceDirField;
	}


	@Override
	protected void setValuesImpl(Frame f) {

		TokenMakerMaker app = (TokenMakerMaker)f;

		File outputDir = app.getSourceOutputDirectory();
		try {
			outputDir = outputDir.getCanonicalFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		sourceDirField.setText(outputDir.getAbsolutePath());

		outputDir = app.getClassOutputDirectory();
		try {
			outputDir = outputDir.getCanonicalFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		classDirField.setText(outputDir.getAbsolutePath());

	}


	/**
	 * Listens for events in this options panel.
	 */
	private class Listener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			setUnsavedChanges(true);
			firePropertyChange(PROP_DIR, null, sourceDirField.getText());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			setUnsavedChanges(true);
			firePropertyChange(PROP_DIR, null, sourceDirField.getText());
		}

	}


}