/*
 * 04/14/2012
 *
 * TmmAboutDialog.java - The About dialog for this application.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.tmm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.fife.ui.EscapableDialog;
import org.fife.ui.Hyperlink;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.SelectableLabel;
import org.fife.ui.UIUtil;


/**
 * About dialog for the application.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TmmAboutDialog extends EscapableDialog {

	private TokenMakerMaker app;


	public TmmAboutDialog(TokenMakerMaker parent) {

		super(parent);
		this.app = parent;

		JPanel cp = new ResizableFrameContentPane(new BorderLayout());

		Box box = Box.createVerticalBox();

		// Don't use a Box, as some JVM's won't have the resulting component
		// honor its opaque property.
		JPanel box2 = new JPanel();
		box2.setLayout(new BoxLayout(box2, BoxLayout.Y_AXIS));
		box2.setOpaque(true);
		box2.setBackground(Color.white);
		box2.setBorder(new TopBorder());

		JLabel label = new JLabel("TokenMakerMaker");
		label.setOpaque(true);
		label.setBackground(Color.white);
		Font labelFont = label.getFont();
		label.setFont(labelFont.deriveFont(Font.BOLD, 20));
		addLeftAligned(label, box2);
		box2.add(Box.createVerticalStrut(5));

		SelectableLabel descLabel = new SelectableLabel(app.getString("Dialog.About.Desc"));
		descLabel.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
					UIUtil.browse(e.getURL().toString());
				}
			}
		});
		box2.add(descLabel);

		box.add(box2);
		box.add(Box.createVerticalStrut(5));

		SpringLayout sl = new SpringLayout();
		JPanel temp = new JPanel(sl);
		JLabel javaLabel = new JLabel(app.getString("Dialog.About.JavaHome"));
		SelectableLabel javaField = new SelectableLabel(System.getProperty("java.home"));

		if (getComponentOrientation().isLeftToRight()) {
			temp.add(javaLabel);    temp.add(javaField);
		}
		else {
			temp.add(javaField);    temp.add(javaLabel);
		}
		UIUtil.makeSpringCompactGrid(temp, 1, 2, 5,5, 15,5);
		box.add(temp);

		box.add(Box.createVerticalStrut(10));
		box.add(Box.createVerticalGlue());

		cp.add(box, BorderLayout.NORTH);

		JButton okButton = new JButton(app.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				escapePressed();
			}
		});
		JButton libButton = new JButton(app.getString("Dialog.About.Libraries"));
		libButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LibrariesDialog().setVisible(true);
			}
		});
		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		buttonsPanel.add(okButton);
		buttonsPanel.add(libButton);
		temp = new JPanel(new BorderLayout());
		temp.setBorder(UIUtil.getEmpty5Border());
		temp.add(buttonsPanel, BorderLayout.LINE_END);
		cp.add(temp, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(okButton);
		setTitle(app.getString("About.ShortDesc"));
		setContentPane(cp);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);

		// Since JEditorPanes showing HTML have trouble with their preferred
		// size, set preferred size on a random panel inside us to force a
		// minimum width (just to look a little nicer).
		Dimension size = temp.getPreferredSize();
		if (size.width<420) {
			size.width = 420;
			temp.setPreferredSize(size);
		}
		pack();
		setLocationRelativeTo(app);

	}


	private JPanel addLeftAligned(Component toAdd, Container addTo) {
		JPanel temp = new JPanel(new BorderLayout());
		temp.setOpaque(false); // For ones on white background.
		temp.add(toAdd, BorderLayout.LINE_START);
		addTo.add(temp);
		return temp;
	}


	/**
	 * The border of the "top section" of the About dialog.
	 */
	private static class TopBorder extends AbstractBorder {

		public Insets getBorderInsets(Component c) { 
			return getBorderInsets(c, new Insets(0, 0, 0, 0));
		}

		public Insets getBorderInsets(Component c, Insets insets) {
			insets.top = insets.left = insets.right = 5;
			insets.bottom = 6;
			return insets;
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
								int width, int height) {
			Color color = UIManager.getColor("controlShadow");
			if (color==null) {
				color = SystemColor.controlShadow;
			}
			g.setColor(color);
			g.drawLine(x,y+height-1, x+width,y+height-1);
		}

	}


	/**
	 * A dialog that displays the libraries used by this application.
	 */
	private class LibrariesDialog extends EscapableDialog {

		public LibrariesDialog() {

			super(TmmAboutDialog.this);

			JPanel cp = new ResizableFrameContentPane(new BorderLayout());
			cp.setBorder(UIUtil.getEmpty5Border());

			SpringLayout sl = new SpringLayout();
			JPanel temp = new JPanel(sl);
			JLabel jflexLabel = new JLabel("JFlex:");
			Hyperlink jflexLink = new Hyperlink("http://jflex.de");
			JLabel rtextLabel = new JLabel("RText:");
			Hyperlink rtextLink = new Hyperlink("http://fifesoft.com/rtext");
			JLabel migLabel = new JLabel("MigLayout:");
			Hyperlink migLink = new Hyperlink("http://miglayout.com");
			JLabel balloonLabel = new JLabel("BalloonTips:");
			Hyperlink balloonLink = new Hyperlink("http://balloontip.dev.java.net");

			if (getComponentOrientation().isLeftToRight()) {
				temp.add(jflexLabel);        temp.add(jflexLink);
				temp.add(rtextLabel);       temp.add(rtextLink);
				temp.add(migLabel);         temp.add(migLink);
				temp.add(balloonLabel);     temp.add(balloonLink);
			}
			else {
				temp.add(jflexLink);        temp.add(jflexLabel);
				temp.add(rtextLink);       temp.add(rtextLabel);
				temp.add(migLink);         temp.add(migLabel);
				temp.add(balloonLink);     temp.add(balloonLabel);
			}
			UIUtil.makeSpringCompactGrid(temp, 4, 2, 5,5, 15,5);
			cp.add(temp, BorderLayout.NORTH);

			setContentPane(cp);
			setTitle(app.getString("Dialog.Libraries.Title"));
			setModal(true);
			pack();
			setLocationRelativeTo(TmmAboutDialog.this);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		}

	}


}