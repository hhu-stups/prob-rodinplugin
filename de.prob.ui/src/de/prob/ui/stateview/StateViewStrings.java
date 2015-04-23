/**
 * 
 */
package de.prob.ui.stateview;

import org.eclipse.osgi.util.NLS;

/**
 * Natural Language Strings for the state view
 * 
 * @author plagge
 */
public class StateViewStrings extends NLS {
	public static String signalInvariantTooltip;
	public static String signalInvariantGood;
	public static String signalInvariantBad;

	public static String signalEventerrorTooltip;
	public static String signalEventerrorGood;
	public static String signalEventerrorBad;

	public static String signalModelmodifiedTooltip;
	public static String signalModelmodifiedBad;

	public static String signalTimeoutTooltip;
	public static String signalTimeoutBad;
	public static String signalTimeoutMaxReached;

	public static String columnHeaderName;
	public static String columnHeaderCurrentvalue;
	public static String columnHeaderPreviousvalue;

	public static String errorShowEventerrors;

	public static String menuShowMultipleVariables;

	public static String menuEnterNewFormula;
	public static String dialogTitleEnterNewFormula;
	public static String dialogMessageEnterNewFormula;
	public static String dialogSyntaxError;

	public static String formulasSectionLabel;

	public static String menuReloadAnimation;

	static {
		final Class<StateViewStrings> clazz = StateViewStrings.class;
		initializeMessages(clazz.getName(), clazz);
	}
}
