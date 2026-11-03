package de.prob.core.command;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Gets the current values of all of ProB's preferences (including the
 * advanced ones), using <code>list_current_eclipse_preferences/1</code>.
 */
public final class GetCurrentPreferencesCommand implements IComposableCommand {

	private static final String PREFS_VARIABLE = "Prefs";

	private Map<String, String> values = Collections.emptyMap();

	public static Map<String, String> getCurrentPreferences(
			final Animator animator) throws ProBException {
		final GetCurrentPreferencesCommand command = new GetCurrentPreferencesCommand();
		animator.execute(command);
		return command.getValues();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("list_current_eclipse_preferences")
				.printVariable(PREFS_VARIABLE).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final Map<String, String> result = new TreeMap<String, String>();
		for (PrologTerm pref : (ListPrologTerm) bindings.get(PREFS_VARIABLE)) {
			// preference(Name,Value)
			if (pref.hasFunctor("preference", 2))
				result.put(PrologTerm.atomicString(pref.getArgument(1)),
						toString(pref.getArgument(2)));
		}
		values = Collections.unmodifiableMap(result);
	}

	private static String toString(final PrologTerm value) {
		return value.isAtomic() ? PrologTerm.atomicString(value) : value
				.toString();
	}

	/** @return the values of the preferences, sorted by name */
	public Map<String, String> getValues() {
		return values;
	}
}
