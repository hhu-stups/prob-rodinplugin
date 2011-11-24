/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.sap.commands;

import de.prob.core.Animator;
import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GenerateLocalTestcasesCommand implements IComposableCommand {

	private final String global;
	private final String local;
	private final int maxNodes;
	private LocalTestcasesResult result;

	public GenerateLocalTestcasesCommand(final String global,
			final String local, final int maxNodes) {
		this.global = global;
		this.local = local;
		this.maxNodes = maxNodes;
	}

	public static LocalTestcasesResult generateTestcases(
			final String globalFilename, final String localFilename,
			final int maxNodes) throws ProBException {
		final GenerateLocalTestcasesCommand command = new GenerateLocalTestcasesCommand(
				globalFilename, localFilename, maxNodes);
		Animator.getAnimator().execute(command);
		return command.result;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final int sat = ((IntegerPrologTerm) bindings.get("SAT")).getValue()
				.intValue();
		final int unsat = ((IntegerPrologTerm) bindings.get("UNSAT"))
				.getValue().intValue();
		result = new LocalTestcasesResult(sat, unsat);

	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("sap_generate_testcases").printAtom(global)
				.printAtom(local).printNumber(maxNodes).printVariable("SAT")
				.printVariable("UNSAT").closeTerm();
	}

}
