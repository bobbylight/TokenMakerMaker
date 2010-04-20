package org.fife.tmm;

//import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import org.fife.io.ProcessRunner;
import org.fife.ui.app.StandardAction;

import JFlex.SilentExit;


/**
 * Generates a .java file from the properties defined in the application.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class GenerateAction extends StandardAction {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public GenerateAction(TokenMakerMaker app) {
		super(app, app.getResourceBundle(), "Generate");
	}


	/**
	 * Called when this action is performed.
	 *
	 * @param e The event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		TokenMakerMaker tmm = (TokenMakerMaker)getApplication();
		if (!tmm.verifyInput()) {
			return;
		}
		TokenMakerInfo tmi = tmm.createTokenMakerInfo();

		// Get the directory to write the .flex and .java files to.
		// Create this directory if it does not yet exist.
		File outputDir = tmm.getSourceOutputDirectory();
		String pkg = tmi.getPackage();
		if (pkg!=null) {
			// Use "/" instead of File.separator so we can also use it as a
			// class name later
			pkg = pkg.replaceAll("\\.", "/");
			outputDir = new File(outputDir, pkg);
		}
		if (!outputDir.isDirectory() && !outputDir.mkdirs()) {
			// TODO: Localize me
			JOptionPane.showMessageDialog(null, "Error creating output directory:\n" + outputDir.getAbsolutePath());
			return;
		}

		// Create the .flex input file.
		File flexFile = null;
		try {
			flexFile = tmi.createFlexFile(outputDir);
		} catch (IOException ioe) {
			getApplication().displayException(ioe);
		}
		if (flexFile==null) {
			return;
		}

		// Use JFlex to create the .java source from the .flex file.
		File javaFile = generateJavaSource(flexFile);
		if (javaFile==null) {
			return;
		}

		// Get the location of the source relative to the source root (e.g.
		// get the source class name, including package).
		String sourceFile = javaFile.getName();
		if (pkg!=null) {
			// Use "/" instead of File.separator so we can also use it as a
			// class name later
			sourceFile = pkg + "/" + sourceFile;
		}

		// Any extra post-processing
		if (!massageJavaSource(javaFile)) {
			return;
		}

//		try {
//			Desktop.getDesktop().open(javaFile);
//		} catch (IOException ioe) {
//			getApplication().displayException(ioe);
//		}

		// Compile the generated Java source.
		if (!compileJavaSource(tmm.getSourceOutputDirectory(), sourceFile,
								tmm.getClassOutputDirectory())) {
			return;
		}

	}


	private boolean compileJavaSource(File sourceRootDir, String sourceFile,
									File classDir) {

		TokenMakerMaker tmm = (TokenMakerMaker)getApplication();
		String installDir = tmm.getInstallLocation();
		File rstaJar = new File(installDir, "rsyntaxtextarea.jar");
		if (!rstaJar.isFile()) { // Debugging in Eclipse
			rstaJar = new File(installDir, "../RSyntaxTextArea/dist/rsyntaxtextarea.jar");
			if (!rstaJar.isFile()) {
				String desc = tmm.getString("Error.RSyntaxTextAreaJarNotFound");
				FileNotFoundException fnfe = new FileNotFoundException(desc);
				tmm.displayException(fnfe);
				return false;
			}
		}

		boolean success = false;

//		try {

			String javaHome = System.getProperty("java.home");
			String exeName = getExeName("javac");
			File exe = new File(javaHome, "bin/" + exeName); // JDK
			if (!exe.isFile()) {
				exe = new File(javaHome, "../bin/" + exeName); // JRE in JDK
			}

			if (!exe.isFile()) {
				// TODO: Localize me
				String text = "Cannot find javac.exe";
				String title = "TokenMakerMaker - Error";
				JOptionPane.showMessageDialog(null, text, title,
						JOptionPane.ERROR_MESSAGE);
			}
			else {

				List<String> command = new ArrayList<String>();
				command.add(exe.getAbsolutePath());
				command.add("-classpath");
				command.add(rstaJar.getAbsolutePath());
				command.add("-d");
				command.add(classDir.getAbsolutePath());
				command.add(sourceFile);

				String[] args = new String[command.size()];
				args = command.toArray(args);
				ProcessRunner pr = new ProcessRunner(args);
				pr.setDirectory(sourceRootDir);
				System.out.println("Directory: " + pr.getDirectory().getAbsolutePath());
				System.out.println("Command:   " + pr.getCommandLineString());
				pr.run();
				int rc = pr.getReturnCode();
				if (rc!=0) {
					System.err.println(pr.getStderr());
				}
				else {

					String classFilePath = sourceFile.substring(0,
							sourceFile.lastIndexOf('.')) + ".class";

					try {
						TesterFrame tf = new TesterFrame(tmm, classDir,
														classFilePath);
						tf.setVisible(true);
					} catch (Exception e) {
						getApplication().displayException(e);
					}

				}

			}

//		} catch () {
//			
//		}

		return success;

	}


	private File generateJavaSource(File flexFile) {

		TokenMakerMaker tmm = (TokenMakerMaker)getApplication();
		String outputDir = flexFile.getParentFile().getAbsolutePath();

		String installDir = tmm.getInstallLocation();
		File skeletonFile = new File(installDir, "skeleton.default");
		if (!skeletonFile.isFile()) { // Debugging in Eclipse
			skeletonFile = new File(installDir, "extra/skeleton.default");
		}
		String[] args = { flexFile.getAbsolutePath(), "-d", outputDir,
							"--skel", skeletonFile.getAbsolutePath() };
		try {
			JFlex.Main.generate(args);
		} catch (SilentExit se) {
			// TODO: Improve error message
			tmm.displayException(new Exception("JFlex generation failed"));
			return null;
		}

		String fileName = flexFile.getName();
		fileName = fileName.substring(0, fileName.indexOf('.')) + ".java";
		return new File(outputDir, fileName);

	}


	private static final String getExeName(String root) {
		if (osIsWindows()) {
			root += ".exe";
		}
		return root;
	}


	private boolean massageJavaSource(File javaFile) {
		return true;
	}


	/**
	 * Returns whether the OS is Windows.
	 *
	 * @return Whether the OS is Windows.
	 */
	private static final boolean osIsWindows() {
		return File.separatorChar=='\\';
	}


}