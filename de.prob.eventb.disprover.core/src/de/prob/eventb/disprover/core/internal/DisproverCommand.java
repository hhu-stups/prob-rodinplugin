package de.prob.eventb.disprover.core.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.be4.classicalb.core.parser.node.AEventBModelParseUnit;
import de.prob.core.Animator;
import de.prob.core.command.ClearMachineCommand;
import de.prob.core.command.CommandException;
import de.prob.core.command.ComposedCommand;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.ExploreStateCommand;
import de.prob.core.command.IComposableCommand;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.core.command.SetPreferenceCommand;
import de.prob.core.command.SetPreferencesCommand;
import de.prob.core.command.StartAnimationCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.Operation.EventType;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.Variable;
import de.prob.eventb.disprover.core.DisproverReasonerInput;
import de.prob.eventb.disprover.core.ICounterExample;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * The DisproverCommand takes two sets of ASTs (one for the machine and a list
 * for the contexts) and tries to set them up with ProB. If setup is possible,
 * the arguments from that operation are joined with the provided variables and
 * returned as an {@link ICounterExample}.
 * <p>
 * 
 * This command is probably not useful without {@link DisproverReasoner}, which
 * calls it.
 * 
 * @author jastram
 */
public class DisproverCommand implements IComposableCommand {

	/**
	 * Lifted from {@link LoadEventBModelCommand}
	 */

	// private final AEventBModelParseUnit machineAst;
	// private final List<AEventBContextParseUnit> contextAsts;
	private final List<DisproverIdentifier> vars;
	private final ComposedCommand allCommands;
	private static Animator animator;
	private final ExploreStateCommand exploreStateCommand;
	private CounterExample counterExample;

	public DisproverCommand(final AEventBModelParseUnit machineAst,
			final List<AEventBContextParseUnit> contextParseUnits,
			final List<DisproverIdentifier> vars,
			final DisproverReasonerInput input) {
		this.vars = vars;

		List<IComposableCommand> cmds = new LinkedList<IComposableCommand>();
		cmds.add(new ClearMachineCommand());
		cmds.add(new SetPreferencesCommand());
		cmds.add(new SetPreferenceCommand("MAX_OPERATIONS", "1"));
		// Overwrite the preferences, if requested
		if (input.useDisproverPrefs()) {
			cmds.add(new SetPreferenceCommand("MAXINT", String.valueOf(input
					.getMaxInt())));
			cmds.add(new SetPreferenceCommand("MININT", String.valueOf(input
					.getMinInt())));
			cmds.add(new SetPreferenceCommand("DEFAULT_SETSIZE", String
					.valueOf(input.getSetSize())));
			cmds.add(new SetPreferenceCommand("TIME_OUT", String.valueOf(input
					.getTimeout())));
		}
		cmds.add(new DisproverLoadCommand(machineAst, contextParseUnits));
		cmds.add(new StartAnimationCommand());
		exploreStateCommand = new ExploreStateCommand("root");
		cmds.add(exploreStateCommand);
		allCommands = new ComposedCommand(cmds);
	}

	/**
	 * For convenience, a static method for accessing the DisproverCommand.
	 */
	public static ICounterExample disprove(final Animator animator,
			final AEventBModelParseUnit machineAst,
			final List<AEventBContextParseUnit> contextParseUnits,
			final List<DisproverIdentifier> vars,
			final DisproverReasonerInput input) throws ProBException {
		DisproverCommand.animator = animator;
		DisproverCommand cmd = new DisproverCommand(machineAst,
				contextParseUnits, vars, input);
		animator.execute(cmd);
		return cmd.getResult();
	}

	private ICounterExample getResult() {
		return counterExample;
	}

	public void writeCommand(final IPrologTermOutput pto) {
		try {
			allCommands.writeCommand(pto);
		} catch (CommandException e) {
			e.printStackTrace();
		}
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		State state = exploreStateCommand.getState();
		animator.announceCurrentStateChanged(state, Operation.NULL_OPERATION);
		List<Operation> ops = state.getEnabledOperations();
		final Operation op = ops.isEmpty() ? null : ops.get(0);
		// Case 1: Counterexample found
		if (op != null && EventType.SETUP_CONTEXT.equals(op.getEventType())) {
			// setup the constants
			try {
				ExecuteOperationCommand.executeOperation(animator, ops.get(0));
			} catch (ProBException e) {
				throw new CommandException(e.getMessage(), e);
			}
			Map<String, Variable> values = animator.getCurrentState()
					.getValues();

			counterExample = new CounterExample(true);
			for (DisproverIdentifier var : vars) {
				if (var.getType() != null) {
					Variable variable = values.get(var.getName());
					counterExample.addVar(var.getName(),
							variable == null ? null : variable.getValue());
				}
			}
			Logger.info("Counter-Example: " + counterExample);
		}

		// Case 2: Timeout
		if (animator.getCurrentState().isTimeoutOccured())
			throw new CommandException("Disprover Time-Out");

		// Case 3: No Counterexample found
		counterExample = new CounterExample(false);
	}
}
