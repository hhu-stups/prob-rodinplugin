package de.prob.core.command;

import java.io.File;
import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Lets ProB write a graph (see {@link GetCommandsCommand}) as a GraphViz dot
 * file, using <code>call_dot_command_in_state/4</code>.
 */
public final class CallDotCommand implements IComposableCommand {

	private final String stateId;
	private final String command;
	private final List<PrologTerm> formulas;
	private final File file;

	/**
	 * @param formulas
	 *            the parsed formulas (raw ASTs) the command needs, see
	 *            {@link CommandFormulas}
	 */
	public CallDotCommand(final String stateId, final String command,
			final List<PrologTerm> formulas, final File file) {
		this.stateId = stateId;
		this.command = command;
		this.formulas = formulas;
		this.file = file;
	}

	public static void writeDot(final Animator animator, final String stateId,
			final String command, final List<PrologTerm> formulas,
			final File file) throws ProBException {
		animator.execute(new CallDotCommand(stateId, command, formulas, file));
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("call_dot_command_in_state");
		pto.printAtomOrNumber(stateId);
		pto.printAtom(command);
		pto.openList();
		for (PrologTerm formula : formulas)
			pto.printTerm(formula);
		pto.closeList();
		pto.printAtom(file.getAbsolutePath());
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// the result is the file
	}
}
