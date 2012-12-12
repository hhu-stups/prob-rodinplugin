package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class CSPEvauateExpressionCommand implements IComposableCommand {

	private static final String EVALUATE_TERM_VARIABLE = "Res";

	private String expression;
	private String fileName;
	private static String result;

	public CSPEvauateExpressionCommand(String expression, String fileName) {
		this.expression = expression;
		this.fileName = fileName;
	}

	public static String evaluate(final Animator a, final String expression,
			final String fileName) throws ProBException {
		CSPEvauateExpressionCommand command = new CSPEvauateExpressionCommand(
				expression, fileName);
		a.execute(command);
		return result;
	}

	@Override
	public void writeCommand(IPrologTermOutput pout) throws CommandException {
		pout.openTerm("evaluate_csp_expression_eclipse");
		pout.printAtom(expression);
		pout.printAtom(fileName);
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		CompoundPrologTerm prologTerm = (CompoundPrologTerm) bindings
				.get(EVALUATE_TERM_VARIABLE);
		result = prologTerm.toString();
	}

}
