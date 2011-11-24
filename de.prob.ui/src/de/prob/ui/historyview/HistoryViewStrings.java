/**
 * 
 */
package de.prob.ui.historyview;

import org.eclipse.osgi.util.NLS;

/**
 * Natural language strings for the history view
 * 
 * @author plagge
 */
public class HistoryViewStrings extends NLS {
	public static String uninitialisedState;

	static {
		final Class<HistoryViewStrings> clazz = HistoryViewStrings.class;
		initializeMessages(clazz.getName(), clazz);
	}

}
