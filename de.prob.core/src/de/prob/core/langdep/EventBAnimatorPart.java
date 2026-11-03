/**
 * 
 */
package de.prob.core.langdep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.eventb.translator.internal.TranslationVisitor;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

/**
 * @author plagge
 * 
 */
public class EventBAnimatorPart implements LanguageDependendAnimationPart {
	private static final String EXPR_WRAPPER = "bexpr";
	private static final String PRED_WRAPPER = "bpred";
	private static final String TRANS_WRAPPER = "btrans";

	private final IEventBRoot root;

	public EventBAnimatorPart(final IEventBRoot root) {
		this.root = root;
	}

	/**
	 * @return the machine or context this part belongs to
	 */
	public IEventBRoot getRoot() {
		return root;
	}

	@Override
	public void parseExpression(final IPrologTermOutput pto,
			final String expression1, final boolean wrap)
			throws ProBParseException {
		final String expression = UnicodeTranslator.toUnicode(expression1);

		final FormulaFactory ff = root.getFormulaFactory();
		final IParseResult parseResult = ff.parseExpression(expression, null);

		checkParseResult(parseResult);
		final Expression ee = parseResult.getParsedExpression();
		typeCheck(ff, ee, expression);
		final PExpression expr = TranslationVisitor.translateExpression(ee);
		toPrologTerm(pto, expr, wrap, EXPR_WRAPPER);
	}

	@Override
	public void parsePredicate(final IPrologTermOutput pto,
			final String predicate1, final boolean wrap)
			throws ProBParseException {
		final String predicate = UnicodeTranslator.toUnicode(predicate1);

		final FormulaFactory ff = root.getFormulaFactory();
		final IParseResult parseResult = ff.parsePredicate(predicate, null);

		checkParseResult(parseResult);
		final Predicate pp = parseResult.getParsedPredicate();
		typeCheck(ff, pp, predicate);
		final PPredicate apred = TranslationVisitor.translatePredicate(pp);
		toPrologTerm(pto, apred, wrap, PRED_WRAPPER);
	}

	/** the identifier for the value of an expression, see {@link OpenFormula} */
	public static final String RESULT_IDENTIFIER = "CONSOLE_RESULT";

	/**
	 * A predicate or expression that may contain free identifiers, i.e.
	 * identifiers that are not part of the model (see
	 * {@link EventBAnimatorPart#parseOpenFormula(String)}).
	 */
	public static final class OpenFormula {
		private final boolean predicate;
		private final boolean usesModelIdentifiers;
		private final List<String> freeIdentifiers;
		private final String resultIdentifier;
		private final PrologTerm solverPredicate;

		OpenFormula(final boolean predicate,
				final boolean usesModelIdentifiers,
				final List<String> freeIdentifiers,
				final String resultIdentifier, final PrologTerm solverPredicate) {
			this.predicate = predicate;
			this.usesModelIdentifiers = usesModelIdentifiers;
			this.freeIdentifiers = Collections
					.unmodifiableList(freeIdentifiers);
			this.resultIdentifier = resultIdentifier;
			this.solverPredicate = solverPredicate;
		}

		/** @return true for a predicate, false for an expression */
		public boolean isPredicate() {
			return predicate;
		}

		/** @return true if the formula uses constants or variables */
		public boolean usesModelIdentifiers() {
			return usesModelIdentifiers;
		}

		/**
		 * @return the identifiers of the formula that do not belong to the
		 *         model, sorted (without the {@link #getResultIdentifier()})
		 */
		public List<String> getFreeIdentifiers() {
			return freeIdentifiers;
		}

		/**
		 * @return the identifier that stands for the value of the
		 *         expression, null for a predicate
		 */
		public String getResultIdentifier() {
			return resultIdentifier;
		}

		/**
		 * @return the predicate to solve, unwrapped: the predicate itself or,
		 *         for an expression E, <code>R = E</code> with the
		 *         {@link #getResultIdentifier()} R
		 */
		public PrologTerm getSolverPredicate() {
			return solverPredicate;
		}
	}

