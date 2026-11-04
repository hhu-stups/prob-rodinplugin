package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.AIntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Gets the tables that ProB can compute in a state, using
 * <code>get_table_commands_in_state/2</code>. The tables are computed by
 * {@link CallTableCommand}.
 */
public final class GetTableCommandsCommand implements IComposableCommand {

	private static final String LIST_VARIABLE = "Commands";

	/** a table that ProB can compute */
	public static final class TableCommand {
		private final String command;
		private final String name;
		private final String description;
		private final int formulaArguments;
		private final String group;
		private final String unavailableReason;

		TableCommand(final String command, final String name,
				final String description, final int formulaArguments,
				final String group, final String unavailableReason) {
			this.command = command;
			this.name = name;
			this.description = description;
			this.formulaArguments = formulaArguments;
			this.group = group;
			this.unavailableReason = unavailableReason;
		}

		/** @return the name of the command for ProB */
		public String getCommand() {
			return command;
		}

		/** @return the name for the user, e.g. "Machine Statistics" */
		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		/** @return the number of expressions the command needs (0 or 1) */
		public int getFormulaArguments() {
			return formulaArguments;
		}

		/** @return the group, e.g. "machine_info", or "other" */
		public String getGroup() {
			return group;
		}

		public boolean isAvailable() {
			return unavailableReason == null;
		}

		/** @return why the command is unavailable, or <code>null</code> */
		public String getUnavailableReason() {
			return unavailableReason;
		}
	}

	private final String stateId;
	private List<TableCommand> commands = Collections.emptyList();

	public GetTableCommandsCommand(final String stateId) {
		this.stateId = stateId;
	}

	public static List<TableCommand> getTableCommands(
			final Animator animator, final String stateId)
			throws ProBException {
		final GetTableCommandsCommand command = new GetTableCommandsCommand(
				stateId);
		animator.execute(command);
		return command.getCommands();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_table_commands_in_state");
		pto.printAtomOrNumber(stateId);
		pto.printVariable(LIST_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final List<TableCommand> result = new ArrayList<TableCommand>();
		for (PrologTerm term : (ListPrologTerm) bindings.get(LIST_VARIABLE)) {
			// command(Command,Name,Description,NumberOfFormulaArgs,
			// RelevantPrefs,AdditionalInfo,AvailMsg)
			if (!term.hasFunctor("command", 7))
				continue;
			final PrologTerm args = term.getArgument(4);
			final int formulaArguments = args instanceof AIntegerPrologTerm ? ((AIntegerPrologTerm) args)
					.intValue() : 0;
			String group = "other";
			final PrologTerm info = term.getArgument(6);
			if (info instanceof ListPrologTerm)
				for (PrologTerm i : (ListPrologTerm) info)
					if (i.hasFunctor("group", 1))
						group = text(i.getArgument(1));
			final String available = text(term.getArgument(7));
			result.add(new TableCommand(text(term.getArgument(1)),
					text(term.getArgument(2)), text(term.getArgument(3)),
					formulaArguments, group,
					"available".equals(available) ? null : available));
		}
		commands = Collections.unmodifiableList(result);
	}

	/** @return an atom's text; other terms, e.g. numbers, as Prolog terms */
	static String text(final PrologTerm term) {
		return term.isAtom() ? PrologTerm.atomicString(term) : term.toString();
	}

	/** @return the tables in the order given by ProB */
	public List<TableCommand> getCommands() {
		return commands;
	}
}
