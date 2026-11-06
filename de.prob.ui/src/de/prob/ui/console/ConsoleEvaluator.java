package de.prob.ui.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.prob.core.Animator;
import de.prob.core.ILifecycleListener;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.CbcSolveCommand;
import de.prob.core.command.ComputeCoverageCommand;
import de.prob.core.command.ComputeCoverageCommand.ComputeCoverageResult;
import de.prob.core.command.GetCurrentPreferencesCommand;
import de.prob.core.command.GetPreferencesCommand;
import de.prob.core.command.GetVersionCommand;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationGetValuesCommand.EvaluationResult;
import de.prob.core.command.EvaluationInsertFormulaCommand;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ProBPreference;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.Variable;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.core.langdep.EventBAnimatorPart.OpenFormula;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

/**
 * Evaluates the predicates and expressions entered in the ProB console in
 * the current state of the animator, using the same commands as the formulas
 * of the State view. Identifiers that do not belong to the model are treated
 * as existentially quantified: ProB's constraint solver searches values for
 * them. The other formulas are evaluated by the solver too (an expression E
 * as CONSOLE_RESULT = E), unless they need values of a state that is not
 * initialised.
 * 
 * Must not be called from the UI thread: it waits for ProB.
 */
public class ConsoleEvaluator implements ILifecycleListener {

	public enum Kind {
		/** a predicate that is true */
		TRUE,
		/** a predicate that is false */
		FALSE,
		/** the value of an expression */
		VALUE,
		/** not evaluated, e.g. no model loaded or not initialised */
		NOT_AVAILABLE,
		/** the solver found neither a solution nor that there is none */
		UNKNOWN,
		/** e.g. a syntax error or a well-definedness error */
		ERROR
	}

	public static final class Result {
		private final Kind kind;
		private final String text;

		public Result(final Kind kind, final String text) {
			this.kind = kind;
			this.text = text;
		}

		public Kind getKind() {
			return kind;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return kind + ": " + text;
		}
	}

	/**
	 * The console commands for the solvers of cbc_timed_solve_with_opts, e.g.
	 * ":kodkod PRED"
	 */
	private static final Map<String, String> SOLVERS = createSolvers();

	private static Map<String, String> createSolvers() {
		final Map<String, String> solvers = new LinkedHashMap<String, String>();
		solvers.put("prob", CbcSolveCommand.DEFAULT_SOLVER);
		solvers.put("kodkod", "KODKOD");
		solvers.put("smt", "SMT_SUPPORTED_INTERPRETER");
		solvers.put("cdclt", "CDCLT");
		solvers.put("sat", "SAT");
		solvers.put("satz3", "SATZ3");
		solvers.put("z3", "Z3");
		solvers.put("z3cns", "Z3CNS");
		solvers.put("z3axm", "Z3AXM");
		solvers.put("cvc4", "CVC4");
		return Collections.unmodifiableMap(solvers);
	}

	/**
	 * The formulas sent to ProB, so that each one is inserted only once per
	 * loaded model
	 */
	private final Map<String, EvaluationElement> elements = new ConcurrentHashMap<String, EvaluationElement>();

	// set from the UI thread
	private volatile boolean showSolveTime = true;

	/** whether results of the constraint solver include its solve time */
	public void setShowSolveTime(final boolean showSolveTime) {
		this.showSolveTime = showSolveTime;
	}

	/**
	 * A new model has been loaded: the inserted formulas are no longer valid
	 */
	@Override
	public void reset() {
		elements.clear();
	}

