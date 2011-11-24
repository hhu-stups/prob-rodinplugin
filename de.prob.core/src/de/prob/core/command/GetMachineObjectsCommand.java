/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

/**
 * 
 */
package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.core.domainobjects.MachineDescription.Section;
import de.prob.core.domainobjects.MachineDescription.SectionType;
import de.prob.core.prolog.TypedIdentifierGenerator;
import de.prob.core.types.TypedIdentifier;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * With this command, one retrieves some of the properties of a machine: The
 * elements of given sets, the constants, variables and the operations.
 * 
 * @author plagge
 * 
 */
public class GetMachineObjectsCommand implements IComposableCommand {

	public static class MachineObjectsResult {
		public final Section[] sections;
		public final TypedIdentifier[] setElements;
		public final TypedIdentifier[] constants;
		public final TypedIdentifier[] variables;
		public final TypedIdentifier[] operations;

		public MachineObjectsResult(final Section[] sections,
				final TypedIdentifier[] setElements,
				final TypedIdentifier[] constants,
				final TypedIdentifier[] variables,
				final TypedIdentifier[] operations) {
			this.sections = sections;
			this.setElements = setElements;
			this.constants = constants;
			this.variables = variables;
			this.operations = operations;
		}
	}

	private MachineObjectsResult machineObjectsResult;

	public static MachineObjectsResult getMachineObjects(final Animator a)
			throws ProBException {
		GetMachineObjectsCommand getMachineObjectsCommand = new GetMachineObjectsCommand();
		a.execute(getMachineObjectsCommand);
		return getMachineObjectsCommand.getResult();
	}

	// FIXME refactor me!
	private static Section[] getSections(
			final ISimplifiedROMap<String, PrologTerm> binding, final String var)
			throws CommandException {
		PrologTerm term = getNotNullTerm(binding, var);
		if (term.isList()) {
			final ListPrologTerm list = (ListPrologTerm) term;
			Section[] result = new Section[list.size()];
			for (int i = 0; i < list.size(); i++) {
				final PrologTerm elem = list.get(i);
				final SectionType sectionType;
				if (elem.hasFunctor("model", 1)) {
					sectionType = SectionType.MODEL;
				} else if (elem.hasFunctor("context", 1)) {
					sectionType = SectionType.CONTEXT;
				} else {
					CommandException cmdException = new CommandException(
							"Prolog section list contains non-atomic component");
					cmdException.notifyUserOnce();
					throw cmdException;
				}
				final PrologTerm arg1 = ((CompoundPrologTerm) elem)
						.getArgument(1);
				final String name = PrologTerm.atomicString(arg1);
				result[i] = new Section(sectionType, name);
			}
			return result;
		} else {
			CommandException cmdException = new CommandException(
					"Expected section list, but received a term that is not a list");
			cmdException.notifyUserOnce();
			throw cmdException;
		}
	}

	private static TypedIdentifier[] createTypedIdentifiers(
			final ISimplifiedROMap<String, PrologTerm> binding, final String var)
			throws CommandException {
		TypedIdentifier[] result;
		final PrologTerm term = getNotNullTerm(binding, var);
		if (term.isList()) {
			result = TypedIdentifierGenerator.extract((ListPrologTerm) term);
		} else {
			CommandException cmdException = new CommandException(
					"Expected list in Prolog variable " + var + ", but was: "
							+ term.toString());
			cmdException.notifyUserOnce();
			throw cmdException;
		}
		return result;
	}

	private static PrologTerm getNotNullTerm(
			final ISimplifiedROMap<String, PrologTerm> binding, final String var)
			throws CommandException {
		PrologTerm term = binding.get(var);
		if (term == null) {
			CommandException cmdException = new CommandException(
					"Prolog variable " + var + " has no associated value");
			cmdException.notifyUserOnce();
			throw cmdException;
		}
		return term;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		Section[] sections;
		TypedIdentifier[] setElements, constants, variables, operations;

		sections = getSections(bindings, "Sections");
		setElements = createTypedIdentifiers(bindings, "SetElements");
		constants = createTypedIdentifiers(bindings, "Constants");
		variables = createTypedIdentifiers(bindings, "Variables");
		operations = createTypedIdentifiers(bindings, "Operations");
		machineObjectsResult = new MachineObjectsResult(sections, setElements,
				constants, variables, operations);
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_machine_objects").printVariable("Sections")
				.printVariable("SetElements").printVariable("Constants")
				.printVariable("Variables").printVariable("Operations")
				.closeTerm();
	}

	public MachineObjectsResult getResult() {
		return machineObjectsResult;
	}
}
