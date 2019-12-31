package org.fife.tmm;

import org.fife.ui.app.MenuBar;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;


/**
 * The menu bar of the application.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class AppMenuBar extends MenuBar {

	private RecentFilesMenu historyMenu;

	/**
	 * Approximate maximum length, in pixels, of a File History entry.
	 * Note that this is only  GUIDELINE, and some filenames
	 * can (and will) exceed this limit.
	 */
	private static final int MAX_FILE_PATH_LENGTH = 250;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	AppMenuBar(TokenMakerMaker app, Prefs prefs) {

		ResourceBundle msg = app.getResourceBundle();

		String[] history = null;
		if (prefs.fileHistoryString!=null &&
				!prefs.fileHistoryString.isEmpty()) {
			history = prefs.fileHistoryString.split("<");
		}

		JMenu menu = createMenu(msg, "File");
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.OPEN_ACTION_KEY)));
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.SAVE_ACTION_KEY)));
		menu.addSeparator();
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.OPTIONS_ACTION_KEY)));
		menu.addSeparator();
		historyMenu = new RecentFilesMenu(app, history);
		menu.add(historyMenu);
		menu.addSeparator();
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.EXIT_ACTION_KEY)));
		add(menu);

		menu = createMenu(msg, "Help");
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.HELP_ACTION_KEY)));
		menu.addSeparator();
		menu.add(createMenuItem(app.getAction(TokenMakerMaker.ABOUT_ACTION_KEY)));
		add(menu);

	}


	/**
	 * Adds a file to the file history.
	 *
	 * @param file The file to add.
	 */
	public void addFileToFileHistory(File file) {
		historyMenu.addFileToFileHistory(file.getAbsolutePath());
	}


	/**
	 * Attempts to return an "attractive" shortened version of
	 * <code>fullPath</code>.  For example,
	 * <code>/home/lobster/dir1/dir2/dir3/dir4/file.out</code> could be
	 * abbreviated as <code>/home/lobster/dir1/.../file.out</code>.  Note that
	 * this method is still in the works, and isn't fully cooked yet.
	 */
	private String getDisplayPath(String longPath) {

		// Initialize some variables.
		FontMetrics fontMetrics = getFontMetrics(getFont());
		int textWidth = getTextWidth(longPath, fontMetrics);

		// If the text width is already short enough to fit, don't do anything to it.
		if (textWidth <= MAX_FILE_PATH_LENGTH) {
			return longPath;
		}

		// If it's too long, we'll have to trim it down some...

		// Will be '\' for Windows, '/' for Unix and derivatives.
		String separator = System.getProperty("file.separator");

		// What we will eventually return.
		String displayString = longPath;

		// If there is no directory separator, then the string is just a file name,
		// and so we can't shorten it.  Just return the sucker.
		int lastSeparatorPos = displayString.lastIndexOf(separator);
		if (lastSeparatorPos==-1) {
            return displayString;
        }

		// Get the length of just the file name.
		String justFileName = displayString.substring(
						lastSeparatorPos+1);
		int justFileNameLength = getTextWidth(justFileName, fontMetrics);

		// If even just the file name is too long, return it.
		if (justFileNameLength > MAX_FILE_PATH_LENGTH) {
            return "..." + separator + justFileName;
        }

		// Otherwise, just keep adding levels in the directory hierarchy
		// until the name gets too long.
		String endPiece = "..." + separator + justFileName;
		int endPieceLength = getTextWidth(endPiece, fontMetrics);
		int separatorPos = displayString.indexOf(separator);
		String firstPart = displayString.substring(0, separatorPos+1);
		int firstPartLength = getTextWidth(firstPart, fontMetrics);
		String tempFirstPart = firstPart;
		int tempFirstPartLength = firstPartLength;
		while (tempFirstPartLength+endPieceLength < MAX_FILE_PATH_LENGTH) {
			firstPart  = tempFirstPart;
			separatorPos = displayString.indexOf(separator, separatorPos+1);
			if (separatorPos==-1) {
                endPieceLength = 9999999;
            }
			else {
				tempFirstPart = displayString.substring(0, separatorPos+1);
				tempFirstPartLength = getTextWidth(tempFirstPart, fontMetrics);
			}
		}

		return firstPart+endPiece;

	}


	/**
	 * Returns a string representing all files in the file history separated by
	 * '<' characters.  This character was chosen as the separator because it
	 * is a character that cannot be used in filenames in both Windows and
	 * UNIX/Linux.
	 *
	 * @return A <code>String</code> representing all files in the file
	 *         history, separated by '<' characters.  If no files are in the
	 *         file history, then <code>null</code> is returned.
	 */
	public String getFileHistoryString() {
		StringBuilder retVal = new StringBuilder();
		int historyCount = historyMenu.getItemCount();
		for (int i=historyCount-1; i>=0; i--) {
			retVal.append(historyMenu.getFileFullPath(i)).append("<");
		}
		if (retVal.length()>0) {
            retVal = new StringBuilder(retVal.substring(0, retVal.length() - 1)); // Remove trailing '>'.
        }
		return retVal.toString();
	}


	/**
	 * Determines the width of the given <code>String</code> containing no
	 * tabs.  Note that this is simply a trimmed-down version of
	 * <code>javax.swing.text.getTextWidth</code> that has been
	 * optimized for our use.
	 *
	 * @param s  the source of the text
	 * @param metrics the font metrics to use for the calculation
	 * @return  the width of the text
	 */
	private static int getTextWidth(String s, FontMetrics metrics) {
		int textWidth = 0;
		char[] txt = s.toCharArray();
		for (char c : txt) {
			// Ignore newlines, they take up space and we shouldn't be
			// counting them.
			if (c != '\n') {
				textWidth += metrics.charWidth(c);
			}
		}
		return textWidth;
	}


	/**
	 * The "recent files" submenu.
	 */
	private class RecentFilesMenu extends org.fife.ui.RecentFilesMenu {

		private TokenMakerMaker app;

		RecentFilesMenu(TokenMakerMaker app, String[] history) {
			super(app.getString("RecentFiles"), history);
			setMnemonic(app.getString("RecentFiles.Mnemonic").charAt(0));
			this.app = app;
		}

		@Override
		protected Action createOpenAction(final String fileFullPath) {
			return new AbstractAction(getDisplayPath(fileFullPath)) {
				@Override
				public void actionPerformed(ActionEvent e) {
					app.openFile(new File(fileFullPath));
				}
			};
		}

	}


}