	public Result evaluate(final String input) throws ProBException {
		final String formula = input.trim();
		final Animator animator = Animator.getAnimator();
		if (commandArgument(formula, "version") != null)
			return describeVersion(animator);
		if (!animator.isMachineLoaded())
			return new Result(Kind.NOT_AVAILABLE, "No model is being animated.");
		final String typeArgument = commandArgument(formula, "type");
		if (typeArgument != null)
			return describeType(animator, typeArgument);
		if (commandArgument(formula, "states") != null)
			return describeStateSpace(animator);
		final String prefsArgument = commandArgument(formula, "prefs");
		if (prefsArgument != null)
			return describePreferences(animator, prefsArgument);
		final State state = animator.getCurrentState();
		if (state == null)
			return new Result(Kind.NOT_AVAILABLE, "There is no current state.");
		if (commandArgument(formula, "state") != null)
			return describeState(state);
		if (isSolverCommand(formula))
			return solveWith(animator, state, formula);

		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		if (ldp == null)
			return new Result(Kind.NOT_AVAILABLE,
					"Formulas cannot be parsed for this model.");
		// decided each time: whether an expression goes to the solver
		// depends on the state
		if (ldp instanceof EventBAnimatorPart) {
			final OpenFormula open;
			try {
				open = ((EventBAnimatorPart) ldp).parseOpenFormula(formula);
			} catch (ProBParseException e) {
				return new Result(Kind.ERROR, e.getMessage());
			}
			if (!open.getFreeIdentifiers().isEmpty()
					|| useSolver(open, state))
				return solve(animator, state, open,
						CbcSolveCommand.DEFAULT_SOLVER);
		}

		EvaluationElement element = elements.get(formula);
		if (element == null) {
			final PrologTerm parsed;
			try {
				parsed = parse(new ProBParserBaseAdapter(ldp), formula);
			} catch (ProBParseException e) {
				return new Result(Kind.ERROR, e.getMessage());
			}
			element = EvaluationInsertFormulaCommand.insertFormula(parsed);
			elements.put(formula, element);
		}

		final EvaluationGetValuesCommand cmd = new EvaluationGetValuesCommand(
				state.getId(), Collections.singleton(element));
		animator.execute(cmd);
		return toResult(cmd.getResult().get(element));
	}

	/**
	 * Like "Add new Formula" in the State view: a predicate if possible,
	 * otherwise an expression
	 */
	private static PrologTerm parse(final ProBParserBaseAdapter parser,
			final String formula) throws ProBParseException {
		try {
			return parser.parsePredicate(formula, false);
		} catch (ProBParseException predicateError) {
			try {
				return parser.parseExpression(formula, false);
			} catch (ProBParseException expressionError) {
				// expressions are entered more often than predicates, so the
				// message for the expression is usually the relevant one
				throw expressionError;
			}
		}
	}

	/**
	 * @return the argument of the command, e.g. "F" for ":type F" (also if
	 *         ':' has become '\u2208'), "" if there is none, null if the
	 *         input is not this command
	 */
	private static String commandArgument(final String input,
			final String command) {
		if (!isSolverCommand(input))
			return null;
		final String rest = input.substring(1);
		if (!rest.startsWith(command))
			return null;
		final String argument = rest.substring(command.length());
		if (!argument.isEmpty() && !Character.isWhitespace(argument.charAt(0)))
			return null; // another command, e.g. ":types"
		return argument.trim();
	}

	/**
	 * ":state": the current state as known to the plug-in, without asking
	 * ProB
	 */
	private static Result describeState(final State state) {
		final StringBuilder sb = new StringBuilder("State ").append(
				state.getId()).append(": ");
		sb.append(state.isInitialized() ? "initialised" : "not initialised");
		if (state.isInvariantViolated())
			sb.append(", INVARIANT VIOLATED");
		if (state.isTimeoutOccured())
			sb.append(", time-out occurred");
		if (state.isMaxOperationReached())
			sb.append(", maximum number of events reached");
		if (state.hasStateBasedErrors())
			sb.append(", ").append(state.getStateBasedErrors().size())
					.append(" error(s), see the Event Error View");
		for (Map.Entry<String, Variable> entry : new TreeMap<String, Variable>(
				state.getValues()).entrySet()) {
			final String value = entry.getValue().getValue();
			sb.append("\n  ").append(entry.getKey()).append(" = ")
					.append(value == null ? "?" : value);
		}
		final Set<String> events = new TreeSet<String>();
		for (Operation operation : state.getEnabledOperations())
			events.add(operation.getName());
		sb.append("\nEnabled events: ").append(
				events.isEmpty() ? "none" : String.join(", ", events));
		return new Result(Kind.VALUE, sb.toString());
	}

