/**
 * 
 */
package de.prob.core.langdep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
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
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
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

	public void parseExpression(final IPrologTermOutput pto,
			final String expression1, final boolean wrap)
			throws ProBParseException {
		final String expression = UnicodeTranslator.toUnicode(expression1);

		final FormulaFactory ff = root.getFormulaFactory();
		final IParseResult parseResult = ff.parseExpression(expression, null);

		checkParseResult(parseResult);
		final Expression ee = parseResult.getParsedExpression();
		typeCheck(ff, ee);
		final PExpression expr = TranslationVisitor.translateExpression(ee);
		toPrologTerm(pto, expr, wrap, EXPR_WRAPPER);
	}

	public void parsePredicate(final IPrologTermOutput pto,
			final String predicate1, final boolean wrap)
			throws ProBParseException {
		final String predicate = UnicodeTranslator.toUnicode(predicate1);

		final FormulaFactory ff = root.getFormulaFactory();
		final IParseResult parseResult = ff.parsePredicate(predicate, null);

		checkParseResult(parseResult);
		final Predicate pp = parseResult.getParsedPredicate();
		typeCheck(ff, pp);
		final PPredicate apred = TranslationVisitor.translatePredicate(pp);
		toPrologTerm(pto, apred, wrap, PRED_WRAPPER);
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

	private void typeCheck(final FormulaFactory ff, final Formula<?> pp)
			throws ProBParseException {
		typeCheck(pp, getTypeEnvironment(ff));
	}

	private void typeCheck(final Formula<?> pp,
			final ITypeEnvironment typeEnvironment) throws ProBParseException {
		final ITypeCheckResult tcr = pp.typeCheck(typeEnvironment);
		if (tcr.hasProblem()) {
			final List<ASTProblem> problems = tcr.getProblems();
			final ASTProblem problem = problems.iterator().next();
			throw new ProBParseException(problem.toString());
		}
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

	public void reload(final Animator animator) throws ProBException {
		LoadEventBModelCommand.load(animator, root);
	}

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
				if (ObjectUtils.equals(event.getLabel(), eventName))
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
		typeCheck(predicate, typeEnv);
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
