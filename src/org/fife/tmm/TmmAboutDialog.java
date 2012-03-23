package org.fife.tmm;

import java.awt.BorderLayout;
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
		panel.setLayout(new BorderLayout());
		panel.add(createTextArea(), BorderLayout.LINE_START);
		return panel;
	}


	private JEditorPane createTextArea() {

		// TODO: Localize me
		String text = "<html><h2>TokenMakerMaker</h2>" +
		"Allows the creation of syntax highlighters for RSyntaxTextArea.<p>" +
		"Copyright 2012 Robert Futrell<br>" +
		"Licensed under the <a href='http://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22New_BSD_License.22_or_.22Modified_BSD_License.22.29'>Modified BSD</a> liense<p>" +
		"Libraries used:" +
		"<table style='margin-left: 12px;'>" +
		"<tr><td>&bull; JFlex - </td><td><a href='http://jflex.de'>http://jflex.de</a></td></tr>" +
		"<tr><td>&bull; RText - </td><td><a href='http://fifesoft.com/rtext'>http://fifesoft.com/rtext</a></td></tr>" +
		"<tr><td>&bull; MigLayout - </td><td><a href='http://miglayout.com'>http://miglayout.com</a></td></tr>" +
		"<tr><td>&bull; BalloonTips - </td><td><a href='https://balloontip.dev.java.net/'>https://balloontip.dev.java.net</a></td></tr>" +
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