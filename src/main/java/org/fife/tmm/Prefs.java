package org.fife.tmm;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.UIManager;

import org.fife.ui.OS;
import org.fife.ui.app.GUIApplicationPrefs;


/**
 * Preferences for TokenMakerMaker.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Prefs extends GUIApplicationPrefs<TokenMakerMaker> {

	public File javac;
	public File outputDir;
	public File classOutputDir;
	public String theme;
	public String fileHistoryString;

	private static final String CLASS_OUTPUT_DIR	= "classOutputDir";
	private static final String JAVAC_LOC			= "javacLoc";
	private static final String SOURCE_OUTPUT_DIR	= "sourceOutputDir";
	private static final String THEME				= "theme";
	private static final String HISTORY				= "recentFiles";

	private static final String DEFAULT_OUTPUT_DIR			= System.getProperty("java.io.tmpdir");
	private static final String DEFAULT_CLASS_OUTPUT_DIR	= DEFAULT_OUTPUT_DIR;
	private static final String DEFAULT_THEME				= null;


	public Prefs() {
		setDefaults();
	}


	@Override
	public Prefs populate(TokenMakerMaker tmm) {
		populateCommonPreferences(tmm);
		javac					= tmm.getJavac();
		outputDir				= tmm.getSourceOutputDirectory();
		classOutputDir			= tmm.getClassOutputDirectory();
		theme					= tmm.getThemeName();
		fileHistoryString		= ((AppMenuBar)tmm.getJMenuBar()).getFileHistoryString();
		return this;
	}


	private static File getDefaultJavac() {

		String javaHome = System.getProperty("java.home");

		// First, see if we're running directly from a JDK.
		String loc = "bin/javac";
		if (OS.get()==OS.WINDOWS) {
			loc += ".exe";
		}
		File javac = new File(javaHome, loc);

		if (!javac.isFile()) {

			// Next, check if we're in a JRE inside a JDK.
			javac = new File(javaHome, "../" + loc);

			if (!javac.isFile()) {
				javac = null;
			}

		}

		if (javac!=null) {
			try {
				javac = javac.getCanonicalFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return javac;

	}


	@Override
	public Prefs load() {

		try {

			// Get all properties common to all applications
			Preferences prefs = Preferences.userNodeForPackage(TokenMakerMaker.class);
			loadCommonPreferences(prefs);

			String dir = prefs.get(SOURCE_OUTPUT_DIR, DEFAULT_OUTPUT_DIR);
			outputDir = new File(dir);

			dir = prefs.get(CLASS_OUTPUT_DIR, DEFAULT_CLASS_OUTPUT_DIR);
			classOutputDir = new File(dir);

			String javac = prefs.get(JAVAC_LOC, null);
			this.javac = javac!=null ? new File(javac) : getDefaultJavac();

			theme = prefs.get(THEME, DEFAULT_THEME);

			fileHistoryString = prefs.get(HISTORY, null);

		} catch (RuntimeException re) { // FindBugs
			throw re;
		} catch (Exception e) {
			e.printStackTrace();
			setDefaults();
		}

		return this;

	}


	@Override
	public void save() {
		Preferences prefs = Preferences.userNodeForPackage(TokenMakerMaker.class);
		saveCommonPreferences(prefs);
		prefs.put(JAVAC_LOC,			javac==null ? "" : javac.getAbsolutePath());
		prefs.put(CLASS_OUTPUT_DIR,		classOutputDir.getAbsolutePath());
		prefs.put(SOURCE_OUTPUT_DIR,	outputDir.getAbsolutePath());
		prefs.put(THEME,				theme==null ? "" : theme);
		prefs.put(HISTORY,				fileHistoryString==null ? "" : fileHistoryString);
	}


	@Override
	protected void setDefaults() {

		// TODO: This should be done in GUIApplicationPrefs and we should
		// have to call super.setDefaults()
		location = new Point();
		size = new Dimension(-1, -1); // TODO: "null" size should default to "pack" behavior
		lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		toolbarVisible = true;
		statusBarVisible = true;
		language = "en";

		javac = getDefaultJavac();
		outputDir = new File(DEFAULT_OUTPUT_DIR);
		classOutputDir = new File(DEFAULT_CLASS_OUTPUT_DIR);
		theme = DEFAULT_THEME;
		//fileHistoryString = null;

	}


}
