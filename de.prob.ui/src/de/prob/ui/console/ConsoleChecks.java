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
import org.eclipse.swt.widgets.Display;

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
			return new Result(Kind.ERROR,
					"Usage: :ltl FORMULA, e.g. :ltl G {x>0} or :ltl GF [inc]");
		final PrologTerm parsed;
		try {
			parsed = new LtlParser(animator.getLanguageDependendPart())
					.generatePrologTerm(formula, "root");
		} catch (LtlParseException e) {
			return new Result(Kind.ERROR, describeLtlError(animator, formula,
					e));
		}
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
	 * Explains an error in an LTL formula: whether it is in the LTL syntax or
	 * in a B part ({PRED} or [EVENT]), and a syntax or type error; where it
	 * is (marked below the formula); for a type error, the types of the
	 * model's identifiers in the B part.
	 */
	static String describeLtlError(final Animator animator,
			final String formula, final LtlParseException e) {
		final Throwable cause = e.getCause();
		String token = null;
		int line = -1, column = -1;
		try {
			token = e.getTokenString();
			line = e.getTokenLine();
			column = e.getTokenColumn();
		} catch (RuntimeException noToken) {
			// no position
		}
		final StringBuilder sb = new StringBuilder();
		if (cause instanceof ProBParseException) {
			// a predicate or event in the formula
			String message = cause.getMessage() == null ? "" : cause
					.getMessage().trim();
			final boolean typeError = message.toLowerCase().contains("type");
			final java.util.regex.Matcher m = TYPES_DO_NOT_MATCH
					.matcher(message);
			if (m.find())
				message = "the types " + m.group(1).trim() + " and "
						+ m.group(2).trim() + " do not match"
						+ message.substring(m.end());
			sb.append(typeError ? "Type error" : "Syntax error");
			if (token != null && !token.trim().isEmpty())
				sb.append(" in ").append(braced(token.trim()));
			sb.append(": ").append(message);
			marker(sb, formula, token, line, column);
			if (typeError && token != null) {
				final String types = identifierTypes(animator, token);
				if (!types.isEmpty())
					sb.append("\nTypes in the model: ").append(types);
			}
		} else {
			// the LTL syntax: "[1,5] expecting: ..." or "Unknown token"
			String message = cause != null && cause.getMessage() != null ? cause
					.getMessage() : e.getMessage();
			if (message == null)
				message = String.valueOf(cause != null ? cause : e);
			message = message.replaceFirst("^[\\w.]+Exception:\\s*", "");
			// "[line,column] ...": the position if the token is unknown
			final java.util.regex.Matcher pos = java.util.regex.Pattern
					.compile("^\\s*\\[(\\d+),(\\d+)\\]\\s*").matcher(message);
			if (pos.find()) {
				if (column < 1) {
					line = Integer.parseInt(pos.group(1));
					column = Integer.parseInt(pos.group(2));
				}
				message = message.substring(pos.end());
			}
			message = shortenExpecting(message);
			sb.append("Syntax error in the LTL formula");
			if (token != null && !token.trim().isEmpty())
				sb.append(" at ").append(token.trim());
			sb.append(": ").append(message);
			marker(sb, formula, token, line, column);
			sb.append("\nLTL syntax, e.g.: G {PRED}, F [EVENT], X, U, W, R, not, &, or, =>, e(EVENT), deadlock, sink");
		}
		// not translated into mathematical symbols: the marker would no
		// longer be below the token
		return sb.toString();
	}

	/** "expecting: a, b, ..." with at most 12 alternatives */
	private static String shortenExpecting(final String message) {
		final int i = message.indexOf("expecting:");
		if (i < 0)
			return message;
		final String[] alternatives = message.substring(
				i + "expecting:".length()).split(",");
		if (alternatives.length <= 12)
			return message;
		final StringBuilder sb = new StringBuilder(message.substring(0, i))
				.append("expecting one of:");
		for (int k = 0; k < 12; k++)
			sb.append(k == 0 ? "" : ",").append(alternatives[k]);
		return sb.append(", ...").toString();
	}

	private static final java.util.regex.Pattern TYPES_DO_NOT_MATCH = java.util.regex.Pattern
			.compile("Type:\\s*(.+?)\\s+does not match type:\\s*(\\S+)");

	/** the B part as written in the formula, in braces if it has none */
	private static String braced(final String token) {
		if (token.startsWith("{") || token.startsWith("["))
			return token;
		return "{" + token + "}";
	}

	/** "cur : S, ..." for the predicate (or [EVENT | PRED]) of the token */
	private static String identifierTypes(final Animator animator,
			final String token) {
		final LanguageDependendAnimationPart ldp = animator == null ? null
				: animator.getLanguageDependendPart();
		if (!(ldp instanceof EventBAnimatorPart))
			return "";
		String text = token.trim();
		if ((text.startsWith("{") && text.endsWith("}"))
				|| (text.startsWith("[") && text.endsWith("]")))
			text = text.substring(1, text.length() - 1);
		final int bar = text.indexOf('|');
		if (token.trim().startsWith("[") && bar >= 0)
			text = text.substring(bar + 1);
		try {
			return ((EventBAnimatorPart) ldp).describeIdentifierTypes(text);
		} catch (RuntimeException e) {
			return "";
		}
	}

	/** the formula with the token (or its position) marked below it */
	private static void marker(final StringBuilder sb, final String formula,
			final String token, final int line, final int column) {
		final String[] lines = formula.split("\n", -1);
		final int l = line >= 1 && line <= lines.length ? line - 1 : 0;
		final String text = lines[l];
		int start = column - 1;
		int length = token == null ? 1 : Math.max(1, token.length());
		if (token != null && !token.isEmpty()) {
			if (start < 0 || !text.startsWith(token, start)) {
				// the column may be off, e.g. at the brace
				final int found = text.indexOf(token.trim());
				if (found >= 0) {
					start = found;
					length = token.trim().length();
				}
			}
		}
		if (start < 0 || start > text.length())
			return;
		length = Math.min(length, Math.max(1, text.length() - start));
		final StringBuilder underline = new StringBuilder();
		for (int i = 0; i < start; i++)
			underline.append(text.charAt(i) == '\t' ? '\t' : ' ');
		for (int i = 0; i < length; i++)
			underline.append('^');
		sb.append("\n  ").append(text).append("\n  ").append(underline);
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