	/**
	 * ":states": statistics about the state space explored so far (as in
	 * the Coverage table): its states and transitions, the states by kind
	 * and the transitions by event
	 */
	private static Result describeStateSpace(final Animator animator)
			throws ProBException {
		final ComputeCoverageResult coverage = ComputeCoverageCommand
				.getCoverage(animator);
		final StringBuilder sb = new StringBuilder("State space: ")
				.append(coverage.getTotalNumberOfNodes()).append(" state(s), ")
				.append(coverage.getTotalNumberOfTransitions())
				.append(" transition(s)");
		final Map<String, String> states = counts(coverage.getNodes());
		// the total is already in the first line
		states.remove("total");
		appendCounts(sb, "States", states);
		appendCounts(sb, "Transitions per event", counts(coverage.getOps()));
		final List<String> uncovered = new ArrayList<String>();
		for (String event : coverage.getUncovered())
			uncovered.add(eventName(event));
		sb.append("\nEvents not yet executed: ").append(
				uncovered.isEmpty() ? "none" : String.join(", ", uncovered));
		return new Result(Kind.VALUE, sb.toString());
	}

	/** @return the entries "name:number" of ProB's coverage, by name */
	private static Map<String, String> counts(final List<String> entries) {
		final Map<String, String> counts = new LinkedHashMap<String, String>();
		for (String entry : entries) {
			final int colon = entry.lastIndexOf(':');
			if (colon > 0)
				counts.put(eventName(entry.substring(0, colon)),
						entry.substring(colon + 1));
			else
				counts.put(entry, "?");
		}
		return counts;
	}

	private static void appendCounts(final StringBuilder sb,
			final String title, final Map<String, String> counts) {
		if (counts.isEmpty())
			return;
		int width = 0;
		for (String name : counts.keySet())
			width = Math.max(width, name.length());
		sb.append("\n").append(title).append(':');
		for (Map.Entry<String, String> entry : counts.entrySet()) {
			sb.append("\n  ").append(entry.getKey()).append(':');
			for (int i = entry.getKey().length(); i <= width; i++)
				sb.append(' ');
			sb.append(entry.getValue());
		}
	}

	/**
	 * @return the name of an event as in Rodin, e.g. INITIALISATION for
	 *         ProB's $initialise_machine; other names (such as the kinds of
	 *         states, e.g. invariant_violated) are readable as they are
	 */
	private static String eventName(final String name) {
		if (name.equals("$initialise_machine"))
			return "INITIALISATION";
		if (name.equals("$setup_constants"))
			return "SETUP_CONTEXT";
		if (name.equals("$partial_setup_constants"))
			return "PARTIAL_SETUP_CONTEXT";
		return name;
	}

	/**
	 * ":version": the versions of probcli (if it is running, i.e. a model is
	 * loaded), of the plug-ins, of Rodin's Event-B core and of Java
	 */
	private static Result describeVersion(final Animator animator)
			throws ProBException {
		final StringBuilder sb = new StringBuilder();
		if (animator.isMachineLoaded()) {
			final GetVersionCommand version = GetVersionCommand
					.getVersion(animator);
			sb.append("ProB (probcli) ").append(version.getVersion());
			sb.append("\n  Revision:     ").append(version.getRevision());
			sb.append("\n  Last changed: ").append(version.getLastChanged());
			sb.append("\n  Prolog:       ").append(version.getProlog());
		} else {
			sb.append("ProB (probcli): start an animation to see its version");
		}
		sb.append("\nPlug-ins");
		bundleVersion(sb, "de.prob.core", "ProB core");
		bundleVersion(sb, "de.prob.ui", "ProB UI");
		bundleVersion(sb, "org.eventb.core", "Rodin Event-B core");
		sb.append("\nJava ").append(System.getProperty("java.version"))
				.append(" (").append(System.getProperty("java.vendor"))
				.append(')');
		return new Result(Kind.VALUE, sb.toString());
	}

