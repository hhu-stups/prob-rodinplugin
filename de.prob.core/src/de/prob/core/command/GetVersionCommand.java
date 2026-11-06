package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Gets the version of probcli, using <code>get_version/7</code>: its
 * version number, Git revision, last change date and Prolog version.
 */
public final class GetVersionCommand implements IComposableCommand {

	private static final String[] VARIABLES = { "Major", "Minor", "Service",
			"Qualifier", "Revision", "LastChanged", "Prolog" };

	private String version;
	private String revision;
	private String lastChanged;
	private String prolog;

	public static GetVersionCommand getVersion(final Animator animator)
			throws ProBException {
		final GetVersionCommand command = new GetVersionCommand();
		animator.execute(command);
		return command;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_version");
		for (String variable : VARIABLES)
			pto.printVariable(variable);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final String qualifier = toString(bindings.get("Qualifier"));
		version = toString(bindings.get("Major")) + "."
				+ toString(bindings.get("Minor")) + "."
				+ toString(bindings.get("Service"))
				+ (qualifier.isEmpty() ? "" : "-" + qualifier);
		revision = toString(bindings.get("Revision"));
		lastChanged = toString(bindings.get("LastChanged"));
		prolog = toString(bindings.get("Prolog"));
	}

	// atomicString only accepts atoms, not numbers
	private static String toString(final PrologTerm value) {
		return value.isAtom() ? PrologTerm.atomicString(value) : value
				.toString();
	}

	/** @return e.g. 1.15.0-beta1 */
	public String getVersion() {
		return version;
	}

	/** @return the Git revision */
	public String getRevision() {
		return revision;
	}

	/** @return the date of the last change of ProB's sources */
	public String getLastChanged() {
		return lastChanged;
	}

	/** @return e.g. SICStus 4.9.0 (...) */
	public String getProlog() {
		return prolog;
	}
}
