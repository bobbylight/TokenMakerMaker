package org.fife.tmm;

import java.awt.Frame;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.fife.ui.AboutDialog;
import org.fife.ui.SelectableLabel;
import org.fife.ui.UIUtil;


/**
 * About dialog for the application.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TmmAboutDialog extends AboutDialog implements HyperlinkListener {


	public TmmAboutDialog(Frame parent) {
		super(parent);
	}


	protected JPanel createAboutApplicationPanel() {

		JPanel panel = UIUtil.createTabbedPanePanel();
		panel.setBorder(UIUtil.getEmpty5Border());

		JEditorPane textArea = createTextArea();
		panel.add(textArea);

		return panel;

	}


	private JEditorPane createTextArea() {

		// TODO: Localize me
		String text = "<html><h2>TokenMakerMaker</h2>" +
		"Allows the creation of syntax highlighters for RSyntaxTextArea.<p>" +
		"Copyright 2010 Robert Futrell<br>" +
		"Licensed under the <a href='http://www.gnu.org/copyleft/lesser.html'>LGPL</a><p>" +
		"Libraries used:" +
		"<ul>" +
		"<li>JFlex - <a href='http://jflex.de'>http://jflex.de</a>" +
		"<li>RText - <a href='http://fifesoft.com/rtext'>http://fifesoft.com/rtext</a>" +
		"<li>MigLayout - <a href='http://miglayout.com'>http://miglayout.com</a>" +
		"<li>BalloonTips - <a href='https://balloontip.dev.java.net/'>https://balloontip.dev.java.net</a>" +
		"</ul>";

		SelectableLabel label = new SelectableLabel(text);
		label.addHyperlinkListener(this);
		return label;

	}


	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
			URL url = e.getURL();
			try {
				UIUtil.browse(url.toURI());
			} catch (URISyntaxException use) { // Never happens
				use.printStackTrace();
			}
		}
	}


}