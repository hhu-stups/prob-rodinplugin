/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;

import de.prob.core.Animator;
import de.prob.core.command.ComposedCommand;
import de.prob.core.command.GetStateValuesCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.Variable;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

/**
 * Describes operations without parameters that are enabled several times
 * (non-deterministic assignments) by the variables they change, e.g.
 * "x:=1, y:={2,3}".
 */
public final class NondeterministicChoices {

	private NondeterministicChoices() {
	}

	/**
	 * @return true if the operation has no parameters, so it can only be told
	 *         apart from other occurrences by its effect
	 */
	public static boolean isWithoutParameters(final Operation op) {
		return op.getArguments() == null || op.getArguments().isEmpty();
	}

	/**
	 * Computes descriptions of the operations without parameters in
	 * <code>operations</code>. Asks ProB for the values of the source and
	 * destination states with a single query.
	 *
	 * @param valueLength
	 *            maximum length of a single assignment
	 * @param totalLength
	 *            maximum length of a whole description
	 * @return a description for each operation without parameters; empty if
	 *         ProB could not provide the values
	 */
	public static Map<Operation, String> describe(final Animator animator,
			final Collection<Operation> operations, final int valueLength,
			final int totalLength) {
		final List<Operation> choices = new ArrayList<Operation>();
		final Set<String> stateIds = new HashSet<String>();
		for (Operation op : operations) {
			if (isWithoutParameters(op) && op.getSource() != null
					&& op.getDestination() != null) {
				choices.add(op);
				stateIds.add(op.getSource());
				stateIds.add(op.getDestination());
			}
		}
		final Map<Operation, String> result = new HashMap<Operation, String>();
		if (choices.isEmpty())
			return result;

		final Map<String, List<Variable>> values;
		try {
			values = getValues(animator, stateIds);
		} catch (ProBException e) {
			Logger.log(IStatus.WARNING,
					"Could not get the values of the successor states", e);
			return result;
		}

		for (Operation op : choices) {
			final List<Variable> before = values.get(op.getSource());
			final List<Variable> after = values.get(op.getDestination());
			if (before != null && after != null) {
				result.put(op,
						describe(before, after, valueLength, totalLength));
			}
		}
		return result;
	}

	private static Map<String, List<Variable>> getValues(
			final Animator animator, final Collection<String> stateIds)
			throws ProBException {
		final Map<String, GetStateValuesCommand> commands = new LinkedHashMap<String, GetStateValuesCommand>();
		for (String id : stateIds) {
			commands.put(id, new GetStateValuesCommand(id));
		}
		animator.execute(new ComposedCommand(new ArrayList<GetStateValuesCommand>(
				commands.values())));
		final Map<String, List<Variable>> values = new HashMap<String, List<Variable>>();
		for (Map.Entry<String, GetStateValuesCommand> e : commands.entrySet()) {
			values.put(e.getKey(), e.getValue().getResult());
		}
		return values;
	}

	/**
	 * Describes the variables whose values differ between the two states, in
	 * the order ProB returns them.
	 */
	static String describe(final List<Variable> before,
			final List<Variable> after, final int valueLength,
			final int totalLength) {
		final Map<String, String> old = new HashMap<String, String>();
		for (Variable v : before) {
			old.put(v.getIdentifier(), v.getValue());
		}
		final StringBuilder sb = new StringBuilder();
		for (Variable v : after) {
			final String value = v.getValue();
			if (old.containsKey(v.getIdentifier())
					&& equal(value, old.get(v.getIdentifier())))
				continue;
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(truncate(v.getIdentifier() + ":="
					+ (value == null ? "?" : value), valueLength));
			if (sb.length() > totalLength)
				break;
		}
		if (sb.length() == 0)
			return "no change";
		if (sb.length() > totalLength)
			return sb.substring(0, totalLength) + " [...]";
		return sb.toString();
	}

	private static boolean equal(final String a, final String b) {
		return a == null ? b == null : a.equals(b);
	}

	private static String truncate(final String s, final int length) {
		return s.length() > length ? s.substring(0, Math.max(0, length - 3))
				+ "..." : s;
	}

	/**
	 * Makes equal labels unique by appending " (#n)", n counting from 1 in
	 * the order of <code>labels</code>
	 */
	public static void makeUnique(final List<String> labels) {
		final Map<String, Integer> count = new HashMap<String, Integer>();
		for (String l : labels) {
			final Integer c = count.get(l);
			count.put(l, c == null ? 1 : c + 1);
		}
		for (int i = 0; i < labels.size(); i++) {
			final String l = labels.get(i);
			if (count.get(l) > 1)
				labels.set(i, l + " (#" + (i + 1) + ")");
		}
	}
}
