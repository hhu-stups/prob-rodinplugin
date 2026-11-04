package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Lets ProB compute a table (see {@link GetTableCommandsCommand}), using
 * <code>call_table_command_in_state/4</code>. The first row of a table are
 * the column headers.
 */
public final class CallTableCommand implements IComposableCommand {

	private static final String TABLE_VARIABLE = "Table";

	private final String stateId;
	private final String command;
	private final List<PrologTerm> formulas;
	private List<List<String>> rows = Collections.emptyList();

	/**
	 * @param formulas
	 *            the parsed expressions (raw ASTs) the command needs, see
	 *            {@link WriteDotCommand#parseExpression(Animator, String)}
	 */
	public CallTableCommand(final String stateId, final String command,
			final List<PrologTerm> formulas) {
		this.stateId = stateId;
		this.command = command;
		this.formulas = formulas;
	}

	public static List<List<String>> callTableCommand(final Animator animator,
			final String stateId, final String command,
			final List<PrologTerm> formulas) throws ProBException {
		final CallTableCommand call = new CallTableCommand(stateId, command,
				formulas);
		animator.execute(call);
		return call.getRows();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("call_table_command_in_state");
		pto.printAtomOrNumber(stateId);
		pto.printAtom(command);
		pto.openList();
		for (PrologTerm formula : formulas)
			pto.printTerm(formula);
		pto.closeList();
		pto.printVariable(TABLE_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		// list([list([Header1,...]),list([Cell1,...]),...])
		final List<List<String>> result = new ArrayList<List<String>>();
		for (PrologTerm row : elements(bindings.get(TABLE_VARIABLE))) {
			final List<String> cells = new ArrayList<String>();
			for (PrologTerm cell : elements(row))
				cells.add(GetTableCommandsCommand.text(cell));
			result.add(Collections.unmodifiableList(cells));
		}
		rows = Collections.unmodifiableList(result);
	}

	/** @return the elements of list(L) or of a Prolog list L */
	private static List<PrologTerm> elements(final PrologTerm term)
			throws CommandException {
		PrologTerm list = term;
		if (list.hasFunctor("list", 1))
			list = list.getArgument(1);
		if (!(list instanceof ListPrologTerm))
			throw new CommandException("ProB returned an unexpected table: "
					+ term);
		return (ListPrologTerm) list;
	}

	/** @return the rows; the first one has the column headers */
	public List<List<String>> getRows() {
		return rows;
	}
}
