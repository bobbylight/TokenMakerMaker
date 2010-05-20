package org.fife.tmm;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.prefs.Preferences;
import javax.swing.UIManager;

import org.fife.ui.app.GUIApplicationPreferences;


/**
 * Preferences for TokenMakerMaker.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Prefs extends GUIApplicationPreferences {

	public File javac;
	public File outputDir;
	public File classOutputDir;

	private static final String CLASS_OUTPUT_DIR	= "classOutputDir";
	private static final String JAVAC_LOC			= "javacLoc";
	private static final String SOURCE_OUTPUT_DIR	= "sourceOutputDir";

	private static final String DEFAULT_OUTPUT_DIR			= System.getProperty("java.io.tmpdir");
	private static final String DEFAULT_CLASS_OUTPUT_DIR	= DEFAULT_OUTPUT_DIR;


	public Prefs() {
		setDefaults();
	}


	public static Prefs createPreferences(TokenMakerMaker tmm) {

		Prefs prefs = new Prefs();

		// "Common" preferences
		prefs.location				= tmm.getLocation();
		prefs.location.translate(15,15);
		prefs.size					= tmm.isMaximized() ? new Dimension(-1,-1) : tmm.getSize();
		prefs.lookAndFeel			= UIManager.getLookAndFeel().getClass().getName();
		prefs.toolbarVisible		= tmm.getToolBarVisible();
		prefs.statusBarVisible		= tmm.getStatusBarVisible();
		prefs.language				= tmm.getLanguage();

		prefs.javac					= tmm.getJavac();
		prefs.outputDir				= tmm.getSourceOutputDirectory();
		prefs.classOutputDir		= tmm.getClassOutputDirectory();

		return prefs;

	}


	private File getDefaultJavac() {

		String javaHome = System.getProperty("java.home");

		// First, see if we're running directly from a JDK.
		String loc = "bin/javac";
		if (File.separatorChar=='\\') {
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

		if (javac!=null && javac.isFile()) {
			try {
				javac = javac.getCanonicalFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return javac;

	}


	public static GUIApplicationPreferences loadPreferences() {

		Prefs prefs = new Prefs();

		try {

			// Get all properties associated with the RText class.
			Preferences prefs2 = Preferences.userNodeForPackage(TokenMakerMaker.class);
			loadCommonPreferences(prefs, prefs2);

			String dir = prefs2.get(SOURCE_OUTPUT_DIR, DEFAULT_OUTPUT_DIR);
			prefs.outputDir = new File(dir);

			dir = prefs2.get(CLASS_OUTPUT_DIR, DEFAULT_CLASS_OUTPUT_DIR);
			prefs.classOutputDir = new File(dir);

		} catch (RuntimeException re) { // FindBugs
			throw re;
		} catch (Exception e) {
			e.printStackTrace();
			prefs.setDefaults();
		}

		return prefs;

	}


	@Override
	public void savePreferences(Object tmm) {
		Preferences prefs = Preferences.userNodeForPackage(TokenMakerMaker.class);
		saveCommonPreferences(prefs);
		prefs.put(JAVAC_LOC,			javac==null ? "" : javac.getAbsolutePath());
		prefs.put(CLASS_OUTPUT_DIR,		classOutputDir.getAbsolutePath());
		prefs.put(SOURCE_OUTPUT_DIR,	outputDir.getAbsolutePath());
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void setDefaults() {

		// TODO: This should be done in GUIApplicationPreferences and we should
		// have to call super.setDefaults()
		location = new Point();
		size = new Dimension(-1, -1); // TODO: "null" size should default to "pack" behavior
		lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		toolbarVisible = true;
		statusBarVisible = true;
		language = "en";
		accelerators = new HashMap();

		javac = getDefaultJavac();
		outputDir = new File(DEFAULT_OUTPUT_DIR);
		classOutputDir = new File(DEFAULT_CLASS_OUTPUT_DIR);

	}


}