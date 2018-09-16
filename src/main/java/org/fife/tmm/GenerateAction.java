package org.fife.tmm;

//import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.fife.io.ProcessRunner;
import org.fife.io.ProcessRunnerOutputListener;
import org.fife.ui.app.AppAction;


/**
 * Generates a .java file from the properties defined in the application.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class GenerateAction extends AppAction<TokenMakerMaker> {

	private TokenMakerInfo tmi;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	GenerateAction(TokenMakerMaker app) {
		super(app, app.getResourceBundle(), "Generate");
	}


	/**
	 * Called when this action is performed.
	 *
	 * @param e The event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		TokenMakerMaker tmm = getApplication();
		if (!tmm.verifyInput()) {
			return;
		}
		tmi = tmm.createTokenMakerInfo();

		tmm.focusAndClearOutputTab();

		// Get the directory to write the .flex and .java files to.
		// Create this directory if it does not yet exist.
		File outputDir = tmm.getSourceOutputDirectory();
		if (!outputDir.isDirectory() && !outputDir.mkdirs()) {
			// TODO: Localize me
			JOptionPane.showMessageDialog(null, "Error creating output directory:\n" + outputDir.getAbsolutePath());
			return;
		}

		// Create the .flex input file.
		String text = tmm.getString("Output.GeneratingFlexSource");
		tmm.getOutputPanel().appendOutput(text, ProcessOutputType.HEADER_INFO);
		File flexFile = null;
		try {
			flexFile = tmi.createFlexFile(outputDir);
		} catch (IOException ioe) {
			getApplication().displayException(ioe);
		}
		if (flexFile==null) {
			return;
		}
		tmm.getOutputPanel().appendOutput("", ProcessOutputType.FOOTER_INFO);

		// Use JFlex to create the .java source from the .flex file.
		generateJavaSource(flexFile);

	}


	private boolean compileJavaSource(File sourceRootDir, String sourceFile,
									File classDir) {

		TokenMakerMaker tmm = getApplication();

		File rstaJar = getRstaJar(tmm);
//		if (rstaJar==null || !rstaJar.isFile()) { // Debugging in Eclipse
//
//			File installDir = new File(tmm.getInstallLocation());
//			File rstaBuildDir = new File(installDir, "../RSyntaxTextArea/build/libs");
//			if (rstaBuildDir.isDirectory()) {
//				File[] artifacts = rstaBuildDir.listFiles(new RstaJarFilter());
//				if (artifacts!=null && artifacts.length>0) {
//					rstaJar = artifacts[0];
//					try {
//						rstaJar = rstaJar.getCanonicalFile();
//					} catch (IOException ioe) {
//						// Ignore, path is just uglier
//					}
//				}
//			}
//			if (rstaJar==null || !rstaJar.isFile()) {
//				String desc = tmm.getString("Error.RSyntaxTextAreaJarNotFound");
//				FileNotFoundException fnfe = new FileNotFoundException(desc);
//				tmm.displayException(fnfe);
//				return false;
//			}
//
//		}

		File javac = tmm.getJavac();
		if (javac==null) { // They left javac field blank.
			String desc = tmm.getString("Error.JavacNotConfigured");
			tmm.getOutputPanel().appendOutput("\n\n" + desc, ProcessOutputType.FOOTER_INFO);
			String title = tmm.getString("Warning.DialogTitle");
			JOptionPane.showMessageDialog(tmm, desc, title,
											JOptionPane.WARNING_MESSAGE);
			return false;
		}
		else if (!javac.isFile()) { // Shouldn't happen
			String desc = tmm.getString("Error.JavacNotFile", javac.getAbsolutePath());
			tmm.getOutputPanel().appendOutput("\n\n" + desc, ProcessOutputType.TERMINAL_ERROR);
			tmm.displayException(new IOException(desc));
			return false;
		}

		boolean success = false;

//		try {

			List<String> command = new ArrayList<String>();
			command.add(javac.getAbsolutePath());
			command.add("-classpath");
			command.add(rstaJar.getAbsolutePath());
			command.add("-d");
			command.add(classDir.getAbsolutePath());
			command.add(sourceFile);

			String[] args = new String[command.size()];
			args = command.toArray(args);
			ProcessRunner pr = new ProcessRunner(args);
			pr.setDirectory(sourceRootDir);

			String text = tmm.getString("Output.Compiling");
			tmm.getOutputPanel().appendOutput("\n\n" + text, ProcessOutputType.HEADER_INFO);

			pr.setOutputListener(new CompilingOutputListener(classDir, sourceFile));
			Thread thread = new Thread(new ProcessRunnerRunnable(pr));
			thread.start();

//		} catch () {
//
//		}

		return success;

	}


	private void generateJavaSource(File flexFile) {

		TokenMakerMaker tmm = getApplication();
		String outputDir = flexFile.getParentFile().getAbsolutePath();

		File javaFile = Utils.getFileWithNewExtension(flexFile, "java");
		if (javaFile.isFile()) {
			javaFile.delete(); // TODO: Error handling?
		}

		String installDir = tmm.getInstallLocation();
		File skeletonFile = new File(installDir, "skeleton.default");
		if (!skeletonFile.isFile()) { // Debugging in an IDE
			skeletonFile = new File(new File("."), "src/main/dist/skeleton.default");
		}

		File jflexJar = getJFlexJar(tmm);

		// Run JFlex off the EDT and collect its output as it runs.
		// We'll parse the generated .java file afterwards.
		String javaExe = tmm.getJavaExe().getAbsolutePath();
		String[] command = { javaExe, "-cp",
				jflexJar.getAbsolutePath(), "JFlex.Main",
				flexFile.getAbsolutePath(), "-d", outputDir,
				"--skel", skeletonFile.getAbsolutePath()
		};
		final ProcessRunner pr = new ProcessRunner(command);
		pr.setOutputListener(new JFlexOutputListener(flexFile));
		Thread thread = new Thread(new ProcessRunnerRunnable(pr));
		String text = tmm.getString("Output.GeneratingJavaSource");
		tmm.getOutputPanel().appendOutput(text, ProcessOutputType.HEADER_INFO);
		thread.start();

	}


	/**
	 * Returns all entries on the application's classpath.
	 *
	 * @return All classpath entries.
	 */
	private static String[] getClasspathEntries() {

		// When run from an IDE such as Eclipse, jars are on the command line
		String[] entries = System.getProperty("java.class.path").
				split(File.pathSeparator);

		// If running from the built jar, we must check the manifest for
		// classpath entries
		if (entries.length == 1) {

			try {
				JarFile jsf = new JarFile(entries[0]);
				try {
					Manifest mf = jsf.getManifest();
					Attributes attrs = mf.getMainAttributes();
					String classpath = attrs.getValue("Class-Path");
					entries = classpath.split(" ");
				} finally {
					jsf.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

		}

		return entries;

	}


	/**
	 * Returns the classpath entry that matches the given regular expression.
	 *
	 * @param entries The classpath entries.
	 * @param regex The regular expression.
	 * @return The (first) matching classpath entry, or <code>null</code> if
	 *         no match is found.
	 */
	private static String getClasspathEntryMatching(String[] entries,
			String regex) {
		Pattern p = Pattern.compile(regex);
		for (String entry : entries) {
			if (p.matcher(entry).matches()) {
				return entry;
			}
		}
		return null;
	}


	private File getClasspathJarImpl(TokenMakerMaker tmm, String fileRegex) {
		String[] classpathEntries = getClasspathEntries();
		String jflexJarPath = getClasspathEntryMatching(classpathEntries,
				fileRegex);
		File file = new File(jflexJarPath);
		if (!file.isAbsolute()) {
			file = new File(tmm.getInstallLocation(), jflexJarPath);
		}
		return file;
	}


	private File getJFlexJar(TokenMakerMaker tmm) {
		return getClasspathJarImpl(tmm, ".*jflex\\-\\d\\.\\d\\.\\d\\.jar$");
	}


	private File getRstaJar(TokenMakerMaker tmm) {
		return getClasspathJarImpl(tmm,
				".*rsyntaxtextarea\\-\\d\\.\\d\\.\\d(?:\\-SNAPSHOT)?\\.jar$");
	}


	private boolean massageJavaSource(File javaFile) {
		return true;
	}


	private void processGeneratedJavaSource(File javaFile) {

		if (javaFile==null) {
			return;
		}

		// Get the location of the source relative to the source root (e.g.
		// get the source class name, including package).
		String sourceFile = javaFile.getName();
		String pkg = tmi.getPackage();
		if (pkg!=null) {
			// Use "/" instead of File.separator so we can also use it as a
			// class name later
			pkg = pkg.replace('.', '/');
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
		TokenMakerMaker tmm = getApplication();
		compileJavaSource(tmm.getSourceOutputDirectory(), sourceFile,
								tmm.getClassOutputDirectory());

	}

    /**
     * Listens for a process's output.
     */
	private class BaseOutputListener implements ProcessRunnerOutputListener {

		@Override
		public void processCompleted(final Process p, final int rc, final Throwable t) {
			SwingUtilities.invokeLater(() -> processCompletedEdt(p, rc, t));
		}

		/**
		 * Invoked on the EDT when the process completes.  The default
		 * implementation writes the process return code to the console.
		 * Subclasses can override.
		 *
		 * @param p The process.
		 * @param rc The process's return code.
		 * @param t An exception that was thrown and caused the process to be
		 *        forcibly terminated, or <code>null</code> if the process
		 *        exited cleanly.
		 */
		protected void processCompletedEdt(Process p, int rc, Throwable t) {
			TokenMakerMaker tmm = getApplication();
			String text = tmm.getString("Output.ProcessRC", Integer.toString(rc));
			tmm.getOutputPanel().appendOutput(text, ProcessOutputType.FOOTER_INFO);
		}

		@Override
		public void outputWritten(Process p, final String line, final boolean stderr) {
			SwingUtilities.invokeLater(() -> {
                TokenMakerMaker tmm = getApplication();
                //ProcessOutputType type = stderr ? ProcessOutputType.STDERR :
                //	ProcessOutputType.STDOUT;
                ProcessOutputType type = ProcessOutputType.STDOUT;
                // JFlex always writes to stderr???
                tmm.getOutputPanel().appendOutput(line, type);
            });
		}

	}

    /**
     * Listens for compiler output.
     */
	private class CompilingOutputListener extends BaseOutputListener {

		private File dir;
		private String sourceFile;

		CompilingOutputListener(File dir, String sourceFile) {
			this.dir = dir;
			this.sourceFile = sourceFile;
		}

		@Override
		protected void processCompletedEdt(Process p, int rc, Throwable t) {

			super.processCompletedEdt(p, rc, t);
			TokenMakerMaker tmm = getApplication();

			if (rc==0) {
				String msg = "\n\n" + tmm.getString("Output.LaunchingTesterWindow");
				tmm.getOutputPanel().appendOutput(msg, ProcessOutputType.HEADER_INFO);
				String classFile = sourceFile.substring(0,
						sourceFile.lastIndexOf('.')) + ".class";
				try {
					TesterFrame tf = new TesterFrame(tmm, dir, classFile);
					tf.setVisible(true);
				} catch (Exception e) {
					getApplication().displayException(e);
				}
			}
			else {
				String error = "\n" + tmm.getString("Output.TerminalError");
				tmm.getOutputPanel().appendOutput(error, ProcessOutputType.TERMINAL_ERROR);
			}

		}

	}

    /**
     * Listens for JFlex output.
     */
	private class JFlexOutputListener extends BaseOutputListener {

		private File flexFile;

		JFlexOutputListener(File flexFile) {
			this.flexFile = flexFile;
		}

		@Override
		protected void processCompletedEdt(Process p, int rc, Throwable t) {
			super.processCompletedEdt(p, rc, t);
			if (rc==0) {
				File javaFile = Utils.getFileWithNewExtension(flexFile, "java");
				processGeneratedJavaSource(javaFile);
			}
			else {
				TokenMakerMaker tmm = getApplication();
				String error = "\n" + tmm.getString("Output.TerminalError");
				tmm.getOutputPanel().appendOutput(error, ProcessOutputType.TERMINAL_ERROR);
			}
		}

	}

	/**
	 * Runs a process.
	 */
	private class ProcessRunnerRunnable implements Runnable {

		private ProcessRunner pr;

		ProcessRunnerRunnable(ProcessRunner pr) {
			this.pr = pr;
		}

		public void run() {
			TokenMakerMaker tmm = getApplication();
			String cmd = tmm.getString("Output.RunningCommand", pr.getCommandLineString());
			tmm.getOutputPanel().appendOutput(cmd, ProcessOutputType.HEADER_INFO);
			pr.run();
		}

	}


//	/**
//	 * A file filter that identifies files whose names are of the format
//	 * "<code>rsyntaxtextarea-n.n.n.jar</code>" and
//	 * "<code>rsyntaxtextarea-n.n.n-SNAPSHOT.jar</code>".
//	 */
//	private static class RstaJarFilter implements FilenameFilter {
//
//		@Override
//		public boolean accept(File dir, String name) {
//			return name.matches("rsyntaxtextarea\\-([\\d\\.]+)(?:-SNAPSHOT)?\\.jar");
//		}
//
//	}


}
