package de.prob.core.command;

import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * @author Jens Bendisposto
 * 
 */
public class GetPrintableAtomsList implements IComposableCommand {

	private static final String PROLOG_VARIABLE = "L";
	private final String prologPredicate;
	private List<String> list;

	/**
	 * Executes the query: prologPredicate(L). Expects L to be a list of
	 * printable atoms
	 * 
	 * @param animator
	 * @param prologPredicate
	 * @return
	 * @throws ProBException
	 */

	public static List<String> getList(final Animator animator,
			final String prologPredicate) throws ProBException {
		GetPrintableAtomsList cmd = new GetPrintableAtomsList(prologPredicate);
		animator.execute(cmd);
		return cmd.getList();
	}

	private List<String> getList() {
		return list;
	}

	private GetPrintableAtomsList(final String prologPredicate) {
		this.prologPredicate = prologPredicate;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		list = PrologTerm.atomicStrings((ListPrologTerm) bindings
				.get(PROLOG_VARIABLE));
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(prologPredicate);
		pto.printVariable(PROLOG_VARIABLE);
		pto.closeTerm();
	}

}
