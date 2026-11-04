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
 * Gets the tables or graphs that ProB can compute in a state, using
 * <code>get_table_commands_in_state/2</code> or
 * <code>get_dot_commands_in_state/2</code>. They are computed by
 * {@link CallTableCommand} and {@link CallDotCommand}.
 */
public final class GetCommandsCommand implements IComposableCommand {

	private static final String LIST_VARIABLE = "Commands";

	public enum Category {
		/** tables, see {@link CallTableCommand} */
		TABLE("get_table_commands_in_state"),
		/** GraphViz graphs, see {@link CallDotCommand} */
		DOT("get_dot_commands_in_state");

		private final String predicate;

		private Category(final String predicate) {
			this.predicate = predicate;
		}
	}

	/** a table or graph that ProB can compute */
	public static final class CommandInfo {
		private final String command;
		private final String name;
		private final String description;
		private final int formulaArguments;
		private final String group;
		private final String preferredDotType;
		private final List<String> extraArguments;
		private final String unavailableReason;

		CommandInfo(final String command, final String name,
				final String description, final int formulaArguments,
				final String group, final String preferredDotType,
				final List<String> extraArguments,
				final String unavailableReason) {
			this.command = command;
			this.name = name;
			this.description = description;
			this.formulaArguments = formulaArguments;
			this.group = group;
			this.preferredDotType = preferredDotType;
			this.extraArguments = extraArguments;
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

		/** @return the number of formulas the command needs (0 or 1) */
		public int getFormulaArguments() {
			return formulaArguments;
		}

		/** @return the group, e.g. "machine_info", or "other" */
		public String getGroup() {
			return group;
		}

		/**
		 * @return the GraphViz layout program for a graph, e.g. "sfdp", or
		 *         <code>null</code> for the default (dot)
		 */
		public String getPreferredDotType() {
			return preferredDotType;
		}

		/** @return further arguments for GraphViz, e.g. -Goverlap=scale */
		public List<String> getExtraArguments() {
			return extraArguments;
		}

		public boolean isAvailable() {
			return unavailableReason == null;
		}

		/** @return why the command is unavailable, or <code>null</code> */
		public String getUnavailableReason() {
			return unavailableReason;
		}
	}

	private final Category category;
	private final String stateId;
	private List<CommandInfo> commands = Collections.emptyList();

	public GetCommandsCommand(final Category category, final String stateId) {
		this.category = category;
		this.stateId = stateId;
	}

	/**
	 * Does not notify the user if ProB fails: the exception's message has
	 * ProB's error messages, see {@link Animator#executeQuietly}.
	 */
	public static List<CommandInfo> getCommands(final Animator animator,
			final Category category, final String stateId)
			throws ProBException {
		final GetCommandsCommand command = new GetCommandsCommand(category,
				stateId);
		animator.executeQuietly(command);
		return command.getCommands();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(category.predicate);
		pto.printAtomOrNumber(stateId);
		pto.printVariable(LIST_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final List<CommandInfo> result = new ArrayList<CommandInfo>();
		for (PrologTerm term : (ListPrologTerm) bindings.get(LIST_VARIABLE)) {
			// command(Command,Name,Description,NumberOfFormulaArgs,
			// RelevantPrefs,AdditionalInfo,AvailMsg)
			if (!term.hasFunctor("command", 7))
				continue;
			final PrologTerm args = term.getArgument(4);
			final int formulaArguments = args instanceof AIntegerPrologTerm ? ((AIntegerPrologTerm) args)
					.intValue() : 0;
			String group = "other";
			String dotType = null;
			final List<String> extraArguments = new ArrayList<String>();
			final PrologTerm info = term.getArgument(6);
			if (info instanceof ListPrologTerm)
				for (PrologTerm i : (ListPrologTerm) info) {
					if (i.hasFunctor("group", 1))
						group = text(i.getArgument(1));
					else if (i.hasFunctor("preferred_dot_type", 1))
						dotType = text(i.getArgument(1));
					else if (i.hasFunctor("extra_arguments", 1)
							&& i.getArgument(1) instanceof ListPrologTerm)
						for (PrologTerm a : (ListPrologTerm) i.getArgument(1))
							extraArguments.add(text(a));
				}
			final String available = text(term.getArgument(7));
			result.add(new CommandInfo(text(term.getArgument(1)),
					text(term.getArgument(2)), text(term.getArgument(3)),
					formulaArguments, group, dotType,
					Collections.unmodifiableList(extraArguments),
					"available".equals(available) ? null : available));
		}
		commands = Collections.unmodifiableList(result);
	}

	/** @return an atom's text; other terms, e.g. numbers, as Prolog terms */
	static String text(final PrologTerm term) {
		return term.isAtom() ? PrologTerm.atomicString(term) : term.toString();
	}

	/** @return the commands in the order given by ProB */
	public List<CommandInfo> getCommands() {
		return commands;
	}
}
