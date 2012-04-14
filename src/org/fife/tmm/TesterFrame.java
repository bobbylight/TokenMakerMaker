package org.fife.tmm;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.fife.ui.UIUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rtextarea.RTextScrollPane;


/**
 * A simple frame with a <code>RSyntaxTextArea</code> instance, testing a
 * newly-generated token maker.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TesterFrame extends JFrame {

	private RSyntaxTextArea textArea;


	public TesterFrame(TokenMakerMaker app, File dir, String classFile)
									throws Exception {

		ClassLoader parent = getClass().getClassLoader();
		URL[] urls = new URL[1];
		urls[0] = dir.toURI().toURL();
		URLClassLoader ucl = URLClassLoader.newInstance(urls, parent);
		String className = classFile.substring(0, classFile.lastIndexOf('.'));
		className = className.replaceAll("/", ".");
		Class<?> clazz = ucl.loadClass(className);
		TokenMaker tm = (TokenMaker)clazz.newInstance();
		
		JPanel cp = new JPanel(new BorderLayout());

		cp.setBorder(UIUtil.getEmpty5Border());
		textArea = new RSyntaxTextArea(20, 50);
		String themeName = app.getThemeName();
		if (themeName!=null) {
			BufferedInputStream bin = new BufferedInputStream(
					getClass().getResourceAsStream("/" + themeName));
			Theme theme = Theme.load(bin);
			bin.close();
			theme.apply(textArea);
		}
		((RSyntaxDocument)textArea.getDocument()).setSyntaxStyle(tm);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		cp.add(sp);

		setContentPane(cp);
		setTitle(app.getString("TesterFrame.Title", className));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(app);

	}


}