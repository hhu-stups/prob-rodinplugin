/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.Animator;
import de.prob.core.domainobjects.ProBPreference;
import de.prob.exceptions.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetPreferencesCommand implements IComposableCommand {

	private static final String PREFS_VARIABLE = "Prefs";
	private List<ProBPreference> prefs;

	private GetPreferencesCommand() {
	}

	public static List<ProBPreference> getPreferences(final Animator animator)
			throws ProBException {
		GetPreferencesCommand getPreferencesCommand = new GetPreferencesCommand();
		animator.execute(getPreferencesCommand);
		return getPreferencesCommand.getPrefs();
	}

	private List<ProBPreference> getPrefs() {
		return prefs;
	}

	private ProBPreference verifyTerm(final PrologTerm term)
			throws CommandException {
		CompoundPrologTerm compoundTerm;
		try {
			compoundTerm = BindingGenerator.getCompoundTerm(term, "preference",
					5);
		} catch (ResultParserException e) {
			CommandException commandException = new CommandException(
					e.getLocalizedMessage(), e);
			commandException.notifyUserOnce();
			throw commandException;
		}
		return new ProBPreference(compoundTerm);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		ListPrologTerm p = (ListPrologTerm) bindings.get(PREFS_VARIABLE);
		prefs = new ArrayList<ProBPreference>();
		for (PrologTerm term : p) {
			ProBPreference preference = null;
			preference = verifyTerm(term);
			prefs.add(preference);
		}
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("list_eclipse_preferences").printVariable(PREFS_VARIABLE)
				.closeTerm();
	}
}