	/**
	 * Parses a predicate or, if it is not one, an expression, like
	 * {@link #parsePredicate(IPrologTermOutput, String, boolean)} but
	 * identifiers that do not belong to the model are allowed (their types
	 * must be inferable).
	 */
	public OpenFormula parseOpenFormula(final String text)
			throws ProBParseException {
		final String formula = UnicodeTranslator.toUnicode(text);
		final FormulaFactory ff = root.getFormulaFactory();
		final ITypeEnvironment typeEnv = getTypeEnvironment(ff);

		final IParseResult predicateResult = ff.parsePredicate(formula, null);
		if (!predicateResult.hasProblem()) {
			final Predicate pred = predicateResult.getParsedPredicate();
			final List<String> free = typeCheckOpen(pred, typeEnv, formula);
			return new OpenFormula(true, usesModelIdentifiers(pred, typeEnv),
					free, null, toPrologTerm(pred));
		}

		final IParseResult expressionResult = ff
				.parseExpression(formula, null);
		// expressions are entered more often than predicates, so the
		// message for the expression is usually the relevant one
		checkParseResult(expressionResult);
		final Expression expr = expressionResult.getParsedExpression();
		final String result = freshName(expr, typeEnv);
		final Predicate equality = ff.makeRelationalPredicate(Formula.EQUAL,
				ff.makeFreeIdentifier(result, null), expr, null);
		final List<String> free = typeCheckOpen(equality, typeEnv, formula);
		free.remove(result);
		return new OpenFormula(false, usesModelIdentifiers(expr, typeEnv),
				free, result, toPrologTerm(equality));
	}

	private static boolean usesModelIdentifiers(final Formula<?> formula,
			final ITypeEnvironment typeEnvironment) {
		for (FreeIdentifier id : formula.getFreeIdentifiers())
			if (typeEnvironment.contains(id.getName()))
				return true;
		return false;
	}

	/**
	 * Describes the type of a predicate or expression (parsed as in
	 * {@link #parseOpenFormula(String)}): "predicate" or the type of the
	 * expression, followed by the inferred types of the identifiers that do
	 * not belong to the model, one per line (e.g. "  x : \u2124").
	 */
	public String describeType(final String text) throws ProBParseException {
		final String formula = UnicodeTranslator.toUnicode(text);
		final FormulaFactory ff = root.getFormulaFactory();
		final ITypeEnvironment typeEnv = getTypeEnvironment(ff);

		final Formula<?> parsed;
		final IParseResult predicateResult = ff.parsePredicate(formula, null);
		if (!predicateResult.hasProblem()) {
			parsed = predicateResult.getParsedPredicate();
		} else {
			final IParseResult expressionResult = ff.parseExpression(formula,
					null);
			checkParseResult(expressionResult);
			parsed = expressionResult.getParsedExpression();
		}
		final ITypeCheckResult tcr = parsed.typeCheck(typeEnv);
		if (tcr.hasProblem())
			throw typeError(tcr, formula);
		final StringBuilder sb = new StringBuilder();
		sb.append(parsed instanceof Expression ? ((Expression) parsed)
				.getType().toString() : "predicate");
		final ITypeEnvironment inferred = tcr.getInferredEnvironment();
		for (String name : new TreeSet<String>(inferred.getNames()))
			sb.append("\n  ").append(name).append(" : ")
					.append(inferred.getType(name));
		return sb.toString();
	}

	/**
	 * Type checks the formula in the type environment of the model.
	 * 
	 * @return the identifiers that are not in the type environment, sorted
	 */
	private List<String> typeCheckOpen(final Formula<?> formula,
			final ITypeEnvironment typeEnvironment, final String text)
			throws ProBParseException {
		final ITypeCheckResult tcr = formula.typeCheck(typeEnvironment);
		if (tcr.hasProblem())
			throw typeError(tcr, text);
		return new ArrayList<String>(new TreeSet<String>(tcr
				.getInferredEnvironment().getNames()));
	}

	/**
	 * @param text
	 *            the parsed text, the problems refer to positions in it
	 * @return an exception for the type check problems; Rodin's message for
	 *         an unknown type does not say which identifier is concerned
	 */
	private static ProBParseException typeError(final ITypeCheckResult tcr,
			final String text) {
		// the identifiers in the order they occur in the text
		final Map<Integer, String> unknownByPosition = new TreeMap<Integer, String>();
		ASTProblem other = null;
		for (ASTProblem problem : tcr.getProblems()) {
			final String snippet = snippet(problem, text);
			if (problem.getMessage() == ProblemKind.TypeUnknown
					&& snippet != null)
				unknownByPosition.put(problem.getSourceLocation().getStart(),
						snippet);
			else if (other == null)
				other = problem;
		}
		final Set<String> unknown = new LinkedHashSet<String>(
				unknownByPosition.values());
		if (!unknown.isEmpty())
			return new ProBParseException("Cannot infer the type of "
					+ String.join(", ", unknown)
					+ " (add a typing predicate such as x \u2208 \u2124)");
		if (other == null) // only problems without a location
			other = tcr.getProblems().get(0);
		final String snippet = snippet(other, text);
		return new ProBParseException(snippet == null
				|| snippet.equals(text.trim()) ? other.toString() : other
				+ " in: " + snippet);
	}