	private static void bundleVersion(final StringBuilder sb,
			final String bundleId, final String name) {
		final Bundle bundle = Platform.getBundle(bundleId);
		if (bundle == null)
			return;
		final String label = "  " + name + ":";
		sb.append("\n").append(label);
		for (int i = label.length(); i < 24; i++)
			sb.append(' ');
		sb.append(bundle.getVersion());
	}

	/**
	 * ":prefs [TEXT]": the current values of ProB's preferences, only those
	 * whose name contains TEXT (ignoring case) if given
	 */
	private static Result describePreferences(final Animator animator,
			final String filter) throws ProBException {
		final Map<String, String> values = GetCurrentPreferencesCommand
				.getCurrentPreferences(animator);
		final Map<String, String> defaults = new HashMap<String, String>();
		for (ProBPreference pref : GetPreferencesCommand
				.getPreferences(animator))
			defaults.put(pref.name, pref.defaultValue);
		final String lowerFilter = filter.toLowerCase(Locale.ROOT);
		final StringBuilder sb = new StringBuilder();
		int count = 0;
		for (Map.Entry<String, String> entry : values.entrySet()) {
			final String name = entry.getKey();
			if (!name.toLowerCase(Locale.ROOT).contains(lowerFilter))
				continue;
			count++;
			sb.append("\n  ").append(name).append(" = ")
					.append(entry.getValue());
			final String defaultValue = defaults.get(name);
			if (defaultValue != null
					&& !defaultValue.equals(entry.getValue()))
				sb.append("   (default: ").append(defaultValue).append(')');
		}
		return new Result(Kind.VALUE, count + " preference(s)"
				+ (filter.isEmpty() ? "" : " containing \"" + filter + "\"")
				+ sb);
	}

	/** ":type F": the type of F, computed by Rodin's type checker */
	private static Result describeType(final Animator animator,
			final String formula) {
		if (formula.isEmpty())
			return new Result(Kind.ERROR, "Usage: :type FORMULA");
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		if (!(ldp instanceof EventBAnimatorPart))
			return new Result(Kind.NOT_AVAILABLE,
					"Types are only available for Event-B models.");
		try {
			return new Result(Kind.VALUE,
					((EventBAnimatorPart) ldp).describeType(formula));
		} catch (ProBParseException e) {
			return new Result(Kind.ERROR, e.getMessage());
		}
	}

	private static boolean isSolverCommand(final String formula) {
		// with "Type Mathematical Symbols", ':' may have become '\u2208'
		return formula.startsWith(":") || formula.startsWith("\u2208");
	}

	/**
	 * Input like ":kodkod PRED": solves PRED with the given solver, also if
	 * it has no free identifiers
	 */
	private Result solveWith(final Animator animator, final State state,
			final String input) throws ProBException {
		final String command = input.substring(1);
		int end = 0;
		while (end < command.length()
				&& !Character.isWhitespace(command.charAt(end)))
			end++;
		final String name = command.substring(0, end);
		final String solver = SOLVERS.get(name.toLowerCase(Locale.ROOT));
		if (solver == null)
			return new Result(Kind.ERROR, "Unknown command :" + name
					+ ", available: :help, :x, :clear, :type, :state, :table, :graph, :states, :prefs, :version, "
					+ solverCommands());
		final String formula = command.substring(end).trim();
		if (formula.isEmpty())
			return new Result(Kind.ERROR, "Usage: :" + name + " PRED");
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		if (!(ldp instanceof EventBAnimatorPart))
			return new Result(Kind.NOT_AVAILABLE,
					"The solvers are only available for Event-B models.");
		final OpenFormula open;
		try {
			open = ((EventBAnimatorPart) ldp).parseOpenFormula(formula);
		} catch (ProBParseException e) {
			return new Result(Kind.ERROR, e.getMessage());
		}
		return solve(animator, state, open, solver);
	}

