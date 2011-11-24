package de.prob.core.command;

import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * This command sends the ID of an expression to the ProB core and receives the
 * corresponding label (usually the pretty-printed expression) and the IDs of
 * the expression's child nodes.
 * 
 * @see EvaluationGetTopLevelCommand
 * @see EvaluationGetValuesCommand
 * @author plagge
 */
public class EvaluationExpandCommand implements IComposableCommand {
	private static final String LABEL_VARNAME = "Lbl";
	private static final String CHILDREN_VARNAME = "Chs";

	private final PrologTerm evaluationElement;

	private String label;
	private List<PrologTerm> children;

	public EvaluationExpandCommand(final PrologTerm evaluationElement) {
		this.evaluationElement = evaluationElement;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		label = ((CompoundPrologTerm) bindings.get(LABEL_VARNAME)).getFunctor();
		children = (ListPrologTerm) bindings.get(CHILDREN_VARNAME);
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_expand_formula");
		evaluationElement.toTermOutput(pto);
		pto.printVariable(LABEL_VARNAME);
		pto.printVariable(CHILDREN_VARNAME);
		pto.closeTerm();
	}

	public String getLabel() {
		return label;
	}

	public List<PrologTerm> getChildrenIds() {
		return children;
	}
}