	/** @return the part of the text the problem refers to, if known */
	private static String snippet(final ASTProblem problem, final String text) {
		final SourceLocation location = problem.getSourceLocation();
		if (location == null || location.getStart() < 0
				|| location.getEnd() >= text.length()
				|| location.getStart() > location.getEnd())
			return null;
		return text.substring(location.getStart(), location.getEnd() + 1);
	}

	private static String freshName(final Expression expr,
			final ITypeEnvironment typeEnvironment) {
		final Set<String> used = new HashSet<String>();
		for (FreeIdentifier id : expr.getFreeIdentifiers())
			used.add(id.getName());
		String name = RESULT_IDENTIFIER;
		for (int i = 1; used.contains(name)
				|| typeEnvironment.contains(name); i++)
			name = RESULT_IDENTIFIER + i;
		return name;
	}

	private static PrologTerm toPrologTerm(final Predicate predicate) {
		final StructuredPrologOutput out = new StructuredPrologOutput();
		predicate(out, predicate);
		out.fullstop();
		return out.getSentences().get(0);
	}

	private static void predicate(final IPrologTermOutput pto,
			final Predicate predicate) {
		final PPredicate apred = TranslationVisitor.translatePredicate(predicate);
		apred.apply(new ASTProlog(pto, null));
	}

	private void toPrologTerm(final IPrologTermOutput pto,
			final Node formulaNode, final boolean wrap, final String wrapper)
			throws ProBParseException {
		if (wrap) {
			pto.openTerm(wrapper);
		}
		final ASTProlog prolog = new ASTProlog(pto, null);
		formulaNode.apply(prolog);
		if (wrap) {
			pto.closeTerm();
		}
	}

	private void typeCheck(final FormulaFactory ff, final Formula<?> pp,
			final String text) throws ProBParseException {
		typeCheck(pp, getTypeEnvironment(ff), text);
	}

	private void typeCheck(final Formula<?> pp,
			final ITypeEnvironment typeEnvironment, final String text)
			throws ProBParseException {
		final ITypeCheckResult tcr = pp.typeCheck(typeEnvironment);
		if (tcr.hasProblem())
			throw typeError(tcr, text);
		final ITypeEnvironment inferred = tcr.getInferredEnvironment();
		if (!inferred.isEmpty()) {
			final Set<String> names = inferred.getNames();
			throw new ProBParseException("unknown identifier: "
					+ names.toString());
		}
	}

	private ITypeEnvironment getTypeEnvironment(final FormulaFactory ff)
			throws ProBParseException {
		ITypeEnvironment typeEnv = null;

		try {
			if (root instanceof IMachineRoot)
				typeEnv = root.getSCMachineRoot().getTypeEnvironment();
			if (root instanceof ISCMachineRoot)
				typeEnv = root.getSCMachineRoot().getTypeEnvironment();
			if (root instanceof IContextRoot)
				typeEnv = root.getSCContextRoot().getTypeEnvironment();
			if (root instanceof ISCContextRoot)
				typeEnv = root.getSCContextRoot().getTypeEnvironment();

		} catch (CoreException e) {
			throw rodin2parseException(e);
		}
		return typeEnv;
	}

	private void checkParseResult(final IParseResult parseResult)
			throws ProBParseException {
		if (parseResult.hasProblem())
			throw new ProBParseException(parseResult.getProblems().toString());
	}

	@Override
	public void reload(final Animator animator) throws ProBException {
		LoadEventBModelCommand.load(animator, root);
	}

	@Override
	public void parseTransitionPredicate(final IPrologTermOutput pto,
			final String transPredicate, final boolean wrap)
			throws ProBParseException, UnsupportedOperationException {
		if (root instanceof IMachineRoot) {
			final String event;
			final String predicate;
			int sepIndex = transPredicate.indexOf("|");
			if (sepIndex < 0) {
				event = transPredicate.trim();
				predicate = null;
			} else {
				event = transPredicate.substring(0, sepIndex).trim();
				predicate = transPredicate.substring(sepIndex + 1);
			}
			parseTransition(pto, wrap, event, predicate);
		} else
			throw new UnsupportedOperationException(
					"predicates on transitions are only supported when animating machines");
	}

