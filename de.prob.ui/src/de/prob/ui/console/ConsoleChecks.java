/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eventb.core.IEventBRoot;

import de.be4.ltl.core.parser.LtlParseException;
import de.be4.ltl.core.parser.LtlParser;
import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.ConstraintBasedAssertionCheckCommand;
import de.prob.core.command.ConstraintBasedDeadlockCheckCommand;
import de.prob.core.command.ConstraintBasedDynamicAssertionCheckCommand;
import de.prob.core.command.ConstraintBasedInvariantCheckCommand;
import de.prob.core.command.ConstraintBasedInvariantCheckCommand.InvariantCheckCounterExample;
import de.prob.core.command.ConstraintBasedRefinementCheckCommand;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.FindValidStateCommand;
import de.prob.core.command.LtlCheckingCommand;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.term.PrologTerm;
import de.prob.ui.console.ConsoleEvaluator.Kind;
import de.prob.ui.console.ConsoleEvaluator.Result;
import de.prob.ui.ltl.CounterExampleViewPart;
import de.prob.ui.ltl.LtlErrors;
import de.prob.ui.settings.ModelSettings;
import de.prob.logging.Logger;
import de.prob.unicode.UnicodeTranslator;

/**
 * The console's ":ltl FORMULA" (like LTL Model Checking in the ProB menu)
 * and ":cbc CHECK" (like the Constraint Based Checking menu). They run in the
 * console's job, so its Cancel button interrupts ProB; the results are shown
 * in the console instead of dialogs, a counter-example as by the menus.
 */
final class ConsoleChecks {

	/** the checks of ":cbc", with their descriptions, in menu order */
	static final Map<String, String> CBC_CHECKS = new LinkedHashMap<String, String>();
	static {
		CBC_CHECKS.put("deadlock",
				"[PRED]  Deadlock Freedom: a deadlocking state satisfying the invariant (and PRED)");
		CBC_CHECKS.put("invariant",
				"[EVENT ...]  Invariant Preservation: an event (by default each) violating the invariant");
		CBC_CHECKS.put("refinement",
				"Refinement Proof Obligations: an event violating its refinement POs");
		CBC_CHECKS.put("context-theorems",
				"Context Theorems: a state satisfying the axioms but not all theorems");
		CBC_CHECKS.put("invariant-theorems",
				"Invariant Theorems: a state satisfying the invariant but not all theorems");
		CBC_CHECKS.put("find-state",
				"[PRED]  Find State satisfying Predicate: a state satisfying the invariant and PRED");
	}

	private ConsoleChecks() {
	}

	/**
	 * ":ltl FORMULA": LTL model checking from the initial states, with
	 * ProB's current symmetry setting; a counter-example is shown in the LTL
	 * Counter-Example view
	 */
	static Result ltl(final Animator animator, final String formula,
			final IProgressMonitor monitor) throws ProBException {
		if (formula.isEmpty())
			return listLtlFormulas();
		final PrologTerm parsed;
		try {
			parsed = new LtlParser(animator.getLanguageDependendPart())
					.generatePrologTerm(formula, "root");
		} catch (LtlParseException e) {
			return new Result(Kind.ERROR, LtlErrors.describe(animator, formula, e, true));
		}
		saveLtlFormula(formula);
		LtlCheckingCommand.Result result;
		do {
			// -1: no maximum number of states, as in LtlCheckingJob
			result = LtlCheckingCommand.modelCheck(animator, parsed, -1,
					LtlCheckingCommand.StartMode.init);
		} while (!result.isAbort()
				&& (monitor == null || !monitor.isCanceled()));
		switch (result.getStatus()) {
		case ok:
			return new Result(Kind.TRUE,
					"The LTL formula holds: no counter-example, all states were visited.");
		case counterexample:
			showCounterExample(result);
			return new Result(Kind.FALSE,
					"Counter-example found: the LTL formula does not hold. It is shown in the LTL Counter-Example view.");
		case incomplete:
			return new Result(Kind.UNKNOWN,
					monitor != null && monitor.isCanceled() ? "Cancelled: no counter-example found so far."
							: "No counter-example found, but not all states were visited (due to the animation settings).");
		case nostart:
			return new Result(Kind.UNKNOWN,
					"No initialised state to start from: ProB could not initialise the machine. Try to animate the model first.");
		case typeerror:
			return new Result(Kind.ERROR,
					"Type error in the LTL formula.");
		default:
			return new Result(Kind.ERROR, "Unexpected LTL result: "
					+ result.getStatus());
		}
	}

