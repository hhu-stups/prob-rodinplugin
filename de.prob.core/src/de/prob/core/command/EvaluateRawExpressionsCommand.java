package de.prob.core.command;

import java.util.Collections;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.prob.core.Animator;
import de.prob.core.domainobjects.eval.AbstractEvalElement;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class EvaluateRawExpressionsCommand implements IComposableCommand {

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final List<AbstractEvalElement> evalElements;
	private final String stateId;
	private List<String> values;

	public EvaluateRawExpressionsCommand(
			final List<AbstractEvalElement> evalElements, final String id) {
		this.evalElements = evalElements;
		this.stateId = id;
	}

	public static List<String> evaluate(final Animator animator,
			final List<AbstractEvalElement> evalElements, final String id)
			throws ProBException {
		EvaluateRawExpressionsCommand command = new EvaluateRawExpressionsCommand(
				evalElements, id);
		animator.execute(command);
		return command.getValues();
	}

	public static String evaluate(final Animator a,
			final AbstractEvalElement e, final String id) throws ProBException {
		return evaluate(a, Collections.singletonList(e), id).get(0);
	}

	private List<String> getValues() {
		return values;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		ListPrologTerm prologTerm = (ListPrologTerm) bindings
				.get(EVALUATE_TERM_VARIABLE);
		values = PrologTerm.atomicStrings(prologTerm);
	}

	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm("evaluate_raw_expressions");
		pout.printAtomOrNumber(stateId);
		pout.openList();

		// print parsed expressions/predicates
		for (AbstractEvalElement term : evalElements) {
			final ASTProlog prolog = new ASTProlog(pout, null);
			term.getPrologAst().apply(prolog);
		}
		pout.closeList();
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EvaluateRawExpression(");
		boolean first = true;
		for (final AbstractEvalElement term : evalElements) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(term.getLabel());
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