	private void parseTransition(final IPrologTermOutput pto,
			final boolean wrap, final String eventName,
			final String predicateString) throws ProBParseException {
		final ISCEvent event = getEvent(eventName);
		final Predicate predicate = predicateString == null ? null
				: parseTransPredicate(predicateString, event);
		printTransPred(pto, wrap, eventName, predicate);
	}

	private ISCEvent getEvent(final String eventName) throws ProBParseException {
		final ISCMachineRoot machine = ((IMachineRoot) root).getSCMachineRoot();
		try {
			for (final ISCEvent event : machine.getSCEvents()) {
				if (Objects.equals(event.getLabel(), eventName))
					return event;
			}
			throw new ProBParseException("unknown event " + eventName);
		} catch (RodinDBException e) {
			throw rodin2parseException(e);
		}
	}

	private Predicate parseTransPredicate(final String predicateString,
			final ISCEvent event) throws ProBParseException {
		final String utf8String = UnicodeTranslator.toUnicode(predicateString);

		final FormulaFactory ff = root.getFormulaFactory();
		final IParseResult parseResult = ff.parsePredicate(utf8String, null);

		checkParseResult(parseResult);
		final Predicate predicate = parseResult.getParsedPredicate();

		final ITypeEnvironmentBuilder typeEnv = ff.makeTypeEnvironment();
		typeEnv.addAll(getTypeEnvironment(ff));

		addEventParameters(event, ff, typeEnv, new ArrayList<String>());
		final ISCMachineRoot machine = ((IMachineRoot) root).getSCMachineRoot();
		addPostStateVariables(machine, ff, typeEnv, new ArrayList<String>());
		typeCheck(predicate, typeEnv, utf8String);
		return predicate;
	}

	private void addPostStateVariables(final ISCMachineRoot machine,
			final FormulaFactory ff, final ITypeEnvironmentBuilder typeEnv,
			final ArrayList<String> allVariables) throws ProBParseException {
		try {
			final ISCVariable[] variables = machine.getSCVariables();
			addAllIdentifiers(ff, typeEnv, allVariables, variables, "'");
			for (final IRodinFile refMachine : machine.getAbstractSCMachines()) {
				ISCMachineRoot absMachine = (ISCMachineRoot) refMachine
						.getRoot();
				addPostStateVariables(absMachine, ff, typeEnv, allVariables);
			}
		} catch (CoreException e) {
			throw rodin2parseException(e);
		}
	}

	private void addEventParameters(final ISCEvent event,
			final FormulaFactory ff, final ITypeEnvironmentBuilder typeEnv,
			final Collection<String> allParameters) throws ProBParseException {
		try {
			ISCParameter[] params = event.getSCParameters();
			addAllIdentifiers(ff, typeEnv, allParameters, params, "");
			// TODO: The Prolog side does not handle abstract parameters yet
			// As soon that is solved, the following code can be enabled
			// for (final ISCEvent absEvent : event.getAbstractSCEvents()) {
			// addEventParameters(absEvent, ff, typeEnv, allParameters);
			// }
		} catch (CoreException e) {
			throw rodin2parseException(e);
		}
	}

	private void addAllIdentifiers(final FormulaFactory ff,
			final ITypeEnvironmentBuilder typeEnv,
			final Collection<String> allParameters,
			final ISCIdentifierElement[] ids, final String postfix)
			throws CoreException {
		for (final ISCIdentifierElement identifier : ids) {
			final String name = identifier.getIdentifierString() + postfix;
			if (!allParameters.contains(name)) {
				final Type type = identifier.getType(ff);
				typeEnv.addName(name, type);
				allParameters.add(name);
			}
		}
	}

	private ProBParseException rodin2parseException(final CoreException e) {
		return new ProBParseException(
				"Error in the underlying Rodin Database.\nTry cleaning your workspace.\n Details: "
						+ e.getLocalizedMessage());
	}

	private void printTransPred(final IPrologTermOutput pto,
			final boolean wrap, final String eventName,
			final Predicate predicate) {
		if (wrap) {
			pto.openTerm(TRANS_WRAPPER);
		}
		pto.openTerm("event");
		pto.printAtom(eventName);
		if (predicate != null) {
			final PPredicate apred = TranslationVisitor
					.translatePredicate(predicate);
			final ASTProlog prolog = new ASTProlog(pto, null);
			apred.apply(prolog);
		}
		pto.closeTerm();
		if (wrap) {
			pto.closeTerm();
		}
	}
}
