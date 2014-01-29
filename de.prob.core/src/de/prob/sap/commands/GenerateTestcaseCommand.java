/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.sap.commands;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.basis.MachineRoot;

import de.prob.core.Animator;
import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.sap.exceptions.ParseProblemException;
import de.prob.sap.util.FormulaUtils;

/**
 * A command to generate test cases for the choreography model.
 * 
 * @author plagge
 */
public class GenerateTestcaseCommand implements IComposableCommand {

	private static final String UNCOVERED_EVENTS = "UncoveredEvents";
	private static final String NUMBER_TESTCASES = "NrTestcases";

	private final Collection<String> operationNames;
	private final Predicate targetPredicate;
	private final int maxDepth;
	private final int maxNodes;
	private final String filename;

	private GlobalTestcaseResult result;

	public GenerateTestcaseCommand(final Collection<String> operationNames,
			final Predicate targetPredicate, final int maxDepth,
			final int maxNodes, final String filename) {
		this.operationNames = operationNames;
		this.targetPredicate = targetPredicate;
		this.maxDepth = maxDepth;
		this.maxNodes = maxNodes;
		this.filename = filename;
	}

	/**
	 * Like {@link #generateTestcases(Collection, Predicate, int, int, String)},
	 * but the predicate is given as a string that will be parsed before
	 * generating tests. A reference to {@link MachineRoot} is needed to parse
	 * and type-check the predicate.
	 * 
	 * @see #generateTestcases(Collection, Predicate, int, int, String)
	 */
	public static GlobalTestcaseResult generateTestcases(
			final IMachineRoot machineRoot,
			final Collection<String> operationNames,
			final String targetPredicate, final int maxDepth,
			final int maxNodes, final String filename) throws CoreException,
			ProBException {
		final Predicate predicate = parsePredicate(machineRoot, targetPredicate);
		return generateTestcases(operationNames, predicate, maxDepth, maxNodes,
				filename);
	}

	/**
	 * Generate test cases and write them into an XML file. A machine should
	 * have been already loaded by the animator (see
	 * {@link LoadEventBModelCommand}). Each generated test case covers one of
	 * the specified events and ends in a state that satisfies the given
	 * predicate.
	 * 
	 * @param events
	 *            a list of event names for which test cases should be generated
	 * @param predicate
	 *            a predicate that describes a valid end state where a test case
	 *            might stop
	 * @param maxDepth
	 *            the maximum length of the test cases
	 * @param maxNodes
	 *            the maximum number of new states that will be explored while
	 *            searching for test cases.
	 * @param filename
	 *            the file name of the XML file that will be generated and
	 *            contain the generated test cases, never <code>null</code>.
	 * @return an object to summarize the test case generation
	 * @throws ProBException
	 */
	private static GlobalTestcaseResult generateTestcases(
			final Collection<String> events, final Predicate predicate,
			final int maxDepth, final int maxNodes, final String filename)
			throws ProBException {
		final Animator animator = Animator.getAnimator();
		GenerateTestcaseCommand command = new GenerateTestcaseCommand(events,
				predicate, maxDepth, maxNodes, filename);
		animator.execute(command);
		return command.result;
	}

	private static Predicate parsePredicate(final IMachineRoot machineRoot,
			final String targetPredicate) throws ParseProblemException,
			CoreException {
		final FormulaFactory formfact = FormulaFactory.getDefault();
		final IParseResult parsedPredicate = formfact.parsePredicate(
				targetPredicate, null);
		if (parsedPredicate.hasProblem())
			throw new ParseProblemException(parsedPredicate.getProblems());
		final Predicate predicate = parsedPredicate.getParsedPredicate();

		final ITypeEnvironment typeEnv = machineRoot.getSCMachineRoot()
				.getTypeEnvironment();
		final ITypeCheckResult tcr = predicate.typeCheck(typeEnv);
		if (tcr.hasProblem())
			throw new ParseProblemException(tcr.getProblems());
		return predicate;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final IntegerPrologTerm pNumberTests = (IntegerPrologTerm) bindings
				.get(NUMBER_TESTCASES);
		final int numberOfTests = pNumberTests.getValue().intValue();

		final ListPrologTerm pEvents = (ListPrologTerm) bindings
				.get(UNCOVERED_EVENTS);
		Collection<String> uncovered = new ArrayList<String>(pEvents.size());
		for (PrologTerm term : pEvents) {
			final String event = PrologTerm.atomicString(term);
			uncovered.add(event);
		}

		result = new GlobalTestcaseResult(numberOfTests, uncovered);
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("sap_generate_testcases");

		// print operation names
		pto.openList();
		for (String op : operationNames) {
			pto.printAtom(op);
		}
		pto.closeList();

		// print the predicate
		FormulaUtils.printPredicate(targetPredicate, pto);

		// print limits
		pto.printNumber(maxDepth).printNumber(maxNodes).printAtom(filename);
		// print variables for return values
		pto.printVariable(NUMBER_TESTCASES).printVariable(UNCOVERED_EVENTS);

		pto.closeTerm();
	}

}