	/**
	 * saves the formula in the model's settings file, shared with the LTL
	 * Model Checking dialog
	 */
	private static void saveLtlFormula(final String formula) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				final IEventBRoot model = ModelSettings.animatedModel();
				if (model == null)
					return;
				try {
					ModelSettings.add(model, ModelSettings.Kind.LTL, formula,
							null);
				} catch (CoreException e) {
					Logger.info("Could not save the LTL formula: "
							+ e.getMessage());
				}
			}
		});
	}

	/** @return the LTL formulas saved for the animated model, newest first */
	static List<String> savedLtlFormulas() {
		final List<String> result = new ArrayList<String>();
		final IEventBRoot model = ModelSettings.animatedModel();
		if (model == null)
			return result;
		final List<ModelSettings.SavedFormula> saved = ModelSettings.formulas(
				model, ModelSettings.Kind.LTL);
		for (int i = saved.size() - 1; i >= 0; i--)
			if (!result.contains(saved.get(i).formula))
				result.add(saved.get(i).formula);
		return result;
	}

	/** ":ltl" without a formula: the usage and the saved formulas */
	private static Result listLtlFormulas() {
		final List<String> saved = savedLtlFormulas();
		final StringBuilder sb = new StringBuilder(
				"Usage: :ltl FORMULA, e.g. :ltl G {x>0} or :ltl GF [inc]");
		if (saved.isEmpty())
			sb.append("\nNo LTL formulas saved for this model yet.");
		else {
			sb.append("\nSaved LTL formulas (Tab after :ltl inserts them):");
			for (String f : saved)
				sb.append("\n  ").append(f);
		}
		return new Result(Kind.VALUE, sb.toString());
	}

	private static void showCounterExample(
			final LtlCheckingCommand.Result result) {
		final CounterExample counterExample = new CounterExample(result);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				final CounterExampleViewPart view = CounterExampleViewPart
						.showDefault();
				if (view != null)
					view.addCounterExample(counterExample);
			}
		});
	}

	/** ":cbc CHECK [ARGUMENTS]" */
	static Result cbc(final Animator animator, final String argument)
			throws ProBException {
		if (argument.isEmpty())
			return new Result(Kind.VALUE, "Usage: :cbc CHECK, with CHECK:\n"
					+ describeChecks());
		final int space = argument.indexOf(' ');
		final String check = (space < 0 ? argument : argument.substring(0,
				space)).toLowerCase();
		final String rest = space < 0 ? "" : argument.substring(space + 1)
				.trim();
		switch (check) {
		case "deadlock":
			return deadlock(animator, rest);
		case "invariant":
			return invariant(animator, rest);
		case "refinement":
			return refinement(animator);
		case "context-theorems":
			return contextTheorems(animator);
		case "invariant-theorems":
			return invariantTheorems(animator);
		case "find-state":
			return findState(animator, rest);
		default:
			return new Result(Kind.ERROR, "Unknown check " + check
					+ ", available:\n" + describeChecks());
		}
	}

	static String describeChecks() {
		final StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> e : CBC_CHECKS.entrySet())
			sb.append("  ").append(e.getKey()).append(' ')
					.append(e.getValue()).append('\n');
		return sb.toString().replaceFirst("\n$", "");
	}

	/** null for an empty predicate */
	private static PrologTerm parsePredicate(final Animator animator,
			final String predicate) throws ProBParseException {
		if (predicate.isEmpty())
			return null;
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		return new ProBParserBaseAdapter(ldp).parsePredicate(predicate, false);
	}

	/** like the menus: the state is shown after the root state */
	private static void show(final Animator animator,
			final Operation... steps) throws ProBException {
		animator.getHistory().gotoPos(0);
		for (Operation op : steps)
			ExecuteOperationCommand.executeOperation(animator, op);
	}

	private static String stateNote(final Animator animator) {
		return animator.getCurrentState() == null ? "" : " (state "
				+ animator.getCurrentState().getId() + ")";
	}

	private static Result deadlock(final Animator animator,
			final String predicate) throws ProBException {
		final PrologTerm pred;
		try {
			pred = parsePredicate(animator, predicate);
		} catch (ProBParseException e) {
			return new Result(Kind.ERROR, e.getMessage());
		}
		final ConstraintBasedDeadlockCheckCommand command = new ConstraintBasedDeadlockCheckCommand(
				pred);
		animator.execute(command);
		if (command.getResult() == null)
			return new Result(Kind.ERROR, "ProB did not return a result.");
		switch (command.getResult()) {
		case NO_DEADLOCK:
			return new Result(Kind.TRUE,
					"No deadlock: no deadlocking state satisfies the invariant"
							+ (pred == null ? "" : " and the predicate")
							+ " (for the current sizes of deferred sets).");
		case DEADLOCK_FOUND:
			show(animator, command.getDeadlockOperation());
			return new Result(Kind.FALSE,
					"Deadlock found: a deadlocking state satisfying the invariant, now the current state"
							+ stateNote(animator)
							+ ". It may not be reachable from the initialisation.");
		case INTERRUPTED:
			return new Result(Kind.UNKNOWN,
					"Interrupted (by the user or a time-out).");
		default:
			return new Result(Kind.ERROR,
					"An error occurred while type checking the predicate.");
		}
	}

	private static Result invariant(final Animator animator,
			final String eventNames) throws ProBException {
		Collection<String> events = null;
		if (!eventNames.isEmpty()) {
			events = new ArrayList<String>(Arrays.asList(eventNames
					.split("[\\s,]+")));
			final MachineDescription md = animator.getMachineDescription();
			if (md != null) {
				final List<String> unknown = new ArrayList<String>(events);
				unknown.removeAll(md.getEventNames());
				if (!unknown.isEmpty())
					return new Result(Kind.ERROR, "Unknown event(s): "
							+ String.join(", ", unknown) + "; events: "
							+ String.join(", ", md.getEventNames()));
			}
		}
		final ConstraintBasedInvariantCheckCommand command = new ConstraintBasedInvariantCheckCommand(
				events);
		animator.execute(command);
		if (command.getResult() == null)
			return new Result(Kind.ERROR, "ProB did not return a result.");
		switch (command.getResult()) {
		case NO_VIOLATION_FOUND:
			return new Result(Kind.TRUE, "No invariant violation found"
					+ (events == null ? "." : " for " + String.join(", ",
							events) + "."));
		case VIOLATION_FOUND:
			final List<String> violating = new ArrayList<String>();
			for (InvariantCheckCounterExample c : command.getCounterExamples())
				if (!violating.contains(c.getEventName()))
					violating.add(c.getEventName());
			final InvariantCheckCounterExample first = command
					.getCounterExamples().iterator().next();
			show(animator, first.getStep1(), first.getStep2());
			return new Result(Kind.FALSE, "Invariant violation found, by: "
					+ String.join(", ", violating)
					+ ".\nThe first one is shown in the history: "
					+ first.getEventName() + " leads to the current state"
					+ stateNote(animator) + ", which violates the invariant.");
		default:
			return new Result(Kind.UNKNOWN,
					"Interrupted (by the user or a time-out).");
		}
	}

	private static Result refinement(final Animator animator)
			throws ProBException {
		final ConstraintBasedRefinementCheckCommand command = new ConstraintBasedRefinementCheckCommand();
		animator.execute(command);
		if (command.getResult() == null)
			return new Result(Kind.ERROR, "ProB did not return a result.");
		switch (command.getResult()) {
		case NO_VIOLATION_FOUND:
			return new Result(Kind.TRUE, "No refinement violation found.");
		case VIOLATION_FOUND:
			return new Result(Kind.FALSE, "Refinement violation found:\n"
					+ UnicodeTranslator.toUnicode(command.getResultsString()));
		default:
			return new Result(Kind.UNKNOWN,
					"Interrupted (by the user or a time-out).");
		}
	}

	private static Result contextTheorems(final Animator animator)
			throws ProBException {
		final ConstraintBasedAssertionCheckCommand command = new ConstraintBasedAssertionCheckCommand();
		animator.execute(command);
		if (command.getResult() == null)
			return new Result(Kind.ERROR, "ProB did not return a result.");
		switch (command.getResult()) {
		case NO_COUNTER_EXAMPLE_EXISTS:
			return new Result(Kind.TRUE,
					"No counter-example to the context theorems exists (for the current sizes of deferred sets).");
		case NO_COUNTER_EXAMPLE_FOUND:
			return new Result(Kind.UNKNOWN,
					"No counter-example to the context theorems was found, but one may exist.");
		case COUNTER_EXAMPLE:
			show(animator, command.getCounterExampleOperation());
			return new Result(Kind.FALSE,
					"Counter-example found: a state satisfying the axioms but not all context theorems, now the current state"
							+ stateNote(animator) + ".");
		default:
			return new Result(Kind.UNKNOWN,
					"Interrupted (by the user or a time-out).");
		}
	}

	private static Result invariantTheorems(final Animator animator)
			throws ProBException {
		final ConstraintBasedDynamicAssertionCheckCommand command = new ConstraintBasedDynamicAssertionCheckCommand();
		animator.execute(command);
		if (command.getResult() == null)
			return new Result(Kind.ERROR, "ProB did not return a result.");
		switch (command.getResult()) {
		case NO_COUNTER_EXAMPLE_EXISTS:
			return new Result(Kind.TRUE,
					"No counter-example to the invariant theorems exists (for the current sizes of deferred sets).");
		case NO_COUNTER_EXAMPLE_FOUND:
			return new Result(Kind.UNKNOWN,
					"No counter-example to the invariant theorems was found, but one may exist.");
		case COUNTER_EXAMPLE:
			show(animator, command.getCounterExampleOperation());
			return new Result(Kind.FALSE,
					"Counter-example found: a state satisfying the invariant but not all invariant theorems, now the current state"
							+ stateNote(animator)
							+ ". It may not be reachable from the initialisation.");
		default:
			return new Result(Kind.UNKNOWN,
					"Interrupted (by the user or a time-out).");
		}
	}

	private static Result findState(final Animator animator,
			final String predicate) throws ProBException {
		final PrologTerm pred;
		try {
			// as in the menu's dialog, 1=1 if there is none
			pred = parsePredicate(animator, predicate.isEmpty() ? "1=1"
					: predicate);
		} catch (ProBParseException e) {
			return new Result(Kind.ERROR, e.getMessage());
		}
		final FindValidStateCommand command = new FindValidStateCommand(pred);
		animator.execute(command);
		if (command.getResult() == null)
			return new Result(Kind.ERROR, "ProB did not return a result.");
		switch (command.getResult()) {
		case STATE_FOUND:
			show(animator, command.getOperation());
			return new Result(Kind.TRUE,
					"Found a state satisfying the invariant and the predicate, now the current state"
							+ stateNote(animator) + ".");
		case NO_STATE_FOUND:
			return new Result(Kind.FALSE,
					"No state satisfying both the invariant and the predicate was found.");
		case INTERRUPTED:
			return new Result(Kind.UNKNOWN,
					"Interrupted (by the user or a time-out).");
		default:
			return new Result(Kind.ERROR,
					"An error occurred while type checking the predicate.");
		}
	}
}