	/** @return true for the name of a solver command, e.g. "kodkod" */
	public static boolean isSolverName(final String name) {
		return SOLVERS.containsKey(name.toLowerCase(Locale.ROOT));
	}

	/** @return the console commands of the solvers, e.g. ":kodkod" */
	public static String solverCommands() {
		final StringBuilder sb = new StringBuilder();
		for (String name : SOLVERS.keySet()) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(':').append(name);
		}
		return sb.toString();
	}

	/**
	 * Lets a solver find values for the free identifiers, the identifiers of
	 * the model have their values in the current state
	 */
	private Result solve(final Animator animator, final State state,
			final OpenFormula formula, final String solver)
			throws ProBException {
		final CbcSolveCommand cmd = CbcSolveCommand.solve(animator, solver,
				state.getId(), formula.getSolverPredicate());
		switch (cmd.getOutcome()) {
		case SOLUTION:
			final Map<String, String> solution = cmd.getSolution();
			final StringBuilder sb = new StringBuilder();
			if (formula.isPredicate()) {
				sb.append("TRUE");
			} else {
				sb.append(valueOf(solution, formula.getResultIdentifier()));
			}
			// predicate: one identifier per line; expression: the values the
			// result was computed for. The solution also contains the
			// identifiers of the model, they are not shown.
			boolean first = true;
			for (String id : formula.getFreeIdentifiers()) {
				if (formula.isPredicate())
					sb.append("\n  ");
				else
					sb.append(first ? "\n  for " : ", ");
				first = false;
				sb.append(id).append(" = ").append(valueOf(solution, id));
			}
			sb.append(time(cmd));
			return new Result(formula.isPredicate() ? Kind.TRUE : Kind.VALUE,
					sb.toString());
		case NO_SOLUTION:
			return formula.isPredicate() ? new Result(Kind.FALSE,
					(formula.getFreeIdentifiers().isEmpty() ? "FALSE"
							: "FALSE (no solution exists)") + time(cmd))
					: new Result(
					Kind.ERROR, "The expression has no value." + time(cmd));
		case UNKNOWN:
			return new Result(Kind.UNKNOWN, "UNKNOWN: no solution found"
					+ reason(cmd) + time(cmd));
		default:
			return new Result(Kind.ERROR, "ProB could not solve the formula"
					+ reason(cmd) + time(cmd));
		}
	}

	private String time(final CbcSolveCommand cmd) {
		if (!showSolveTime)
			return "";
		return "\n  (solved by " + cmd.getSolver() + " in "
				+ cmd.getMilliseconds() + " ms)";
	}

	/**
	 * Formulas without free identifiers are also evaluated by the solver (an
	 * expression E as CONSOLE_RESULT = E), except if they use identifiers of
	 * the model in a state that is not initialised: the solver would then
	 * choose values for them
	 */
	private static boolean useSolver(final OpenFormula formula,
			final State state) {
		return state.isInitialized() || !formula.usesModelIdentifiers();
	}

	private static String valueOf(final Map<String, String> solution,
			final String id) {
		final String value = solution.get(id);
		return value == null ? "?" : UnicodeTranslator.toUnicode(value);
	}

	private static String reason(final CbcSolveCommand cmd) {
		return cmd.getReason() == null ? "." : " (" + cmd.getReason() + ").";
	}

	private static Result toResult(final EvaluationResult result) {
		if (result == null)
			return new Result(Kind.ERROR, "ProB returned no value.");
		if (!result.isActive())
			return new Result(Kind.NOT_AVAILABLE,
					"Not available in the current state (e.g. the machine is not initialised yet).");
		if (result.hasError())
			return new Result(Kind.ERROR, result.getText());
		if (result.isPredicate())
			return new Result(result.isPredicateTrue() ? Kind.TRUE : Kind.FALSE,
					result.getText());
		return new Result(Kind.VALUE, result.getText());
	}

}
