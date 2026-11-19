/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */
package de.prob.core.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * A command to get all the stored errors of ProB's Prolog part as a list of
 * strings.
 * 
 * @author plagge
 */
public class GetErrorsCommand implements IComposableCommand {
	private static final String ERRORS_VARIABLE = "Errors";
	private static final String ONLY_WARNINGS_VARIABLE = "OnlyWarnings";
	private final boolean withSources;
	private List<String> errors;
	/** null if not asked for */
	private List<String> sources;
	private boolean onlyWarnings;

	public GetErrorsCommand() {
		this(false);
	}

	/**
	 * @param withSources
	 *            if ProB is also to tell the source of each message (e.g.
	 *            setup_constants_inconsistent), which needs a probcli with
	 *            getErrorMessagesWithSources/2
	 */
	public GetErrorsCommand(final boolean withSources) {
		this.withSources = withSources;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final ListPrologTerm list = (ListPrologTerm) bindings
				.get(ERRORS_VARIABLE);
		if (withSources) {
			final List<String> texts = new ArrayList<String>();
			final List<String> names = new ArrayList<String>();
			for (final PrologTerm term : list) {
				if (term.hasFunctor("msg", 2)) {
					names.add(term.getArgument(1).atomicToString());
					texts.add(term.getArgument(2).atomicToString());
				} else {
					names.add("");
					texts.add(term.atomicToString());
				}
			}
			errors = texts;
			sources = names;
		} else {
			errors = PrologTerm.atomicStrings(list);
		}
		onlyWarnings = "true".equals(bindings.get(ONLY_WARNINGS_VARIABLE)
				.getFunctor());
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(withSources ? "getErrorMessagesWithSources"
				: "getErrorMessages").printVariable(ONLY_WARNINGS_VARIABLE)
				.printVariable(ERRORS_VARIABLE).closeTerm();
	}

	public List<String> getErrors() {
		return errors;
	}

	/**
	 * @return the sources of the messages of {@link #getErrors()}, in the
	 *         same order, or null if not asked for
	 */
	public List<String> getSources() {
		return sources;
	}

	public boolean onlyWarningsOccurred() {
		return onlyWarnings;
	}

}
