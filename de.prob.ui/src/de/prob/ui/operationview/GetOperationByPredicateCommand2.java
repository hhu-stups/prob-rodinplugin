/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.core.Animator;
import de.prob.core.ProblemHandler;
import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.eval.PredicateEvalElement;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Command to execute an event that has not been enumerated by ProB, for further
 * information see ({@link #getOperation})
 * 
 * @author Jens Bendisposto
 * 
 */

// Kind of a hack for PROBPLUGIN-88
public final class GetOperationByPredicateCommand2 implements
		IComposableCommand {

	private static final String NEW_STATE_ID_VARIABLE = "NewStateID";
	private final PredicateEvalElement evalElement;
	private final String stateId;
	private final String name;
	private List<Operation> operation;
	private final int nrOfSolutions;

	private GetOperationByPredicateCommand2() {
		throw new UnsupportedOperationException("Do not call this constructor");
	}

	private GetOperationByPredicateCommand2(final String stateId,
			final String name, final String predicate, final int nrOfSolutions)
			throws CommandException {
		this.stateId = stateId;
		this.name = name;
		this.nrOfSolutions = nrOfSolutions;
		PredicateEvalElement parsedEvalElement = null;

		IParseResult result = FormulaFactory.getDefault().parsePredicate(
				predicate, null);
		Predicate parsedPredicate = result.getParsedPredicate();

		try {
			parsedEvalElement = PredicateEvalElement.fromRodin(parsedPredicate);
		} catch (BException e) {
			String message = "Fatal error when trying to parse " + predicate
					+ ". Execution of operation " + name + " aborted.";
			ProblemHandler.raiseCommandException(message);
			parsedEvalElement = null;
		} finally {
			evalElement = parsedEvalElement;
		}
	}

	/**
	 * Works like @see getOperations but returns a single solution
	 */
	public static Operation getOperation(final Animator a,
			final String stateId, final String name, final String predicate)
			throws ProBException, BException {

		List<Operation> operations = getOperations(a, stateId, name, predicate,
				1);

		return operations == null ? null : operations.get(0);
	}

	/**
	 * Tries to find a valid transition from the state <em>stateId</em>
	 * satisfying a B <em>predicate</em> . Returns either null if no event
	 * satisfying <em></em> was found by ProB or an Operation object that can be
	 * executed using ExecuteOperationCommand.
	 * 
	 * @param animator
	 *            - Animator Instance
	 * @param stateId
	 *            - The state in which the event should be fired
	 * @param name
	 *            - The event's name
	 * @param predicate
	 *            - Additional guarding predicate
	 * @param nrOfSolutions
	 *            - maximum number of solutions
	 * @return an Operation or null
	 * @throws BException
	 *             - if the B predicate contains errors
	 * @throws ProBException
	 *             - if something terrible happens ;-)
	 */
	public static List<Operation> getOperations(final Animator a,
			final String stateId, final String name, final String predicate,
			final int nrOfSolutions) throws ProBException, BException {

		GetOperationByPredicateCommand2 executeOperationCommand = new GetOperationByPredicateCommand2(
				stateId, name, predicate, nrOfSolutions);
		if (executeOperationCommand.evalElement != null) {
			a.execute(executeOperationCommand);
		}
		return executeOperationCommand.getOperation();
	}

	/**
	 * This method is called when the command is prepared for sending. The
	 * method is called by the Animator class, most likely it is not interesting
	 * for other classes.
	 * 
	 * @see de.prob.core.command.IComposableCommand#writeCommand(de.prob.prolog.output.IPrologTermOutput)
	 */
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("execute_custom_operations").printAtomOrNumber(stateId)
				.printAtom(name);
		final ASTProlog prolog = new ASTProlog(pto, null);
		evalElement.getPrologAst().apply(prolog);
		pto.printNumber(nrOfSolutions);
		pto.printVariable(NEW_STATE_ID_VARIABLE);
		pto.printVariable("Errors").closeTerm();
	}

	/**
	 * This method is called to extract relevant information from ProB's answer.
	 * The method is called by the Animator class, most likely it is not
	 * interesting for other classes.
	 * 
	 * @see de.prob.core.command.IComposableCommand#writeCommand(de.prob.prolog.output.IPrologTermOutput)
	 */
	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {

		ListPrologTerm list = (ListPrologTerm) bindings
				.get(NEW_STATE_ID_VARIABLE);

		if (list.isEmpty()) {
			operation = null;
		} else {
			ArrayList<Operation> result = new ArrayList<Operation>();
			for (PrologTerm prologTerm : list) {
				Operation op = Operation.fromPrologTerm(prologTerm);
				result.add(op);
			}
			operation = result;
		}
	}

	private List<Operation> getOperation() {
		return operation;
	}

}
