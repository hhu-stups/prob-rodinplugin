/**
 * 
 */
package de.prob.ui.ltl;

import org.eclipse.osgi.util.NLS;

/**
 * @author plagge
 * 
 */
public final class LtlStrings extends NLS {
	public static String ltlLoopAdvice;

	public static String ltlHelpText;

	public static String ltlResultIncompleteTitle;
	public static String ltlResultIncompleteMessage;
	public static String ltlResultOkTitle;
	public static String ltlResultOkMessage;
	public static String ltlResultCounterexampleTitle;
	public static String ltlResultCounterexampleMessage;
	public static String ltlResultNoStartTitle;
	public static String ltlResultNoStartMessage;

	static {
		final Class<LtlStrings> clazz = LtlStrings.class;
		initializeMessages(clazz.getName(), clazz);
	}

}
