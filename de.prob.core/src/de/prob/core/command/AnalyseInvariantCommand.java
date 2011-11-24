package de.prob.core.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class AnalyseInvariantCommand implements ISimpleTextCommand {

	private static final String UNKNOWN = "Unknown";
	private static final String FALSE = "False";
	private static final String TOTAL = "Total";
	private static final String RESULT = "Result";
	private static final String DESCRIPTION = "Desc";
	private StringBuffer text;

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		// analyse_predicate(Type,Desc,Result,Total,False,Unknown)
		pto.openTerm("analyse_predicate").printAtom("invariant")
				.printVariable(DESCRIPTION).printVariable(RESULT)
				.printVariable(TOTAL).printVariable(FALSE)
				.printVariable(UNKNOWN).closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		text = new StringBuffer();
		text.append("Analysed: " + bindings.get(DESCRIPTION) + "\n");
		text.append("-------------------------------------\n");
		ListPrologTerm r = (ListPrologTerm) bindings.get(RESULT);
		for (PrologTerm term : r) {
			text.append(term);
			text.append('\n');
		}
		text.append("-------------------------------------\n");
		text.append("Total: " + bindings.get(TOTAL)+ "\n");
		text.append("False: " + bindings.get(FALSE)+ "\n");
		text.append("Unknown: " + bindings.get(UNKNOWN));
	}

	@Override
	public String getResultingText() {
		return text.toString();
	}

}
