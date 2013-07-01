/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */
package de.prob.core.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class QuickDescribeUnsatPropertiesCommand implements IComposableCommand {
	public static final String OUTPUT_VARIABLE = "Output";
	private boolean unsatPropertiesExist;
	private String description;

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		if (bindings.get(OUTPUT_VARIABLE).hasFunctor(
				"no_unsat_properties_found", 0)) {
			unsatPropertiesExist = false;
			description = "";
		} else {
			unsatPropertiesExist = true;
			ListPrologTerm outputStrings = (ListPrologTerm) bindings.get(
					OUTPUT_VARIABLE).getArgument(1);

			StringBuilder sb = new StringBuilder();

			for (PrologTerm p : outputStrings) {
				sb.append(PrologTerm.atomicString(p));
			}

			// replace newline dummys. maybe we should restructure the Prolog
			// part?
			description = sb.toString().replace(";", "\n");

		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("quick_describe_unsat_properties")
				.printVariable(OUTPUT_VARIABLE).closeTerm();
	}

	public boolean unsatPropertiesExist() {
		return unsatPropertiesExist;
	}

	public String getUnsatPropertiesDescription() {
		return description;
	}

}
