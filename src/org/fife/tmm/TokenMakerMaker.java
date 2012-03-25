/*
 * 07/19/2009
 *
 * TokenMakerMaker.java - Entry point for token maker program.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.tmm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import org.fife.help.HelpDialog;
import org.fife.ui.AboutDialog;
import org.fife.ui.CustomizableToolBar;
import org.fife.ui.RButton;
import org.fife.ui.SplashScreen;
import org.fife.ui.StatusBar;
import org.fife.ui.app.AbstractGUIApplication;
import org.fife.ui.app.GUIApplicationPreferences;
import org.fife.ui.app.StandardAction;
import org.fife.ui.rtextfilechooser.FileChooserOwner;
import org.fife.ui.rtextfilechooser.RTextFileChooser;
import org.fife.ui.rtextfilechooser.filters.ExtensionFileFilter;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import net.java.balloontip.utils.FadingUtils;
import net.java.balloontip.utils.TimingUtils;


/**
 * A GUI application for designing and creating <code>TokenMaker</code>
 * implementations for RSyntaxTextArea.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TokenMakerMaker extends AbstractGUIApplication
							implements FileChooserOwner {

	public static final String GENERATE_ACTION_KEY			= "GenerateAction";
	public static final String OPEN_ACTION_KEY				= "OpenAction";
	public static final String OPTIONS_ACTION_KEY			= "OptionsAction";
	public static final String SAVE_ACTION_KEY				= "SaveAction";

	private JTabbedPane tp;
	private OutputPanel outputPanel;
	private RTextFileChooser chooser;

	private File javac;
	private File sourceOutputDir;
	private File classOutputDir;
	private HelpDialog helpDialog;

	private static final String VERSION = "1.0";

	private static final String BUNDLE_NAME	= "org.fife.tmm.TokenMakerMaker";


	/**
	 * Constructor.
	 *
	 * @param jarFile The name (not full path) of the JAR file containing the
	 *        main class of this application (e.g. "Foobar.jar").
	 */
	public TokenMakerMaker(String jarFile) {
		super(jarFile);
	}


	/**
	 * {@inheritDoc}
	 */
	protected AboutDialog createAboutDialog() {
		return new TmmAboutDialog(this);
	}


	/**
	 * {@inheritDoc}
	 */
	protected void createActions(GUIApplicationPreferences prefs) {

		ResourceBundle msg = getResourceBundle();

		StandardAction action = new OpenAction(this);
		addAction(OPEN_ACTION_KEY, action);

		action = new SaveAction(this);
		addAction(SAVE_ACTION_KEY, action);

		action = new OptionsAction(this);
		addAction(OPTIONS_ACTION_KEY, action);

		action = new ExitAction(this, msg, "Exit");
		addAction(EXIT_ACTION_KEY, action);

		action = new HelpAction(this, msg, "Help");
		addAction(HELP_ACTION_KEY, action);

		action = new AboutAction(this, msg, "About");
		addAction(ABOUT_ACTION_KEY, action);

		action = new GenerateAction(this);
		addAction(GENERATE_ACTION_KEY, action);

	}


	private RTextFileChooser createFileChooser() {
		RTextFileChooser chooser = new RTextFileChooser();
		String desc = getString("FileChooser.XMLFiles");
		ExtensionFileFilter filter = new ExtensionFileFilter(desc, "xml");
		chooser.setFileFilter(filter);
		chooser.setEncoding("UTF-8");
		File prefsDir = getPreferencesDir();
		if (prefsDir.isDirectory()) {
			File file = getFileChooserFavoritesFile();
			if (file.isFile()) {
				try {
					chooser.loadFavorites(file);
				} catch (IOException ioe) {
					desc = getString("Error.LoadingFileChooserFavorites");
					displayException(this, ioe, desc);
				}
			}
		}
		return chooser;
	}


	/**
	 * Creates the menu bar for this application.
	 *
	 * @return The menu bar.
	 */
	@Override
	protected JMenuBar createMenuBar(GUIApplicationPreferences prefs) {
		return new MenuBar(this);
	}


	@Override
	protected SplashScreen createSplashScreen() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Creates the status bar for this application.
	 *
	 * @return The status bar.
	 */
	@Override
	protected StatusBar createStatusBar(GUIApplicationPreferences prefs) {
		StatusBar sb = new StatusBar();
		return sb;
	}


	/**
	 * Creates the tabbed pane with the editable features of the language.
	 *
	 * @return The tabbed pane.
	 */
	private JTabbedPane createTabbedPane() {

		JTabbedPane tp = new JTabbedPane();

		TmmPanel panel = new GeneralPanel(this);
		tp.add(getString("Tab.General"), panel.panel);

		panel = new CommentsPanel(this);
		tp.add(getString("Tab.Comments"), panel.panel);

		panel = new KeywordsPanel(this);
		tp.add(getString("Tab.Keywords"), panel.panel);

		panel = new Keywords2Panel(this);
		tp.add(getString("Tab.Keywords2"), panel.panel);

		panel = new DataTypesPanel(this);
		tp.add(getString("Tab.DataTypes"), panel.panel);

		panel = new FunctionsPanel(this);
		tp.add(getString("Tab.Functions"), panel.panel);

		panel = new OperatorsPanel(this);
		tp.add(getString("Tab.Operators"), panel.panel);

		panel = new StringLexingPanel(this);
		tp.add(getString("Tab.Strings"), panel.panel);

		outputPanel = new OutputPanel(this);
		tp.add(getString("Tab.Output"), outputPanel.panel);

		return tp;

	}


	@Override
	protected CustomizableToolBar createToolBar(GUIApplicationPreferences prefs) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doExit() {

		File dir = getPreferencesDir();
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				System.err.println("TokenMakerMaker: Error: Cannot create " +
						"preferences directory: " + dir.getAbsolutePath());
			}
		}

		Prefs prefs = Prefs.createPreferences(this);
		prefs.savePreferences(this);

		if (chooser!=null) {
			chooser.savePreferences();
			try {
				chooser.saveFavorites(getFileChooserFavoritesFile());
			} catch (IOException ioe) {
				String desc = getString("Error.SavingFileChooserFavorites");
				displayException(this, ioe, desc);
			}
		}

		System.exit(0);

	}


	public void focusAndClearOutputTab() {
		tp.setSelectedIndex(tp.getTabCount() - 1);
		outputPanel.clear();
	}


	/**
	 * Returns the directory in which output class files should be placed.
	 *
	 * @return The output directory.
	 * @see #setClassOutputDirectory(File)
	 */
	public File getClassOutputDirectory() {
		return classOutputDir;
	}


	/**
	 * Lazily creates and returns our file chooser.
	 *
	 * @return The file chooser.
	 */
	public RTextFileChooser getFileChooser() {
		if (chooser==null) {
			chooser = createFileChooser();
		}
		return chooser;
	}


	/**
	 * Returns the file used to store file chooser favorites.
	 *
	 * @return The file.
	 */
	private File getFileChooserFavoritesFile() {
		return new File(getPreferencesDir(), "fileChooser.favorites.txt");
	}


	@Override
	public HelpDialog getHelpDialog() {
		if (helpDialog==null) {
			String baseDir = getInstallLocation() + "/help/";
			if (!new File(baseDir).isDirectory()) { // Debugging in Eclipse
				baseDir = getInstallLocation() + "/res/help/";
			}
			String contentsFile = baseDir + "help.xml";
			helpDialog = new HelpDialog(this, contentsFile, baseDir);
		}
		helpDialog.setLocationRelativeTo(this);
		return helpDialog;
	}


	/**
	 * Returns the location of the "javac.exe" compiler to use to compile the
	 * generated .java file.
	 *
	 * @return The Java compiler location, or <code>null</code> if none is
	 *         configured.
	 * @see #setJavac(File)
	 */
	public File getJavac() {
		return javac;
	}


	public OutputPanel getOutputPanel() {
		return outputPanel;
	}


	/**
	 * Returns the directory in which generated source files (*.flex, *.java)
	 * should be placed).
	 *
	 * @return The output directory.
	 * @see #setSourceOutputDirectory(File)
	 */
	public File getSourceOutputDirectory() {
		return sourceOutputDir;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPreferencesClassName() {
		return "org.fife.tmm.Prefs";
	}


	/**
	 * Returns the directory to use when loading and saving preferences, other
	 * than those done by the Java Preferences API (which unfortunately uses
	 * the registry on Windows).
	 *
	 * @return The directory.
	 */
	private File getPreferencesDir() {
		return new File(System.getProperty("user.home"), ".tmm");
	}


	/**
	 * Returns the name of the resource bundle for this application.
	 *
	 * @return The resource bundle class name.
	 */
	@Override
	public String getResourceBundleClassName() {
		return BUNDLE_NAME;
	}


	/**
	 * Returns the version number of this application.
	 *
	 * @return The version number.
	 */
	@Override
	public String getVersionString() {
		return VERSION;
	}


	@Override
	public void openFile(String fileName) {
		// TODO Auto-generated method stub

	}


	public TokenMakerInfo createTokenMakerInfo() {
		TokenMakerInfo info = new TokenMakerInfo();
		for (int i=0; i<tp.getComponentCount(); i++) {
			JPanel temp = (JPanel)tp.getComponentAt(i);
			TmmPanel panel = (TmmPanel)temp.getClientProperty(
											TmmPanel.PROPERTY_TMM_PANEL);
			panel.configureTokenMakerInfo(info);
		}
		return info;
	}


	void load(TokenMakerInfo info) {
		for (int i=0; i<tp.getComponentCount(); i++) {
			JPanel temp = (JPanel)tp.getComponentAt(i);
			TmmPanel panel = (TmmPanel)temp.getClientProperty(
											TmmPanel.PROPERTY_TMM_PANEL);
			panel.initializeFrom(info);
		}
	}


	@Override
	protected void preDisplayInit(GUIApplicationPreferences prefs,
			SplashScreen splash) {

		// Load our preferences
		Prefs p = (Prefs)prefs;
		setJavac(p.javac);
		setSourceOutputDirectory(p.outputDir);
		setClassOutputDirectory(p.classOutputDir);

		tp = createTabbedPane();
		getContentPane().add(tp, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		JPanel temp = new JPanel(new GridLayout(1, 1));
		temp.add(new RButton(getAction(GENERATE_ACTION_KEY)));
		buttonPanel.add(temp);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	}


	@Override
	protected void preMenuBarInit(GUIApplicationPreferences prefs,
			SplashScreen splash) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void preStatusBarInit(GUIApplicationPreferences prefs,
			SplashScreen splash) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void preToolBarInit(GUIApplicationPreferences prefs,
			SplashScreen splash) {
		// TODO Auto-generated method stub

	}


	@Override
	public void preferences() {
		// TODO Auto-generated method stub

	}


	/**
	 * Sets the directory in which output class files should be placed.
	 *
	 * @param dir The output directory.
	 * @see #getClassOutputDirectory()
	 */
	public void setClassOutputDirectory(File dir) {
		this.classOutputDir = dir;
	}


	/**
	 * Sets the location of the Java compiler.
	 *
	 * @param javac The Java compiler location, or <code>null</code> for none.
	 * @see #getJavac()
	 */
	public void setJavac(File javac) {
		this.javac = javac;
	}


	/**
	 * Sets the directory in which output source files (*.flex, *.java) should
	 * be placed.
	 *
	 * @param dir The output directory.
	 * @see #getSourceOutputDirectory()
	 */
	public void setSourceOutputDirectory(File dir) {
		this.sourceOutputDir = dir;
	}


	private void showBalloonTip(JComponent parent, String text) {
		final BalloonTip tip = new BalloonTip(parent, text);
		tip.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				ActionListener al = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tip.closeBalloon();
					}
				};
				FadingUtils.fadeOutBalloon(tip, al, 400, 20);
			}
		});
		Color bg = UIManager.getColor("ToolTip.background");
		if (bg==null) {
			bg = SystemColor.info;
		}
		Color border = Color.BLACK;
		tip.setStyle(new RoundedBalloonStyle(5, 5, bg, border));
		TimingUtils.showTimedBalloon(tip, 5000);
		//FadingUtils.fadeInBalloon(tip, null, 400, 20);
	}


	void validateError(JComponent comp, String key) {
		if (comp instanceof JTextComponent) {
			((JTextComponent)comp).selectAll();
		}
		comp.requestFocusInWindow();
		showBalloonTip(comp, getString(key));
		UIManager.getLookAndFeel().provideErrorFeedback(comp);
	}


	/**
	 * Returns whether or not the user has input all the values necessary
	 * to generate a token maker class.  If there is a problem, the user
	 * is notified.
	 *
	 * @return Whether all the user's input is sufficient.
	 */
	public boolean verifyInput() {

		for (int i=0; i<tp.getComponentCount(); i++) {
			JPanel temp = (JPanel)tp.getComponentAt(i);
			TmmPanel panel = (TmmPanel)temp.getClientProperty(
											TmmPanel.PROPERTY_TMM_PANEL);
			if (!panel.verifyInput()) {
				tp.setSelectedIndex(i);
				return false;
			}
		}

		return true;

	}


}