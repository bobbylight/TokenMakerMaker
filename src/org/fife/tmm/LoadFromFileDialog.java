package org.fife.tmm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fife.ui.EscapableDialog;
import org.fife.ui.FSATextField;
import org.fife.ui.RButton;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.SelectableLabel;
import org.fife.ui.UIUtil;
import org.fife.ui.rtextfilechooser.RTextFileChooser;


/**
 * A dialog that allows the user to specify a file name.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class LoadFromFileDialog extends EscapableDialog implements ActionListener {

	/**
	 * The parent application.
	 */
	private TokenMakerMaker app;

	/**
	 * The field the user enters a file name in.
	 */
	private FSATextField textField;

	/**
	 * The words in the file specified by the user.
	 */
	private List<String> words;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public LoadFromFileDialog(TokenMakerMaker app, String typeKey) {

		super(app);
		this.app = app;

		JPanel cp = new ResizableFrameContentPane(new BorderLayout());
		cp.setBorder(UIUtil.getEmpty5Border());
		setContentPane(cp);
		String type = app.getString("Dialog.InsertFromFile." + typeKey);

		Box content = Box.createVerticalBox();
		String text = app.getString("Dialog.InsertFromFile.Desc", type);
		SelectableLabel label = new SelectableLabel(text);
		content.add(label);
		content.add(Box.createVerticalStrut(5));
		JLabel prompt = new JLabel(app.getString("Dialog.InsertFromFile.Prompt"));
		if (getComponentOrientation().isLeftToRight()) {
			prompt.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		}
		else {
			prompt.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		}
		textField = new FSATextField(30);
		RButton browseButton = createButton(app, "Browse");
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(prompt, BorderLayout.LINE_START);
		temp.add(textField);
		JPanel temp2 = new JPanel(new BorderLayout());
		temp2.add(browseButton);
		if (getComponentOrientation().isLeftToRight()) {
			temp2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		}
		else {
			temp2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		}
		temp.add(temp2, BorderLayout.LINE_END);
		content.add(temp);
		content.add(Box.createVerticalGlue());
		cp.add(content, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new GridLayout(1,2, 5,5));
		RButton okButton = createButton(app, "OK");
		RButton cancelButton = createButton(app, "Cancel");
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(buttonPanel);
		cp.add(bottomPanel, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(okButton);
		setModal(true);
		setTitle(app.getString("Dialog.InsertFromFile.Title", type));
		pack();

	}


	/**
	 * Called when an event occurs in this dialog.
	 *
	 * @param e The event.
	 */
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("OK".equals(command)) {
			if (loadWords()) {
				setVisible(false);
			}
		}

		else if ("Cancel".equals(command)) {
			escapePressed();
		}

		else if ("Browse".equals(command)) {
			RTextFileChooser chooser = new RTextFileChooser();
			int rc = chooser.showOpenDialog(this);
			if (rc==RTextFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				textField.setFileSystemAware(false);
				textField.setText(file.getAbsolutePath());
				textField.setFileSystemAware(true);
				textField.requestFocusInWindow();
			}
		}

	}


	/**
	 * Creates a button for this panel.
	 *
	 * @param app The parent application.
	 * @param key The key for the button's text (and its action command).
	 * @return The button.
	 */
	private RButton createButton(TokenMakerMaker app, String key) {
		RButton button = new RButton(app.getString(key));
		button.setActionCommand(key);
		button.addActionListener(this);
		return button;
	}


	/**
	 * Returns the words in the file specified by the user.
	 *
	 * @return The words, or <code>null</code> if the user canceled the
	 *         dialog.
	 */
	public List<String> getWords() {
		return words;
	}


	/**
	 * Loads the words from the file the user specified.
	 *
	 * @return Whether the words were successfully loaded.
	 */
	private boolean loadWords() {

		File file = new File(textField.getText());
		if (!file.isFile()) {
			app.validateError(textField, "Error.FileDoesNotExist");
			return false;
		}

		words = new ArrayList<String>();

		try {

			BufferedReader r = new BufferedReader(new FileReader(file));
			String line;

			while ((line=r.readLine())!=null) {
				words.add(line);
			}

			r.close();

		} catch (IOException ioe) {
			app.displayException(ioe);
			return false;
		}

		return true;

	}


	/**
	 * {@inheritDoc}
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			textField.requestFocusInWindow();
		}
		super.setVisible(visible);
	}


}