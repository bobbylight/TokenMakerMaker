package org.fife.tmm;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.fife.ui.UIUtil;


/**
 * Base class for panels in the tabbed pane.
 *
 * @author Robert Futrell
 * @version 1.0
 */
abstract class TmmPanel {

	/**
	 * Client property set on the child <code>JPanel</code> to reference this
	 * <code>TmmPanel</code>.  This is a hack to make our code simpler in
	 * other places.
	 */
	public static final String PROPERTY_TMM_PANEL		= "tmmPanel";

	protected static final String DONT_ENABLE			= "DontEnable";

	protected TokenMakerMaker app;
	protected JPanel panel;


	public TmmPanel(TokenMakerMaker app) {
		this.app = app;
		panel = UIUtil.createTabbedPanePanel();
		panel.putClientProperty(PROPERTY_TMM_PANEL, this);
	}


	/**
	 * Sets the options in a {@link TokenMakerInfo} that this panel
	 * knows about.
	 *
	 * @param info The object to configure.
	 * @see #initializeFrom(TokenMakerInfo)
	 */
	public abstract void configureTokenMakerInfo(TokenMakerInfo info);


	/**
	 * Creates a non-opaque check box.
	 *
	 * @param text The text for the check box.
	 * @return The check box.
	 * @see #createCheckBox(String, boolean)
	 */
	protected JCheckBox createCheckBox(String text) {
		return createCheckBox(text, false);
	}


	/**
	 * Creates a non-opaque check box.
	 * 
	 * @param text The text for the check box.
	 * @param selected Whether it is initially selected.
	 * @return The check box.
	 * @see #createCheckBox(String)
	 */
	protected JCheckBox createCheckBox(String text, boolean selected) {
		JCheckBox cb = new JCheckBox(text, selected);
		cb.setOpaque(false);
		return cb;
	}


	/**
	 * Initializes this panel to display the the parameters as described
	 * in a {@link TokenMakerInfo}.
	 *
	 * @param info The object to initialize from.
	 * @see #configureTokenMakerInfo(TokenMakerInfo)
	 */
	public abstract void initializeFrom(TokenMakerInfo info);


	/**
	 * Verifies whether all the input in this panel is correct, and displays
	 * an error message if it isn't.
	 *
	 * @return Whether the input for this panel is okay.
	 */
	public abstract boolean verifyInput();


}