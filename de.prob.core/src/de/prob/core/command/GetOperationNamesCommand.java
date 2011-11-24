/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.Animator;
import de.prob.core.command.internal.GetAllOperationsNamesCommand;
import de.prob.core.command.internal.GetOperationParameterNames;
import de.prob.core.domainobjects.OperationInfo;
import de.prob.exceptions.ProBException;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetOperationNamesCommand {

	public static List<OperationInfo> getNames(final Animator animator)
			throws ProBException {
		List<OperationInfo> result = new ArrayList<OperationInfo>();

		GetAllOperationsNamesCommand namesCmd = new GetAllOperationsNamesCommand();
		animator.execute(namesCmd);

		animator.execute(namesCmd);
		for (PrologTerm prologTerm : namesCmd.getNamesTerm()) {

			String opName = ((CompoundPrologTerm) prologTerm).getFunctor(); // FIXME
																			// this
																			// looks
																			// pretty
																			// weird,
																			// what
																			// does
																			// probcli
																			// answers?

			GetOperationParameterNames cmd = new GetOperationParameterNames(
					opName);
			animator.execute(cmd);
			List<String> paramNames = cmd.getParameterNames();
			result.add(new OperationInfo(opName, paramNames));
		}

		return result;
	}

}
