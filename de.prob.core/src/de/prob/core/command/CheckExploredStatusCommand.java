/**
 * (c) 2009-2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

/**
 * Checks if ProB has explored a state, i.e. computed its outgoing
 * transitions. Older versions of probcli do not know this property and
 * answer <code>false</code>, i.e. that no state is explored.
 */
public final class CheckExploredStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String PROPERTY_NAME = "explored";

	public CheckExploredStatusCommand(final String stateId) {
		super(PROPERTY_NAME, stateId);
	}

	public static boolean isExplored(final Animator a, final String stateId)
			throws ProBException {
		return CheckBooleanPropertyCommand.isPropertyTrue(a, PROPERTY_NAME,
				stateId);
	}

	/**
	 * Checks several states with one query.
	 *
	 * @return for each state if it is explored
	 */
	public static List<Boolean> areExplored(final Animator a,
			final List<String> stateIds) throws ProBException {
		final List<CheckExploredStatusCommand> checks = new ArrayList<CheckExploredStatusCommand>();
		for (String stateId : stateIds)
			checks.add(new CheckExploredStatusCommand(stateId));
		a.execute(new ComposedCommand(checks));
		final List<Boolean> result = new ArrayList<Boolean>();
		for (CheckExploredStatusCommand check : checks)
			result.add(check.getResult());
		return result;
	}
	// state_property2(explored,StateId) :- visited_expression_id(StateId), \+ not_all_transitions_added(StateId).

}
